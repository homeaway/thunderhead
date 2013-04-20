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

package com.homeaway.aws.thunderhead.model.builder;

/**
 * @author jmonette
 */
public final class CloudSearchQuery {

    private CloudSearchQuery() { }

    public static CloudSearchQuery newInstance() {
        return new CloudSearchQuery();
    }

    public CloudSearchQuery q() {
        return this;
    }

    public CloudSearchQuery bq() {
        return this;
    }

    public CloudSearchQuery and() {
        return this;
    }

    public CloudSearchQuery or() {
        return this;
    }

    @Override
    public String toString() {
        return "";
    }
}
