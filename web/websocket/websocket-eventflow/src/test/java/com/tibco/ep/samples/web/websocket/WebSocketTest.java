/* *****************************************************************************
 Copyright (c) 2020-2023 Cloud Software Group, Inc.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors
 may be used to endorse or promote products derived from this software
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */

package com.tibco.ep.samples.web.websocket;

import com.tibco.ep.dtm.management.DtmCommand;
import com.tibco.ep.testing.framework.Administration;
import com.tibco.ep.testing.framework.Results;
import com.tibco.ep.testing.framework.UnitTest;
import org.eclipse.jetty.websocket.jakarta.client.JakartaWebSocketClientContainerProvider;
import org.eclipse.jetty.websocket.jakarta.common.JakartaWebSocketContainer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

import static com.tibco.ep.samples.web.websocket.WebSocketClient.messageContent;

/**
 * WebSocket-war test
 */
public class WebSocketTest extends UnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketTest.class);

    private static String address;

    private static final int TOTAL_TRY_TIMES = 30;

    /**
     * get web server address
     */
    @BeforeClass
    public static void setupServer() {

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
     * test for websocket-war
     *
     * @throws Exception exceptions
     */
    @Test
    public void EndpointTest() throws Exception {

        JakartaWebSocketContainer container = (JakartaWebSocketContainer) JakartaWebSocketClientContainerProvider.getWebSocketContainer();
        container.start();

        try {
            WebSocketClient client = waitForWAR(container);

            client.sendMessage();
            Thread.sleep(1000);
            String response = client.getMessage();
            Assert.assertNotNull("Response message should not be null", response);
            Assert.assertTrue("Response message should contain the message", response.contains(messageContent));
        } finally {
            container.stop();
        }

    }

    /**
     * check if the WAR is ready in 30 seconds
     *
     * @param container WebSocketContainer
     * @return WebSocketClient instance which is connecting to the webSocket WAR
     * @throws DeploymentException
     * @throws IOException
     * @throws InterruptedException
     */
    private WebSocketClient waitForWAR(WebSocketContainer container) throws DeploymentException, IOException, InterruptedException {
        final String baseURL = address.replace("http", "ws") + "/websocket-war/test";
        LOGGER.info(baseURL);
        boolean isTestSuccess = false;
        WebSocketClient client = null;
        for (int i = 0; i < TOTAL_TRY_TIMES; i++) {
            try {
                client = new WebSocketClient(container, URI.create(baseURL));
                isTestSuccess = true;
                break;
            } catch (IOException e) {
                Thread.sleep(1000);
            }
        }
        if (!isTestSuccess) {
            Assert.fail(String.format("webSocket-war is not ready in %d secs, fail the test.", TOTAL_TRY_TIMES));
        }
        return client;
    }
}
