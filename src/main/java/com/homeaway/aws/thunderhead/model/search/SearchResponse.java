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

package com.homeaway.aws.thunderhead.model.search;

import com.google.common.base.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author jmonette
 */
@XmlType(name = "SearchResponse")
@XmlRootElement(name = "results")
public class SearchResponse {

    /** How to rank the query results */
    @XmlElement(name = "rank")
    private String rank;

    /** What expression was being matched */
    @XmlElement(name = "match-expr")
    private String matchExpr;

    /** The SearchHits object representing the documents found */
    @XmlElement(name = "hits")
    private SearchHits found;

    /** The SearchInfo object representing some statistics from querying */
    @XmlElement(name = "info")
    private SearchInfo info;

    /****************************** */
    /*    Getters and Setters       */
    /****************************** */
    public String getRank() {
        return rank;
    }

    public void setRank(final String rank) {
        this.rank = rank;
    }

    public String getMatchExpr() {
        return matchExpr;
    }

    public void setMatchExpr(final String matchExpr) {
        this.matchExpr = matchExpr;
    }

    public SearchHits getFound() {
        return found;
    }

    public void setFound(final SearchHits found) {
        this.found = found;
    }

    public SearchInfo getInfo() {
        return info;
    }

    public void setInfo(final SearchInfo info) {
        this.info = info;
    }

    /**
     * The hashcode representing the SearchResponse object
     *
     * @return the hashcode representing the SearchResponse object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(rank,
                                matchExpr,
                                found,
                                info);
    }

    /**
     * Equals method for the SearchResponse object
     *
     * @param obj object to compare if this object is equal to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || !(obj instanceof SearchResponse)) {return false;}

        final SearchResponse that = (SearchResponse) obj;
        return Objects.equal(this.rank, that.rank)
            && Objects.equal(this.matchExpr, that.matchExpr)
            && Objects.equal(this.found, that.found)
            && Objects.equal(this.info, that.info);
    }

    /**
     * String representation of the SearchResponse object
     *
     * @return String representation of the SearchResponse object
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("rank", rank)
                      .add("match-expr", matchExpr)
                      .add("found", found)
                      .add("info", info)
                      .toString();
    }
}
