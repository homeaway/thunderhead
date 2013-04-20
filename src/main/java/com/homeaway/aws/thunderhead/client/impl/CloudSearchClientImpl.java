/* Copyright (c) 2010 HomeAway, Inc.
 * All rights reserved.  http://jmonette/github.io/thunderhead
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
import com.homeaway.aws.thunderhead.model.enums.CloudSearchPath;
import com.homeaway.aws.thunderhead.model.enums.CloudSearchQueryParam;
import com.homeaway.aws.thunderhead.model.enums.CloudSearchStatusCode;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchBadRequestException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchBandwidthExceededException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchClientException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchForbiddenException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchGenericException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchInternalException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchInvalidCharacterSetException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchLengthRequiredException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchMethodNotAllowedException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchNotAcceptException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchNotFoundException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchRequestTimeoutException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchRequestTooLargeException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchRuntimeException;
import com.homeaway.aws.thunderhead.model.sdf.SearchDocumentFormat;
import com.homeaway.aws.thunderhead.model.search.SearchResponse;
import com.homeaway.aws.thunderhead.model.upload.UploadResponse;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author jmonette
 */
public class CloudSearchClientImpl implements CloudSearchClient {

    /** The AWS cloud search version that the client uses */
    public static final String CLOUDSEARCH_VERSION = "2011-02-01";

    /** The logger used for logging */
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudSearchClientImpl.class);


    private WebResource queryWebResource;
    private WebResource updateWebResource;

    private String searchHost;
    private String updateHost;
    private boolean searchSecure;
    private boolean updateSecure;
    private int connectTimeout;
    private int readTimeout;

    public CloudSearchClientImpl(String searchHost, String updateHost,
                                 boolean searchSecure, boolean updateSecure,
                                 int connectTimeout, int readTimeout) {
        this.searchHost = searchHost;
        this.updateHost = updateHost;
        this.searchSecure = searchSecure;
        this.updateSecure = updateSecure;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    @Profiled(tag = "CloudSearchClientImpl.query")
    public SearchResponse query(MultivaluedMap<String, String> queryParams) throws CloudSearchClientException {
        ClientResponse clientResponse = null;
        SearchResponse searchResponse = null;

        /* Force request xml results from AWS cloudsearch */
        MultivaluedMap<String, String> myQueryParams = new MultivaluedMapImpl(queryParams);
        myQueryParams.remove(CloudSearchQueryParam.RESULTS_TYPE.getName());
        myQueryParams.add(CloudSearchQueryParam.RESULTS_TYPE.getName(), "xml");

        LOGGER.debug("Querying to {} with query params: {}", this.queryWebResource.getURI(), myQueryParams);
        try {
            clientResponse = this.queryWebResource
                                 .path(CLOUDSEARCH_VERSION)
                                 .path(CloudSearchPath.SEARCH.getName())
                                 .queryParams(myQueryParams)
                                 .get(ClientResponse.class);


            LOGGER.debug("Received a status of {} for query to {}", clientResponse.getStatus(), this.queryWebResource.getURI());
            checkStatus(clientResponse);

            searchResponse = clientResponse.getEntity(SearchResponse.class);
        } catch (RuntimeException re) {
            throw new CloudSearchRuntimeException(re.getMessage(), re);
        } finally {
            if (clientResponse != null) {
                clientResponse.close();
            }
        }

        return searchResponse;
    }

    @Override
    @Profiled(tag = "CloudSearchClientImpl.update")
    public UploadResponse update(SearchDocumentFormat sdf) throws CloudSearchClientException {
        ClientResponse clientResponse = null;
        UploadResponse uploadResponse = null;

        LOGGER.debug("POSTing to {} with document: {}", this.updateWebResource.getURI(), sdf);
        try {
            clientResponse = this.updateWebResource
                                 .path(CLOUDSEARCH_VERSION)
                                 .path(CloudSearchPath.DOCUMENTS.getName())
                                 .path(CloudSearchPath.BATCH.getName())
                                 .accept(MediaType.APPLICATION_XML)
                                 .entity(sdf, MediaType.APPLICATION_XML)
                                 .post(ClientResponse.class);

            LOGGER.debug("Received a status of {} for query to {}", clientResponse.getStatus(), this.updateWebResource.getURI());

            checkStatus(clientResponse);

            uploadResponse = clientResponse.getEntity(UploadResponse.class);
        } catch (RuntimeException re) {
            throw new CloudSearchRuntimeException(re.getMessage(), re);
        } finally {
            if (clientResponse != null) {
                clientResponse.close();
            }
        }

        return uploadResponse;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public String getSearchHost() {
        return this.searchHost;
    }

    public String getUpdateHost() {
        return this.updateHost;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public boolean isSearchSecure() {
        return this.searchSecure;
    }

    public boolean isUpdateSecure() {
        return this.updateSecure;
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
