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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author jmonette
 */
public final class CloudSearchRequest {

    private CloudSearchQuery cloudSearchQuery;
    private List<String> returnFields = Lists.newLinkedList();
    private String resultsType;
    private String rank;
    private Integer size;
    private Integer start;

    private CloudSearchRequest() { }

    public static CloudSearchRequest newInstance(){
        return new CloudSearchRequest();
    }

    public CloudSearchRequest query(final CloudSearchQuery cloudSearchQuery) {
        this.cloudSearchQuery = cloudSearchQuery;
        return this;
    }

    public CloudSearchRequest rank(final String rank) {
        this.rank = rank;
        return this;
    }

    public CloudSearchRequest resultsType(final String resultsType) {
        this.resultsType = resultsType;
        return this;
    }

    public CloudSearchRequest returnField(String returnField) {
        this.returnFields.add(returnField);
        return this;
    }

    public CloudSearchRequest size(final Integer size) {
        this.size = size;
        return this;
    }

    public CloudSearchRequest start(final Integer start) {
        this.start = start;
        return this;
    }

    public Map<String, String> foo() {
        Map<String, String> queryParams = Maps.newHashMap();

        return queryParams;
    }
}
