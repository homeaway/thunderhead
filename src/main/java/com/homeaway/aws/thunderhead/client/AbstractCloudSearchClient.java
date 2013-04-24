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

import com.homeaway.aws.thunderhead.model.enums.CloudSearchStatusCode;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchBadRequestException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchForbiddenException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchGenericException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchInternalException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchLengthRequiredException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchMethodNotAllowedException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchRequestTimeoutException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchRequestTooLargeException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchBandwidthExceededException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchClientException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchInvalidCharacterSetException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchNotAcceptException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchNotFoundException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract client that the other Cloudsearch clients inherit from
 *
 * @author jmonette
 */
public abstract class AbstractCloudSearchClient {

    /** The AWS cloud search version that the client uses */
    public static final String CLOUDSEARCH_VERSION = "2011-02-01";

    /** The logger used for logging */
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractCloudSearchClient.class);

    protected WebResource queryWebResource;
    protected WebResource updateWebResource;

    /**
     * Default constructor.
     */
    protected AbstractCloudSearchClient() {}

    /**
     * The WebResource to query the cloudsearch domain with
     *
     * @param queryWebResource the WebResource to query the cloudsearch domain with
     */
    public abstract void setQueryWebResource(final WebResource queryWebResource);

    /**
     * The WebResource to update the cloudsearch domain with
     *
     * @param updateWebResource the WebResource to update the cloudsearch domain with
     */
    public abstract void setUpdateWebResource(final WebResource updateWebResource);

    /**
     * Checks the status of the response and throws exceptions accordingly
     *
     * @param response the response to check the status and build exceptions from
     * @throws CloudSearchClientException if the response did not contain a 2XX status
     */
    protected void checkStatus(ClientResponse response) throws CloudSearchClientException {
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
