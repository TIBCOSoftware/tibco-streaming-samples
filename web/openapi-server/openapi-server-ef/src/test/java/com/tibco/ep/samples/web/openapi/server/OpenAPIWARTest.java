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

package com.tibco.ep.samples.web.openapi.server;

import com.tibco.ep.dtm.management.DtmCommand;
import com.tibco.ep.testing.framework.Administration;
import com.tibco.ep.testing.framework.Results;
import com.tibco.ep.testing.framework.UnitTest;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OpenAPI generated WAR test
 */
public class OpenAPIWARTest extends UnitTest {
    private static final int TRY_TIMES_IN_SECS = 30;

    private static String address;

    /**
     * get web server address
     */
    @BeforeClass
    public static void getWebServerAddress() {

        Administration administration = new Administration();

        final Results results = administration.execute("display", "web");
        Assert.assertEquals(DtmCommand.COMMAND_SUCCEEDED, results.returnCode());

        address = results.getCommandResults()
                         .get(0)
                         .getResultSet()
                         .getRows()
                         .get(0)
                         .getColumn(results.getCommandResults().get(0).getHeaderColumn("Network Address"));
    }

    /**
     * Endpoint test
     *
     * @throws InterruptedException interruption on sleep
     */
    @Test
    public void endpointTest() throws InterruptedException {

        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(System.getProperty("user.name"), "");

        Client client = ClientBuilder.newClient().register(feature);
        WebTarget webTarget;
        webTarget = client.target(UriBuilder.newInstance().path(address).path("openapi-server-war").path("test").build());
        waitForWAR(webTarget);

        Response response = webTarget.request().get();
        final String responseEntity = response.readEntity(String.class);
        assertThat(responseEntity).as("validate response entity").contains("Hello, TIBCO!");
    }

    /**
     * check if the WAR is ready in 30 seconds
     *
     * @param webTarget webTarget
     * @throws InterruptedException interruption on sleep
     */
    private void waitForWAR(WebTarget webTarget) throws InterruptedException {

        for (int i = 0; i < TRY_TIMES_IN_SECS; i++) {
            Response response = webTarget.request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return;
            }
            Thread.sleep(1000);
        }
        Assert.fail(String.format("springboot-war never started after %s seconds, fail the test.", TRY_TIMES_IN_SECS));
    }
}
