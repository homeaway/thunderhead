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
import com.homeaway.aws.thunderhead.client.CloudSearchReadWriteClient;
import com.homeaway.aws.thunderhead.client.builder.CloudSearchWebResourceBuilder;
import com.homeaway.aws.thunderhead.model.enums.CloudSearchQueryParam;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchClientException;
import com.homeaway.aws.thunderhead.model.sdf.Field;
import com.homeaway.aws.thunderhead.model.sdf.SearchDocumentAdd;
import com.homeaway.aws.thunderhead.model.sdf.SearchDocumentFormat;
import com.homeaway.aws.thunderhead.model.search.SearchResponse;
import com.homeaway.aws.thunderhead.model.upload.UploadResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author jmonette
 */
public class CloudSearchReadWriteClientIT extends BaseIT{

    private CloudSearchReadWriteClient cloudSearchClient;

    /**
     * This method sets up the cloudSearchClient to be used for testing
     */
    @Before
    public void setup() throws URISyntaxException, IOException {

        WebResource updateWebResource = CloudSearchWebResourceBuilder.newInstance()
                                                                     .host(getUpdateHost())
                                                                     .build();

        WebResource queryWebResource = CloudSearchWebResourceBuilder.newInstance()
                                                                     .host(getQueryHost())
                                                                     .build();

        cloudSearchClient = new CloudSearchReadWriteClient();
        cloudSearchClient.setUpdateWebResource(updateWebResource);
        cloudSearchClient.setQueryWebResource(queryWebResource);
    }

    /**
     * This test will test basic upload functionality. Uploads a document and verifies that there was no request errors
     */
    @Test
    public void basicUploadTest() throws CloudSearchClientException {
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
        UploadResponse uploadResponse = this.cloudSearchClient.updateDomain(SDF);

        assertThat(uploadResponse.getAdds(), is(1));
        assertThat(uploadResponse.getDeletes(), is(0));
        assertThat(uploadResponse.getErrors(), is(nullValue()));
    }

    /**
     * This method will test basic query functionality. This will query the search domain for an entity and verify there
     * was no server error
     */
    @Test
    public void basicQueryTest() throws CloudSearchClientException, InterruptedException {
        // Upload a doc to the search index to test
        basicUploadTest();

        // Sleep for 10 seconds to allow for cloudsearch to index
        Thread.sleep(10000);

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add(CloudSearchQueryParam.BQ.getName(), EXAMPLE_FIELD + ":'" + getUuid() + "'");
        queryParams.add(CloudSearchQueryParam.RETURN_FIELDS.getName(), EXAMPLE_FIELD);

        SearchResponse searchResponse = this.cloudSearchClient.query(queryParams);

        assertThat(searchResponse, is(notNullValue()));
        assertThat(searchResponse.getFound(), is(notNullValue()));
        assertThat(searchResponse.getFound().getHits(), is(notNullValue()));
        assertThat(searchResponse.getFound().getHits().size(), is(1));
        assertThat(searchResponse.getFound().getHits().get(0).getId(), is(getId()));
        assertThat(searchResponse.getFound().getHits().get(0).getReturnFieldsList(), is(notNullValue()));
        assertThat(searchResponse.getFound().getHits().get(0).getReturnFieldsList().size(), is(1));
        assertThat(searchResponse.getFound().getHits().get(0).getReturnFieldsList().get(0).getName(), is(EXAMPLE_FIELD));
        assertThat(searchResponse.getFound().getHits().get(0).getReturnFieldsList().get(0).getValue(), is(getUuid()));
    }
}
