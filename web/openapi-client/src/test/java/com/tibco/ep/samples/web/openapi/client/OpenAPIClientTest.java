/* ******************************************************************************
 * Copyright (c) 2020-2023 Cloud Software Group, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package com.tibco.ep.samples.web.openapi.client;

import com.kabira.platform.property.Status;
import com.tibco.ep.dtm.management.DtmCommand;
import com.tibco.ep.samples.web.openapi.client.handler.ApiClient;
import com.tibco.ep.samples.web.openapi.client.handler.ApiException;
import com.tibco.ep.samples.web.openapi.client.handler.GetTheNodeStatusApi;
import com.tibco.ep.samples.web.openapi.client.model.NodeStatus;
import com.tibco.ep.testing.framework.Administration;
import com.tibco.ep.testing.framework.Results;
import com.tibco.ep.testing.framework.UnitTest;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Assert;
import org.junit.Test;
import org.junit.BeforeClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OpenAPI generated web client test
 */
public class OpenAPIClientTest extends UnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPIClientTest.class);

    private static final int TRY_TIMES_IN_SECS = 60;
    private static final String USERNAME = System.getProperty("user.name");
    private static final String NODE_NAME = System.getProperty(Status.NODE_NAME);
    private final static String HEALTH_CHECK = "healthcheck";
    private final static String VERSION_NUMBER = "v1";
    private final static String STATUS = "status";
    private static String address;

    /**
     * Wait for the health check web service get deployed
     *
     * @throws InterruptedException on start server error
     */
    @BeforeClass
    public static void waitForWARDeployed() throws InterruptedException {

        address = getWebServerAddress();
        final HttpAuthenticationFeature AUTHENTICATION_FEATURE = HttpAuthenticationFeature.basic(USERNAME, "");

        Client client = ClientBuilder.newClient();
        client.register(AUTHENTICATION_FEATURE);
        WebTarget webTarget;
        Response response;
        webTarget = client.target(UriBuilder.newInstance().path(address).path(HEALTH_CHECK).path(VERSION_NUMBER).path(STATUS).build());
        boolean isStarted = false;

        for (int i = 0; i < TRY_TIMES_IN_SECS; i++) {
            response = webTarget.request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                isStarted = true;
                break;
            }
            LOGGER.info("The health check web service is not ready, wait for 1 sec. Then re-try");
            Thread.sleep(1000);
        }
        assertThat(isStarted).describedAs("The health check web service never started after {} seconds", TRY_TIMES_IN_SECS).isTrue();
    }

    /**
     * Generated web client api test
     *
     * @throws ApiException error on test
     */
    @Test
    public void getStatusTest() throws ApiException {

        ApiClient apiClient = new ApiClient();
        apiClient.setUsername(USERNAME);
        apiClient.setBasePath(address + "/" + HEALTH_CHECK + "/" + VERSION_NUMBER);
        apiClient.setServerIndex(null);
        GetTheNodeStatusApi getTheNodeStatusApi = new GetTheNodeStatusApi();
        getTheNodeStatusApi.setApiClient(apiClient);
        LOGGER.info(getTheNodeStatusApi.getApiClient().getBasePath());
        assertThat(getTheNodeStatusApi.statusGet().getNodeState()).as("validate response entity: node state").isEqualTo(NodeStatus.NodeStateEnum.STARTED);
        assertThat(getTheNodeStatusApi.statusGet().getNodeName()).as("validate response entity: node name").isEqualTo(NODE_NAME);
    }

    private static String getWebServerAddress() {

        Administration administration = new Administration();

        final Results results = administration.execute("display", "web");
        Assert.assertEquals(DtmCommand.COMMAND_SUCCEEDED, results.returnCode());
        LOGGER.info("display web result: " + results.toString());
        return results.getCommandResults()
                      .get(0)
                      .getResultSet()
                      .getRows()
                      .get(0)
                      .getColumn(results.getCommandResults().get(0).getHeaderColumn("Network Address"));
    }
}
