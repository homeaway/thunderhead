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
    private List<String> facetFields = Lists.newLinkedList();
    private MultivaluedMap<String, String> facetConstraints = new MultivaluedMapImpl();
    private Map<String, String> facetSorts = Maps.newHashMap();
    private Map<String, Integer> facetTopNs = Maps.newHashMap();
    private String query;
    private String rank;
    private Map<String, String> rankExpressions = Maps.newHashMap();
    private String resultsType;
    private List<String> returnFields = Lists.newLinkedList();
    private Integer size;
    private Integer start;
    private Map<String, String> tRanges = Maps.newHashMap();

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

    public CloudSearchRequest facet(final String facet){
        this.facetFields.add(facet);
        return this;
    }

    public CloudSearchRequest facetConstraints(final String field, final String constraint){
        this.facetConstraints.putSingle(field, constraint);
        return this;
    }

    //TODO sort is a string for now.  We could handle this with an enum of sort types but how do we cleanly handle a case with the hyphen syntax to reverse a sort?  e.g. -max(fieldname)
    public CloudSearchRequest facetSort(final String field, final String sort) {
        this.facetSorts.put(field, sort);
        return this;
    }

    public CloudSearchRequest facetTopN(final String field, final int n) {
        facetTopNs.put(field, n);
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

    //TODO what should this be named?  I can't figure out what the 't' is supposed to stand for from the documentation...
    //TODO range is a string for now; we might eventually want an actual Range object or take ints and handle the formatting internally
    public CloudSearchRequest tRange(final String field, final String range) {
        this.tRanges.put(field, range);
        return this;
    }

    public Map<String, String> buildQueryParams() {
        Map<String, String> queryParams = Maps.newHashMap();

        //todo do we want to validate that either q or bq is set?  One or the other is required, per the amazon docs.
        //if (booleanQuery == null && query == null){/*error?*/}

        if (booleanQuery != null) {
            queryParams.put(CloudSearchQueryParam.BQ.getName(), booleanQuery.getQueryString());
        }
        if (!facetFields.isEmpty()) {
            queryParams.put(CloudSearchQueryParam.FACET.getName(), StringUtils.join(facetFields, ','));
        }
        if (!facetConstraints.isEmpty()) {
            for(Map.Entry<String, List<String>> entry : facetConstraints.entrySet()) {
                //TODO should these custom param names be built in a nicer way?
                queryParams.put("facet-" + entry.getKey() + "-constraints", StringUtils.join(entry.getValue(), ','));
            }
        }
        if (!facetSorts.isEmpty()) {
            for(Map.Entry<String, String> entry : facetSorts.entrySet()) {
                queryParams.put("facet-" + entry.getKey() + "-sort", entry.getValue());
            }
        }
        if (!facetTopNs.isEmpty()) {
            for(Map.Entry<String, Integer> entry : facetTopNs.entrySet()) {
                queryParams.put("facet-" + entry.getKey() + "-top-n", entry.getValue().toString());
            }
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
        if (resultsType != null) {
            queryParams.put(CloudSearchQueryParam.RESULTS_TYPE.getName(), resultsType);
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
        if (!tRanges.isEmpty()) {
            for(Map.Entry<String, String> entry : tRanges.entrySet()) {
                queryParams.put(entry.getKey(), entry.getValue());
            }
        }

        return queryParams;
    }
}
