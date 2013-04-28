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
import com.homeaway.aws.thunderhead.model.enums.CloudSearchQueryParam;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchClientException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchRuntimeException;
import com.homeaway.aws.thunderhead.model.search.SearchResponse;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.perf4j.aop.Profiled;

import javax.ws.rs.core.MultivaluedMap;

/**
 * This class is a client for querying the cloudsearch domain
 * 
 * @author jmonette
 */
public class CloudSearchReadClient extends AbstractCloudSearchClient {

    /**
     * Default constructor. If this constructor is used then setUpdateWebResource and setQueryWebResource must be called to
     * set the WebResources needed by the client
     */
    public CloudSearchReadClient() {
        super();
    }

    /**
     * The WebResource to update the cloudsearch domain with
     *
     * @param updateWebResource the WebResource to update the cloudsearch domain with
     */
    @Override
    public void setUpdateWebResource(final WebResource updateWebResource) {
        throw new CloudSearchRuntimeException("Read client not allowed to update domain", new IllegalAccessException("Read client not allowed to update domain"));
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
     * This method queries Amazon and return the results found
     * 
     * @param queryParams a MultivaluedMap of the query params to use
     * @return a SearchResponse object which represents query results
     * @throws CloudSearchClientException if the response did not return a 2XX status code
     */
    @Profiled(tag = "CloudSearchReadClient.query")
    public SearchResponse query(MultivaluedMap<String, String> queryParams) throws CloudSearchClientException {
        ClientResponse clientResponse = null;
        SearchResponse searchResponse = null; 
        
        /* Force request xml results from AWS cloudsearch */
        MultivaluedMap<String, String> myQueryParams = new MultivaluedMapImpl(queryParams);
        myQueryParams.remove(CloudSearchQueryParam.RESULTS_TYPE.getName());
        myQueryParams.add(CloudSearchQueryParam.RESULTS_TYPE.getName(), "xml");
        
        LOGGER.debug("Querying to {} with query params: {}", this.queryWebResource.getURI(), myQueryParams);
        try {

            clientResponse = this.queryWebResource.path(CLOUDSEARCH_VERSION)
                                                  .path(CloudSearchPath.SEARCH.getName())
                                                  .queryParams(myQueryParams)
                                                  .get(ClientResponse.class);

            
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
}
