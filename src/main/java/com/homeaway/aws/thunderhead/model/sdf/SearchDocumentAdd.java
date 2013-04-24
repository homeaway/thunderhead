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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * This objects represents the add type in an SDF
 * 
 * @author jmonette
 */
@XmlType(name = "Add")
@XmlRootElement(name = "add")
public class SearchDocumentAdd {

    /** The id for the add operation */
    @XmlAttribute(name = "id")
    private String id;

    /** The version for the add operation */
    @XmlAttribute(name = "version")
    private String version;

    /** The language associated with the add operation */
    @XmlAttribute(name = "lang")
    private String lang;

    /** A list of fields to be indexed */
    @XmlElement(name = "field")
    private List<Field> fields;

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

    public String getLang() {
        return lang;
    }

    public void setLang(final String lang) {
        this.lang = lang;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(final List<Field> fields) {
        this.fields = fields;
    }

    /**
     * The hashcode representing the SearchDocumentAdd object
     *
     * @return The hashcode representing the SearchDocumentAdd object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id,
                                version,
                                lang,
                                fields);
    }

    /**
     * Equals method for the SearchDocumentAdd object
     *
     * @param obj object to compare if this object is equal to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || !(obj instanceof SearchDocumentAdd)) {return false;}

        final SearchDocumentAdd that = (SearchDocumentAdd) obj;
        return Objects.equal(this.id, that.id)
            && Objects.equal(this.version, that.version)
            && Objects.equal(this.lang, that.lang)
            && Objects.equal(this.fields, that.fields);

    }

    /**
     * String representation of the SearchDocumentAdd object
     *
     * @return String representation of the SearchDocumentAdd object
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("id", id)
                      .add("version", version)
                      .add("lang", lang)
                      .add("fields", fields)
                      .toString();
    }
}
