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

package com.homeaway.aws.thunderhead.client;

import com.homeaway.aws.thunderhead.model.enums.CloudSearchPath;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchClientException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchRuntimeException;
import com.homeaway.aws.thunderhead.model.sdf.SearchDocumentFormat;
import com.homeaway.aws.thunderhead.model.upload.UploadResponse;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.perf4j.aop.Profiled;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * This class is a client for uploading SDF documents to the cloudsearch domain and querying the cloudsearch domain
 *
 * @author jmonette
 */
public class CloudSearchReadWriteClient extends CloudSearchReadClient {

    /**
     * Default constructor. If this constructor is used then setUpdateWebResource and setQueryWebResource must be called to
     * set the WebResources needed by the client
     */
    public CloudSearchReadWriteClient() {
        super();
    }

    /**
     * The WebResource to update the cloudsearch domain with
     *
     * @param updateWebResource the WebResource to update the cloudsearch domain with
     */
    @Override
    public void setUpdateWebResource(final WebResource updateWebResource) {
        this.updateWebResource = updateWebResource;
    }

    /**
     * The WebResource to query the cloudsearch domain with
     *
     * @param queryWebResource the WebResource to query the cloudsearch domain with
     */
    @Override
    public void setQueryWebResource(final WebResource queryWebResource) {
        this.queryWebResource = queryWebResource;
    }

    /**
     * This method will post the SDF to the Amazon. The SDF can have both adds and delete requests
     *
     * @param entity the SDF entity
     * @return returns a UploadResponse object which represents the output returned from Amazon
     * @throws com.homeaway.aws.thunderhead.model.exceptions.CloudSearchClientException if the response did not return a 2XX status code
     */
    @Profiled(tag = "CloudSearchReadWriteClient.updateDomain")
    public UploadResponse updateDomain(SearchDocumentFormat entity) throws CloudSearchClientException {
        ClientResponse clientResponse = null;
        UploadResponse uploadResponse = null;

        LOGGER.debug("POSTing to {} with document: {}", this.updateWebResource.getURI(), entity);
        try {
            clientResponse = this.updateWebResource.uri(UriBuilder.fromUri(this.updateWebResource.getURI()).build())
                                                                  .path(CLOUDSEARCH_VERSION)
                                                                  .path(CloudSearchPath.DOCUMENTS.getName())
                                                                  .path(CloudSearchPath.BATCH.getName())
                                                                  .accept(MediaType.APPLICATION_XML)
                                                                  .entity(entity, MediaType.APPLICATION_XML)
                                                                  .post(ClientResponse.class);

            LOGGER.debug("Received a status of {} for query to {}", clientResponse.getStatus(), this.updateWebResource.getURI());

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
}
