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

package com.homeaway.aws.thunderhead.model.sdf;

import com.google.common.base.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This objects represents the delete type in an SDF
 * 
 * @author jmonette
 */
@XmlType(name = "Delete")
@XmlRootElement(name = "delete")
public class SearchDocumentDelete {

    /** The id to delete */
    @XmlAttribute(name = "id")
    private String id;

    /** The version to delete */
    @XmlAttribute(name = "version")
    private String version;

    /****************************** */
    /*    Getters and Setters       */
    /****************************** */
    public String getId() {
        return id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * The hashcode representing the SearchDocumentDelete object
     *
     * @return The hashcode representing the SearchDocumentDelete object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id,
                                version);
    }

    /**
     * Equals method for the SearchDocumentDelete object
     *
     * @param obj object to compare if this object is equal to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || !(obj instanceof SearchDocumentDelete)) {return false;}

        final SearchDocumentDelete that = (SearchDocumentDelete) obj;
        return Objects.equal(this.id, that.id)
            && Objects.equal(this.version, that.version);
    }

    /**
     * String representation of the SearchDocumentDelete object
     *
     * @return String representation of the SearchDocumentDelete object
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("id", id)
                      .add("version", version)
                      .toString();
    }
}
