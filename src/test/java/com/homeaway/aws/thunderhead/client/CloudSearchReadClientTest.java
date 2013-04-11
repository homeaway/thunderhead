package com.homeaway.aws.thunderhead.client;

import com.google.common.collect.Lists;
import com.homeaway.aws.thunderhead.model.constants.CloudSearchQueryParam;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchClientException;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author jmonette
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudSearchReadClientTest {
    private CloudSearchReadWriteClient cloudSearchReadWriteClient = new CloudSearchReadWriteClient();

    @Test
    public void testNoReturnFieldsPatchQueryParams() throws CloudSearchClientException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add(CloudSearchQueryParam.Q, "hello");
        MultivaluedMap<String, String> patchedQueryParams = cloudSearchReadWriteClient.patchReturnFields(queryParams);

        assertThat(patchedQueryParams, is(queryParams));
    }

    @Test
    public void testPatchQueryParams() {
        String[] returnFields = new String[] {"this", "is", "a", "test"};
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.put(CloudSearchQueryParam.RETURN_FIELDS, Lists.newArrayList(returnFields));
        MultivaluedMap<String, String> patchedQueryParams = cloudSearchReadWriteClient.patchReturnFields(queryParams);

        patchedQueryParams.get(CloudSearchQueryParam.RETURN_FIELDS);
        assertThat(patchedQueryParams.getFirst(CloudSearchQueryParam.RETURN_FIELDS), is(StringUtils.join(Lists.newArrayList(returnFields), ",")));
    }
}
