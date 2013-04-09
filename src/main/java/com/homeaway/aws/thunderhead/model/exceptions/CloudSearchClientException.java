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

import com.google.common.base.Objects;

import java.util.UUID;

/**
 * A general cloud search exception representing the errors returned from Amazon
 * 
 * @author jmonette
 */
public class CloudSearchClientException extends Exception {
    
    private static final long serialVersionUID = 954670374490397602L;
    private UUID errorId;
    private Integer cloudSearchStatusCode;
    private String entity;
    
    public CloudSearchClientException(Integer cloudSearchStatusCode, String message, String entity) {
        super(message);
        this.errorId = UUID.randomUUID();
        this.cloudSearchStatusCode = cloudSearchStatusCode;
        this.entity = entity;
    }

    /**
     * The error id assigned to this exception
     * 
     * @return the error id assigned to this exception
     */
    public UUID getErrorId() {
        return this.errorId;
    }

    /**
     * Http status code in the Amazon error
     *
     * @return the http status code return from Amazon
     */
    public Integer getCloudSearchStatusCode() {
        return this.cloudSearchStatusCode;
    }
    
    @Override
    public String getMessage() {
        return toString();
    }

    /**
     * String representation of the CloudSearchException
     * 
     * @return string representation of the CloudSearchException
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("errorId", String.valueOf(getErrorId()))
                      .add("message", String.valueOf(super.getMessage()))
                      .add("cloudSearchStatusCod", String.valueOf(this.getCloudSearchStatusCode()))
                      .add("entity", String.valueOf(entity))
                      .toString();
    }
}
