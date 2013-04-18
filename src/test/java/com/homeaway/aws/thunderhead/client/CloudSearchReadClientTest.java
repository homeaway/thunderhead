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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This test class is used for testing the CloudSearchReadClient.
 *
 * @author jmonette
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudSearchReadClientTest {
    private CloudSearchReadWriteClient cloudSearchReadWriteClient = new CloudSearchReadWriteClient();

    /**
     * Test to make sure that patchedQueryParams works with no return fields provided.
     *
     * @throws CloudSearchClientException
     */
    @Test
    public void testNoReturnFieldsPatchQueryParams() throws CloudSearchClientException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add(CloudSearchQueryParam.Q, "hello");
        MultivaluedMap<String, String> patchedQueryParams = cloudSearchReadWriteClient.patchReturnFields(queryParams);

        assertThat(patchedQueryParams, is(queryParams));
    }

    /**
     * Testing that the patched query params method works as expected.
     */
    @Test
    public void testPatchQueryParams() {
        String[] returnFields = new String[] {"this", "is", "a", "test"};
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.put(CloudSearchQueryParam.RETURN_FIELDS, Lists.newArrayList(returnFields));
        MultivaluedMap<String, String> patchedQueryParams = cloudSearchReadWriteClient.patchReturnFields(queryParams);

        patchedQueryParams.get(CloudSearchQueryParam.RETURN_FIELDS);
        assertThat(patchedQueryParams.getFirst(CloudSearchQueryParam.RETURN_FIELDS),
                   is(StringUtils.join(Lists.newArrayList(returnFields), ",")));
    }
}
