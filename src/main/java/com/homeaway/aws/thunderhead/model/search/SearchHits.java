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

package com.homeaway.aws.thunderhead.model.search;

import com.google.common.base.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * A Java object representation of a list of search hits from querying the cloud search domain
 * 
 * @author jmonette
 */
@XmlType(name = "SearchHits")
@XmlRootElement(name = "hits")
public class SearchHits {

    /** The number of documents found */
    @XmlAttribute(name = "found")
    private int count;

    /** The starting index */
    @XmlAttribute(name = "start")
    private int start;

    /** A list of SearchHit object representing documents found */
    @XmlElement(name = "hit")
    private List<SearchHit> hits;

    /****************************** */
    /*    Getters and Setters       */
    /****************************** */
    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(final int start) {
        this.start = start;
    }

    public List<SearchHit> getHits() {
        return hits;
    }

    public void setHits(final List<SearchHit> hits) {
        this.hits = hits;
    }

    /**
     * The hashcode representing the SearchHits object
     *
     * @return the hashcode representing the SearchHits object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(count,
                                start,
                                hits);
    }

    /**
     * Equals method for the SearchHits object
     *
     * @param obj object to compare if this object is equal to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || !(obj instanceof SearchHits)) {return false;}

        final SearchHits that = (SearchHits) obj;
        return Objects.equal(this.count, that.count)
            && Objects.equal(this.start, that.start)
            && Objects.equal(this.hits, that.hits);
    }

    /**
     * String representation of the SearchHits object
     *
     * @return String representation of the SearchHits object
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("count", count)
                      .add("start", start)
                      .add("hits", hits)
                      .toString();
    }
}
