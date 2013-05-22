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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.homeaway.aws.thunderhead.model.enums.CloudSearchQueryParam;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;

/**
 * @author jmonette
 * @author svanderworth
 */
public final class CloudSearchRequest {

    protected static final Logger logger = LoggerFactory.getLogger(CloudSearchRequest.class);

    private CloudSearchQuery booleanQuery;
    private String query;
    private String rank;
    private Map<String, String> rankExpressions = Maps.newHashMap();
    private List<String> returnFields = Lists.newLinkedList();
    private Integer size;
    private Integer start;

    private CloudSearchRequest() { }

    public static CloudSearchRequest newInstance(){
        return new CloudSearchRequest();
    }

    public CloudSearchRequest bq(final CloudSearchQuery query) {
        if (this.query != null) {
            logger.warn("Setting bq when q is already set!");
        }
        this.booleanQuery = query;
        return this;
    }

    public CloudSearchRequest q(final String query) {
        if (this.booleanQuery != null) {
            logger.warn("Setting q when bq is already set!");
        }
        this.query = query;
        return this;
    }

    public CloudSearchRequest rank(final String rank) {
        this.rank = rank;
        return this;
    }

    public CloudSearchRequest rankExpression(final String rankName, final String rankExpression) {
        this.rankExpressions.put(rankName, rankExpression);
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

    public Map<String, String> buildQueryParams() {
        Map<String, String> queryParams = Maps.newHashMap();

        if (booleanQuery != null) {
            queryParams.put(CloudSearchQueryParam.BQ.getName(), booleanQuery.getQueryString());
        }

        if (query != null) {
            queryParams.put(CloudSearchQueryParam.Q.getName(), query);
        }

        if (rank != null) {
            queryParams.put(CloudSearchQueryParam.RANK.getName(), rank);
        }

        if (!rankExpressions.isEmpty()) {
            for(Map.Entry<String, String> entry : rankExpressions.entrySet()) {
                queryParams.put("rank-" + entry.getKey(), entry.getValue());
            }
        }

        if (!returnFields.isEmpty()) {
            queryParams.put(CloudSearchQueryParam.RETURN_FIELDS.getName(), StringUtils.join(returnFields, ','));
        }

        if (size != null) {
            queryParams.put(CloudSearchQueryParam.SIZE.getName(), size.toString());
        }

        if (start != null) {
            queryParams.put(CloudSearchQueryParam.START.getName(), start.toString());
        }

        return queryParams;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("bq", booleanQuery)
                .add("q",query)
                .add("rank", rank)
                .add("rank-expressions", rankExpressions)
                .add("return-fields", returnFields)
                .add("size", size)
                .add("start", start)
                .toString();
    }
}
