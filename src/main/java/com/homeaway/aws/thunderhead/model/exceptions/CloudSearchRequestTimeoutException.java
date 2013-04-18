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

package com.homeaway.aws.thunderhead.model.exceptions;

/**
 * CloudSearchRequestTimeoutException - CloudSearch timed out in processing the request.
 *
 * @author jmonette
 */
public class CloudSearchRequestTimeoutException extends CloudSearchClientException {
    private static final long serialVersionUID = 888689010300861125L;

    /**
     * CloudSearchRequestTimeoutException constructor.
     *
     * @param cloudSearchStatusCode the status code return from CloudSearch.
     * @param message the message attached to the exception.
     * @param entity the request entity as a string.
     */
    public CloudSearchRequestTimeoutException(Integer cloudSearchStatusCode, String message, String entity) {
        super(cloudSearchStatusCode, message, entity);
    }
}
