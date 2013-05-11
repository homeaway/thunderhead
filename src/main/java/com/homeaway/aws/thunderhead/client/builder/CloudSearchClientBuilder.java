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

package com.homeaway.aws.thunderhead.client.builder;

import com.homeaway.aws.thunderhead.client.CloudSearchClient;
import com.homeaway.aws.thunderhead.client.impl.CloudSearchClientImpl;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A Client builder has been provided to help build out a cloudsearch client.
 *
 * @author jmonette
 */
public class CloudSearchClientBuilder {
    public static final int DEFAULT_CONNECT_TIMEOUT = 2000;
    public static final int DEFAULT_READ_TIMEOUT = 3000;

    private boolean querySecure = false;
    private boolean updateSecure = false;

    private String queryHost;
    private String updateHost;

    private int queryConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int queryReadTimeout = DEFAULT_READ_TIMEOUT;
    private int updateConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int updateReadTimeout = DEFAULT_READ_TIMEOUT;


    private CloudSearchClientBuilder() {}

    public static CloudSearchClientBuilder newInstance() {
        return new CloudSearchClientBuilder();
    }

    public CloudSearchClientBuilder queryHttps(boolean secure) {
        this.querySecure = secure;
        return this;
    }

    public CloudSearchClientBuilder updateHttps(boolean secure) {
        this.updateSecure = secure;
        return this;
    }

    public CloudSearchClientBuilder queryHost(String queryHost) {
        this.queryHost = queryHost;
        return this;
    }

    public CloudSearchClientBuilder queryConnectTimeout(int queryConnectTimeout) {
        this.queryConnectTimeout = queryConnectTimeout;
        return this;
    }

    public CloudSearchClientBuilder queryReadTimeout(int queryReadTimeout) {
        this.queryReadTimeout = queryReadTimeout;
        return this;
    }

    public CloudSearchClientBuilder updateHost(String updateHost) {
        this.updateHost = updateHost;
        return this;
    }

    public CloudSearchClientBuilder updateConnectTimeout(int updateConnectTimeout) {
        this.updateConnectTimeout = updateConnectTimeout;
        return this;
    }

    public CloudSearchClientBuilder updateReadTimeout(int updateReadTimeout) {
        this.updateReadTimeout = updateReadTimeout;
        return this;
    }

    /**
     * Build and return a WebResource
     *
     * @return the WebResource that was built from this builder
     * @throws URISyntaxException if the host provided does not match a URI scheme
     */
    public CloudSearchClient build() throws URISyntaxException {

        WebResource queryWebResource = null;
        WebResource updateWebResouce = null;

        if (StringUtils.isNotEmpty(this.queryHost)) {
            String scheme = (this.querySecure ? "https://" : "http://");
            URI uri = new URI(scheme + this.queryHost);
            Client client = Client.create();
            client.setConnectTimeout(this.queryConnectTimeout);
            client.setReadTimeout(this.queryReadTimeout);
            queryWebResource = client.resource(uri);
        }

        if (StringUtils.isNotEmpty(this.updateHost)) {
            String scheme = (this.updateSecure ? "https://" : "http://");
            URI uri = new URI(scheme + this.updateHost);
            Client client = Client.create();
            client.setConnectTimeout(this.updateConnectTimeout);
            client.setReadTimeout(this.updateReadTimeout);
            updateWebResouce = client.resource(uri);
        }

        return new CloudSearchClientImpl(queryWebResource, updateWebResouce);
    }
}
