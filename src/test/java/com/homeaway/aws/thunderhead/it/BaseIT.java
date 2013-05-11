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

package com.homeaway.aws.thunderhead.it;

import com.google.common.collect.Lists;
import com.homeaway.aws.thunderhead.model.sdf.Field;
import com.homeaway.aws.thunderhead.model.sdf.SearchDocumentAdd;
import com.homeaway.aws.thunderhead.model.sdf.SearchDocumentFormat;
import org.junit.Before;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

/**
 * @author jmonette
 */
public abstract class BaseIT {
    protected static final String EXAMPLE_FIELD = "example_field";
    protected static final String PROP_FILE = "test.properties";
    protected static final String UPDATE_HOST_KEY = "thunderhead.update.host";
    protected static final String QUERY_HOST_KEY = "thunderhead.query.host";

    private String updateHost;
    private String queryHost;
    private String uuid;
    private String id;

    @Before
    public void setUp() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(PROP_FILE);
        if (inputStream == null) {
            throw new FileNotFoundException("The property file '" + PROP_FILE + "' does not exist. A " +
                    "'test.properties.template' file has been provided. Please copy this file to " +
                    "'src/test/resources/test.properties' and fill with appropriate values to run the integration " +
                    "tests.");
        }

        Properties props = new Properties();
        props.load(inputStream);

        updateHost = props.getProperty(UPDATE_HOST_KEY);
        queryHost = props.getProperty(QUERY_HOST_KEY);

        uuid = UUID.randomUUID().toString();
        id = uuid.replaceAll("-", "");
    }

    public String getQueryHost() {
        return this.queryHost;
    }

    public String getUpdateHost() {
        return this.updateHost;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getId() {
        return this.id;
    }

    public SearchDocumentFormat buildSearchDocumentFormat() {
        SearchDocumentFormat SDF = new SearchDocumentFormat();
        SearchDocumentAdd searchDocumentAdd = new SearchDocumentAdd();
        searchDocumentAdd.setId(getId());
        searchDocumentAdd.setVersion("1");
        searchDocumentAdd.setLang(Locale.ENGLISH.getLanguage());

        Field exampleField = new Field();
        exampleField.setName(EXAMPLE_FIELD);
        exampleField.setValue(getUuid());

        List<Field> fields = Lists.newArrayList();
        fields.add(exampleField);

        searchDocumentAdd.setFields(fields);
        SDF.setSearchDocumentAdds(Lists.newArrayList(searchDocumentAdd));

        return SDF;
    }
}
