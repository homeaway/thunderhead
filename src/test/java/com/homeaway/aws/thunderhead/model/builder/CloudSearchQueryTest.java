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

package com.homeaway.aws.thunderhead.model.builder;

import org.junit.Test;

import static com.homeaway.aws.thunderhead.model.builder.CloudSearchQuery.and;
import static com.homeaway.aws.thunderhead.model.builder.CloudSearchQuery.match;
import static com.homeaway.aws.thunderhead.model.builder.CloudSearchQuery.or;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 *
 */
public class CloudSearchQueryTest {

    @Test
    public void basicStringExpressionQueryStringTest() {
        String expected = "key:'value'";
        CloudSearchQuery query = match("key", "value");
        assertThat(query.getQueryString(), is(expected));
    }

    @Test
    public void basicIntExpressionQueryStringTest() {
        String expected = "key:0";
        CloudSearchQuery query = match("key", 0);
        assertThat(query.getQueryString(), is(expected));
    }

    @Test
    public void andQueryStringSimpleExpressionTest() {
        String expected = "(and bird:'dog' cat:'fish')";
        CloudSearchQuery query = and(match("bird", "dog"), match("cat", "fish"));
        assertThat(query.getQueryString(), is(expected));
    }

    @Test
    public void andQueryStringNAryExpressionTest() {
        String expected = "(and bird:'dog' cat:'fish' fish:'bird')";
        CloudSearchQuery query = and(match("bird", "dog"), match("cat", "fish"), match("fish", "bird"));
        assertThat(query.getQueryString(), is(expected));
    }

    @Test
    public void orQueryStringSimpleExpressionTest() {
        String expected = "(or bird:'dog' cat:'fish')";
        CloudSearchQuery query = or(match("bird", "dog"), match("cat", "fish"));
        assertThat(query.getQueryString(), is(expected));
    }

    @Test
    public void orQueryStringNAryExpressionTest() {
        String expected = "(or bird:'dog' cat:'fish' fish:'bird')";
        CloudSearchQuery query = or(match("bird", "dog"), match("cat", "fish"), match("fish", "bird"));
        assertThat(query.getQueryString(), is(expected));
    }

    @Test
    public void mixedQueryStringSimpleExpressionTest() {
        String expected = "(and (or bird:'dog' cat:'fish') fish:'bird')";
        CloudSearchQuery query = and(or(match("bird", "dog"), match("cat", "fish")), match("fish", "bird"));
        assertThat(query.getQueryString(), is(expected));
    }

    @Test
    public void getQueryStringComplexExpressionTest() {

        String expected = "(and (or (and cat:'dog' dog:'cat') cat:'fish') (or fish:'bird' bird:'dog'))";

        CloudSearchQuery query =
                and(or(and(match("cat", "dog"), match("dog", "cat")), match("cat", "fish")),
                        or(match("fish", "bird"), match("bird", "dog")));

        assertThat(query.getQueryString(), is(expected));
    }
}
