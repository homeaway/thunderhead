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

package com.homeaway.aws.thunderhead.client.builder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A Web Resource builder has been provided to help build out a web resource for the cloudsearch client.
 *
 * @author jmonette
 */
public final class CloudSearchWebResourceBuilder {
    /** The default connect timeout value. */
    public static final int DEFAULT_CONNECT_TIMEOUT = 2000;
    /** The default read timeout value. */
    public static final int DEFAULT_READ_TIMEOUT = 3000;

    private String host;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private boolean secure = false;

    private CloudSearchWebResourceBuilder() { }

    /**
     * Static factory method for creating a builder.
     * @return the builder created from this factory method.
     */
    public static CloudSearchWebResourceBuilder newInstance() {
        return new CloudSearchWebResourceBuilder();
    }

    /**
     * Configure the builder with the host.
     *
     * @param host the host for the web resource.
     * @return the builder with the host set.
     */
    public CloudSearchWebResourceBuilder host(final String host) {
        this.host = host;
        return this;
    }

    /**
     * Configure the builder with a connect timeout.
     *
     * @param connectTimeout the connect timeout to configure the builder with.
     * @return the builder with the connect set.
     */
    public CloudSearchWebResourceBuilder connectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * Configure the builder with a read timeout.
     *
     * @param readTimeout the read timeout to configure the builder with.
     * @return the builder with the read timeout set.
     */
    public CloudSearchWebResourceBuilder readTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Configure the builder to use secure http protocol or not.
     *
     * @param secure whether or not to use https or http.
     * @return the builder with whether or not to use https
     */
    public CloudSearchWebResourceBuilder useHttps(final boolean secure) {
        this.secure = secure;
        return this;
    }

    /**
     * Build and return a WebResource.
     *
     * @return the WebResource that was built from this builder.
     * @throws URISyntaxException if the host provided does not match a URI scheme.
     */
    public WebResource build() throws URISyntaxException {

        if (StringUtils.isEmpty(this.host)) {
            throw new IllegalStateException("host must not be null or empty");
        }

        String scheme = (this.secure ? "https://" : "http://");
        URI uri = new URI(scheme + this.host);
        Client client = Client.create();
        client.setConnectTimeout(this.connectTimeout);
        client.setReadTimeout(this.readTimeout);

        return client.resource(uri);
    }
}
