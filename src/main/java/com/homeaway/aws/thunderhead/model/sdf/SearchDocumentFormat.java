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

package com.homeaway.aws.thunderhead.model.sdf;

import com.google.common.base.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * The Java object representation of the SDF document to upload to Amazon cloudsearch
 * 
 * @author jmonette
 */
@XmlType(name = "Batch")
@XmlRootElement(name = "batch")
public class SearchDocumentFormat {

    /** A batch of SDF adds to POST to Amazon */
    @XmlElement(name = "add")
    private List<SearchDocumentAdd> searchDocumentAdds;

    /** A batch of SDF deletes to POST to Amazon */
    @XmlElement(name = "delete")
    private List<SearchDocumentDelete> searchDocumentDeletes;

    /****************************** */
    /*    Getters and Setters       */
    /****************************** */
    public List<SearchDocumentAdd> getSearchDocumentAdds() {
        return searchDocumentAdds;
    }

    public void setSearchDocumentAdds(final List<SearchDocumentAdd> searchDocumentAdds) {
        this.searchDocumentAdds = searchDocumentAdds;
    }

    public List<SearchDocumentDelete> getSearchDocumentDeletes() {
        return searchDocumentDeletes;
    }

    public void setSearchDocumentDeletes(final List<SearchDocumentDelete> searchDocumentDeletes) {
        this.searchDocumentDeletes = searchDocumentDeletes;
    }

    /**
     * The hashcode representing the SearchDocumentFormat object
     *
     * @return The hashcode representing the SearchDocumentFormat object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(searchDocumentAdds,
                                searchDocumentDeletes);
    }

    /**
     * Equals method for the SearchDocumentFormat object
     *
     * @param obj object to compare if this object is equal to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || !(obj instanceof SearchDocumentFormat)) {return false;}

        final SearchDocumentFormat that = (SearchDocumentFormat) obj;
        return Objects.equal(this.searchDocumentAdds, that.searchDocumentAdds)
            && Objects.equal(this.searchDocumentDeletes, that.searchDocumentDeletes);

    }

    /**
     * String representation of the SearchDocumentFormat object
     *
     * @return String representation of the SearchDocumentFormat object
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("adds", searchDocumentAdds)
                      .add("deletes", searchDocumentDeletes)
                      .toString();
    }
}
