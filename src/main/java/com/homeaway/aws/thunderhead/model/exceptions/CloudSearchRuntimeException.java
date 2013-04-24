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

package com.homeaway.aws.thunderhead.model.exceptions;

import com.google.common.base.Objects;

import java.util.UUID;

/**
 * @author jmonette
 */
public class CloudSearchRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -5975214219537786082L;
    private UUID errorId;
    
    public CloudSearchRuntimeException(String message, Throwable cause) {
        super(message, cause);
        this.errorId = UUID.randomUUID();
    }
    
    public UUID getErrorId() {
        return this.errorId;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("errorId", String.valueOf(getErrorId()))
                      .add("message", String.valueOf(getMessage()))
                      .add("cause", String.valueOf(getCause()))
                      .toString();
    }
}
