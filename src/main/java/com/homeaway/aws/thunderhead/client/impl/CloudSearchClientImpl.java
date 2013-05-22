/* Copyright (c) 2010 HomeAway, Inc.
 * All rights reserved.  http://homeaway.github.io/thunderhead
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.homeaway.aws.thunderhead.client.impl;

import com.homeaway.aws.thunderhead.client.CloudSearchClient;
import com.homeaway.aws.thunderhead.model.builder.CloudSearchRequest;
import com.homeaway.aws.thunderhead.model.enums.CloudSearchQueryParam;
import com.homeaway.aws.thunderhead.model.enums.CloudSearchStatusCode;
import com.homeaway.aws.thunderhead.model.exceptions.*;
import com.homeaway.aws.thunderhead.model.sdf.SearchDocumentFormat;
import com.homeaway.aws.thunderhead.model.search.SearchResponse;
import com.homeaway.aws.thunderhead.model.upload.UploadResponse;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.perf4j.aop.Profiled;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

/**
 * @author jmonette
 */
public class CloudSearchClientImpl implements CloudSearchClient {

    private WebResource queryWebResource;
    private WebResource updateWebResource;

    public CloudSearchClientImpl(WebResource queryWebResource, WebResource updateWebResource) {
        this.queryWebResource = queryWebResource;
        this.updateWebResource = updateWebResource;
    }

    public WebResource getQueryWebResource() {
        return queryWebResource;
    }

    public WebResource getUpdateWebResource() {
        return updateWebResource;
    }

    /**
     * This method queries Amazon and returns the results found
     *
     * @param cloudSearchRequest a CloudSearchRequest object representing the search request to cloudsearch.
     * @return a SearchResponse object which represents query results
     * @throws CloudSearchClientException if the response did not return a 2XX status code
     */
    @Override
    @Profiled(tag = "CloudSearchReadClient.query")
    public SearchResponse query(CloudSearchRequest cloudSearchRequest) throws CloudSearchClientException {
        if (this.queryWebResource == null) {
            throw new IllegalStateException("CloudSearchClient not configured for querying cloudsearch");
        }

        ClientResponse clientResponse = null;
        SearchResponse searchResponse = null;

        /* Force request xml results from AWS cloudsearch */
        WebResource webResource = this.queryWebResource.path(CLOUDSEARCH_VERSION)
                                                       .path("search")
                                                       .queryParam(CloudSearchQueryParam.RESULTS_TYPE.getName(), "xml");

        for (Map.Entry<String, String> entry : cloudSearchRequest.buildQueryParams().entrySet()) {
            webResource = webResource.queryParam(entry.getKey(), entry.getValue());
        }

        LOGGER.debug("Querying to {} with query params: {}", this.queryWebResource.getURI(), cloudSearchRequest);
        try {

            clientResponse = webResource.get(ClientResponse.class);

            LOGGER.debug("Received a status of {} for query to {}", clientResponse.getStatus(), this.queryWebResource.getURI());
            checkStatus(clientResponse);

            searchResponse = clientResponse.getEntity(SearchResponse.class);
        } catch(RuntimeException re) {
            throw new CloudSearchRuntimeException(re.getMessage(), re);
        } finally {
            if (clientResponse != null) {
                clientResponse.close();
            }
        }

        return searchResponse;
    }

    /**
     * This method will post the SDF to the Amazon. The SDF can have both adds and delete requests
     *
     * @param entity the SDF entity
     * @return returns a UploadResponse object which represents the output returned from Amazon
     * @throws CloudSearchClientException if the response did not return a 2XX status code
     */
    @Override
    @Profiled(tag = "CloudSearchReadWriteClient.updateDomain")
    public UploadResponse updateDomain(SearchDocumentFormat entity) throws CloudSearchClientException {
        if (this.updateWebResource == null) {
            throw new IllegalStateException("CloudSearchClient not configured for updating cloudsearch");
        }

        ClientResponse clientResponse = null;
        UploadResponse uploadResponse = null;

        LOGGER.debug("POSTing to {} with document: {}", this.updateWebResource.getURI(), entity);
        try {
            clientResponse = this.updateWebResource.path(CLOUDSEARCH_VERSION)
                                                   .path("documents")
                                                   .path("batch")
                                                   .accept(MediaType.APPLICATION_XML)
                                                   .entity(entity, MediaType.APPLICATION_XML)
                                                   .post(ClientResponse.class);

            LOGGER.debug("Received a status of {} for POST to {}", clientResponse.getStatus(), this.updateWebResource.getURI());

            checkStatus(clientResponse);

            uploadResponse = clientResponse.getEntity(UploadResponse.class);
        } catch(RuntimeException re) {
            throw new CloudSearchRuntimeException(re.getMessage(), re);
        } finally {
            if(clientResponse != null) {
                clientResponse.close();
            }
        }

        return uploadResponse;
    }

    /**
     * Checks the status of the response and throws exceptions accordingly
     *
     * @param response the response to check the status and build exceptions from
     * @throws CloudSearchClientException if the response did not contain a 2XX status
     */
    private void checkStatus(ClientResponse response) throws CloudSearchClientException {
        int status = response.getStatus();
        if (isSuccessful(status)) {
            return;
        }

        String entity = response.getEntity(String.class);

        switch (CloudSearchStatusCode.fromStatus(status)) {
            case BAD_REQUEST: throw new CloudSearchBadRequestException(status, CloudSearchStatusCode.BAD_REQUEST.getErrorString(), entity);
            case FORBIDDEN: throw new CloudSearchForbiddenException(status, CloudSearchStatusCode.FORBIDDEN.getErrorString(), entity);
            case NOT_FOUND: throw new CloudSearchNotFoundException(status, CloudSearchStatusCode.NOT_FOUND.getErrorString(), entity);
            case METHOD_NOT_ALLOWED: throw new CloudSearchMethodNotAllowedException(status, CloudSearchStatusCode.METHOD_NOT_ALLOWED.getErrorString(), entity);
            case NOT_ACCEPTABLE: throw new CloudSearchNotAcceptException(status, CloudSearchStatusCode.NOT_ACCEPTABLE.getErrorString(), entity);
            case REQUEST_TIMEOUT: throw new CloudSearchRequestTimeoutException(status, CloudSearchStatusCode.REQUEST_TIMEOUT.getErrorString(), entity);
            case LENGTH_REQUIRED: throw new CloudSearchLengthRequiredException(status, CloudSearchStatusCode.LENGTH_REQUIRED.getErrorString(), entity);
            case REQUEST_TOO_LONG: throw new CloudSearchRequestTooLargeException(status, CloudSearchStatusCode.REQUEST_TOO_LONG.getErrorString(), entity);
            case INVALID_CHARACTER_SET: throw new CloudSearchInvalidCharacterSetException(status, CloudSearchStatusCode.INVALID_CHARACTER_SET.getErrorString(), entity);
            case INTERNAL_SERVER_ERROR: throw new CloudSearchInternalException(status, CloudSearchStatusCode.INTERNAL_SERVER_ERROR.getErrorString(), entity);
            case BANDWIDTH_LIMIT_EXCEEDED: throw new CloudSearchBandwidthExceededException(status, CloudSearchStatusCode.BANDWIDTH_LIMIT_EXCEEDED.getErrorString(), entity);
            default: throw new CloudSearchGenericException("Unknown Amazon CloudSearch status code: " + status, entity);
        }
    }

    /**
     * Determines whether or not the http request was successful
     *
     * @param status the status for the http response
     * @return true if the http request was successful
     */
    private boolean isSuccessful(int status) {
        return (status >= 200 && status < 400);
    }

}
