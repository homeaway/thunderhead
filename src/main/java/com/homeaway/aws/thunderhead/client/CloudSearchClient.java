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

package com.homeaway.aws.thunderhead.client;

import com.homeaway.aws.thunderhead.model.builder.CloudSearchRequest;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchClientException;
import com.homeaway.aws.thunderhead.model.sdf.SearchDocumentFormat;
import com.homeaway.aws.thunderhead.model.search.SearchResponse;
import com.homeaway.aws.thunderhead.model.upload.UploadResponse;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @author jmonette
 */
public interface CloudSearchClient {
    /** The AWS cloud search version that the client uses */
    public static final String CLOUDSEARCH_VERSION = "2011-02-01";

    /** The logger used for logging */
    public static final Logger LOGGER = LoggerFactory.getLogger(CloudSearchClient.class);

    @Profiled(tag = "CloudSearchReadWriteClient.updateDomain")
    UploadResponse updateDomain(SearchDocumentFormat entity) throws CloudSearchClientException;

    @Profiled(tag = "CloudSearchReadClient.query")
    SearchResponse query(CloudSearchRequest cloudSearchRequest) throws CloudSearchClientException;
}
