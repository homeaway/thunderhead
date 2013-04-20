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

import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchBadRequestException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchBandwidthExceededException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchClientException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchForbiddenException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchGenericException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchInternalException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchInvalidCharacterSetException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchLengthRequiredException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchMethodNotAllowedException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchNotAcceptException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchNotFoundException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchRequestTimeoutException;
import com.homeaway.aws.thunderhead.model.exceptions.CloudSearchRequestTooLargeException;
import com.sun.jersey.api.client.ClientResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * @author jmonette
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudSearchClientExceptionsTest {
    
    private CloudSearchReadWriteClient cloudSearchReadWriteClient = new CloudSearchReadWriteClient();
    @Mock private ClientResponse clientResponse;
    
    @Test(expected = CloudSearchBadRequestException.class)
    public void testBadRequestException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(400);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchForbiddenException.class)
    public void testForbiddenException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(403);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchNotFoundException.class)
    public void testNotFoundException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(404);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchMethodNotAllowedException.class)
    public void testMethodNotAllowedException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(405);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchNotAcceptException.class)
    public void testNotAcceptableException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(406);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchRequestTimeoutException.class)
    public void testRequestTimeoutException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(408);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchLengthRequiredException.class)
    public void testLengthRequiredExceptionSearchDomain() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(401);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchLengthRequiredException.class)
    public void testLengthRequiredExceptionUpdateDomain() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(411);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchRequestTooLargeException.class)
    public void testRequestTooLongException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(413);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }
    @Test(expected = CloudSearchInvalidCharacterSetException.class)
    public void testInvalidCharacterSetException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(415);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchInternalException.class)
    public void testInternalServerErrorException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(500);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchBandwidthExceededException.class)
    public void testBandwidthExceededException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(509);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }

    @Test(expected = CloudSearchGenericException.class)
    public void testGenericException() throws CloudSearchClientException {
        when(clientResponse.getStatus()).thenReturn(402);
        cloudSearchReadWriteClient.checkStatus(clientResponse);
    }
}
