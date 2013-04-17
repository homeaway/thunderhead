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

package com.homeaway.aws.thunderhead.model.enums;

import com.google.common.base.Objects;

/**
 * This class defines constants for the query params used in cloudsearch
 *
 * @author jmonette
 */
public enum CloudSearchQueryParam {

    BQ("bq"),
    FACET("facet"),
    Q("q"),
    RANK("rank"),
    RESULTS_TYPE("results-type"),
    RETURN_FIELDS("return-fields"),
    SIZE("size"),
    START("start");

    private String queryParam;
    private CloudSearchQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }

    public String getName() {
        return this.queryParam;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("queryParam", this.queryParam)
                .toString();

    }

}
