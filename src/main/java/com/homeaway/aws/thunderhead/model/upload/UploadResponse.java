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

package com.homeaway.aws.thunderhead.model.upload;

import com.google.common.base.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * The Java object representation of the response return from Amazon cloudsearch when updating the domain
 *
 * @author jmonette
 */
@XmlType(name = "Response")
@XmlRootElement(name = "response")
public class UploadResponse {

    /** The status of update */
    @XmlAttribute(name = "status")
    private String status;

    /** The number of adds POSTed to the cloudsearch domain */
    @XmlAttribute(name = "adds")
    private int adds;

    /** The number of deletes POSTed to the cloudsearch domain */
    @XmlAttribute(name = "deletes")
    private int deletes;

    /** Any errors encountered in POSTing to the cloudsearch domain */
    @XmlElementWrapper(name = "errors")
    @XmlElement(name = "error")
    private List<String> errors;

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public int getAdds() {
        return adds;
    }

    public void setAdds(final int adds) {
        this.adds = adds;
    }

    public int getDeletes() {
        return deletes;
    }

    public void setDeletes(final int deletes) {
        this.deletes = deletes;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(final List<String> errors) {
        this.errors = errors;
    }

    /**
     * The hashcode representing the UploadResponse object
     *
     * @return the hashcode representing the UploadResponse object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(errors,
                                status,
                                adds,
                                deletes);
    }

    /**
     * Equals method for the UploadResponse object
     *
     * @param obj object to compare if this object is equal to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || !(obj instanceof UploadResponse)) {return false;}

        final UploadResponse that = (UploadResponse) obj;
        return Objects.equal(this.errors, that.errors)
            && Objects.equal(this.status, that.status)
            && Objects.equal(this.adds, that.adds)
            && Objects.equal(this.deletes, that.deletes);
    }

    /**
     * String representation of the UploadResponse object
     *
     * @return String representation of the UploadResponse object
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("status", status)
                      .add("adds", adds)
                      .add("deletes", deletes)
                      .add("errors", errors)
                      .toString();
    }
}
