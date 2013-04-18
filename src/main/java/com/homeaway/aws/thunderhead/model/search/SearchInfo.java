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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The SearchInfo model.
 *
 * @author jmonette
 */
@XmlType(name = "SearchInfo")
@XmlRootElement(name = "info")
public class SearchInfo {

    /** The rid of the response. */
    @XmlAttribute(name = "rid")
    private String rid;

    /** The time in milliseconds to query the cloudsearch domain. */
    @XmlAttribute(name = "time-ms")
    private String timeMs;

    /** The actual cpu time in milliseconds to query the cloudsearch domain. */
    @XmlAttribute(name = "cpu-time-ms")
    private String cpuTimeMs;

    public String getRid() {
        return rid;
    }

    public void setRid(final String rid) {
        this.rid = rid;
    }

    public String getTimeMs() {
        return timeMs;
    }

    public void setTimeMs(final String timeMs) {
        this.timeMs = timeMs;
    }

    public String getCpuTimeMs() {
        return cpuTimeMs;
    }

    public void setCpuTimeMs(final String cpuTimeMs) {
        this.cpuTimeMs = cpuTimeMs;
    }

    /**
     * The hashcode representing the SearchInfo object.
     *
     * @return the hashcode representing the SearchInfo object.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(rid,
                                timeMs,
                                cpuTimeMs);
    }

    /**
     * Equals method for the SearchInfo object.
     *
     * @param obj object to compare with for equality.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null || !(obj instanceof SearchInfo)) { return false; }

        final SearchInfo that = (SearchInfo) obj;
        return Objects.equal(this.rid, that.rid)
            && Objects.equal(this.timeMs, that.timeMs)
            && Objects.equal(this.cpuTimeMs, that.cpuTimeMs);
    }

    /**
     * String representation of the SearchInfo object.
     *
     * @return String representation of the SearchInfo object.
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("rid", rid)
                      .add("time-ms", timeMs)
                      .add("cpu-time-ms", cpuTimeMs)
                      .toString();
    }
}
