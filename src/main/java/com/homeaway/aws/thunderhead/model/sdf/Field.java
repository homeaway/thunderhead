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
import javax.xml.bind.annotation.XmlValue;

/**
 * This object representation the fields to be indexed in the SDF
 * 
 * @author jmonette
 */
@XmlType(name = "Field")
@XmlRootElement(name = "field")
public class Field {
    
    /** The name of the field found */
    @XmlAttribute(name = "name")
    private String name;

    /** The value of the field found */
    @XmlValue
    private String value;

    /****************************** */
    /*    Getters and Setters       */
    /****************************** */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * The hashcode representing the Field object
     * 
     * @return the hashcode representing the Field object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(name,
                                value);
    }

    /**
     * Equals method for the Field object
     * 
     * @param obj object to compare if this object is equal to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || !(obj instanceof Field)) {return false;}

        final Field that = (Field) obj;
        return Objects.equal(this.name, that.name)
            && Objects.equal(this.value, that.value);
    }

    /**
     * String representation of the Field object
     * 
     * @return String representation of the Field object
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("name", name)
                      .add("value", value)
                      .toString();
    }
}
