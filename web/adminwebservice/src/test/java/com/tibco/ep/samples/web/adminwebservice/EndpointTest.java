/*
 ****************************************************************************
 Copyright (c) 2019-2023 Cloud Software Group, Inc.

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
package com.tibco.ep.samples.web.adminwebservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.kabira.platform.property.Status;
import com.streambase.sb.StreamBaseException;
import com.streambase.sb.unittest.SBServerManager;
import com.streambase.sb.unittest.ServerManagerFactory;
import com.tibco.ep.dtm.management.DtmCommand;
import com.tibco.ep.testing.framework.Administration;
import com.tibco.ep.testing.framework.Results;
import com.tibco.ep.testing.framework.TransactionalDeadlockDetectedException;
import com.tibco.ep.testing.framework.TransactionalMemoryLeakException;
import com.tibco.ep.testing.framework.UnitTest;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * Example test case
 */
public class EndpointTest extends UnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointTest.class);

    private static SBServerManager server;

    private final static String SERVICE_NAME = System.getProperty(Status.NODE_NAME);
    private static final String USERNAME = System.getProperty("user.name");

    private static String url;
    private final static String TARGETS = "targets";
    private final static String CONTEXT_LOGIN = "login";
    private final static String ADMIN = "admin";
    private final static String VERSION_NUMBER = "v1";
    private final static String RESPONSE_KEY_RESULTS = "results";
    private final static String RESPONSE_KEY_SERVICE_NAME = "serviceName";
    private final static String RESPONSE_KEY_RETURN_CODE = "returnCode";
    private final static String RESPONSE_KEY_ROWS = "rows";
    private final static String RESPONSE_KEY_COLUMN_HEADERS = "columnHeaders";

    private final static HttpAuthenticationFeature AUTHENTICATION_FEATURE = HttpAuthenticationFeature.basic(USERNAME, "");

    private final ObjectMapper mapper = new ObjectMapper();
    private static List<Cookie> cookies;

    /**
     * Set up the server
     *
     * @throws StreamBaseException  on start server error
     * @throws InterruptedException on start server error
     */
    @BeforeClass
    public static void setupServer() throws StreamBaseException, InterruptedException {

        // create a StreamBase server and load modules once for all tests in this class
        server = ServerManagerFactory.getEmbeddedServer();
        server.startServer();
        server.loadApp("com.tibco.ep.samples.web.adminwebservice.eventflow.Demo");

        Administration administration = new Administration();
        final Results results = administration.execute("display", "web");
        Assert.assertEquals(DtmCommand.COMMAND_SUCCEEDED, results.returnCode());

        url = results.getCommandResults()
                     .get(0)
                     .getResultSet()
                     .getRows()
                     .get(0)
                     .getColumn(results.getCommandResults().get(0).getHeaderColumn("Network Address"));
        LOGGER.info(url);
        Client client = ClientBuilder.newClient();
        client.register(AUTHENTICATION_FEATURE);
        WebTarget webTarget;
        Response response;
        webTarget = client.target(new JerseyUriBuilder().path(url).path(ADMIN).path(VERSION_NUMBER).path(CONTEXT_LOGIN).build());

        for (int i = 0; i < 60; i++) {
            response = webTarget.request().post(null);
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                assertNotEquals(response.getCookies().keySet().size(), 0);

                cookies = response.getCookies().entrySet().stream().map(e-> e.getValue().toCookie()).toList();
                break;
            } else {
                LOGGER.info("response: " + response.readEntity(String.class));
            }
            LOGGER.info("Admin web service is not ready, wait for 1 sec. Then re-try");
            Thread.sleep(1000);
        }

        if (cookies == null) {
            Assert.fail("Starting Admin web service is failed.");
        }
    }

    /**
     * Stop the server
     *
     * @throws StreamBaseException  on shutdown failure
     * @throws InterruptedException on shutdown failure
     */
    @AfterClass
    public static void stopServer() throws InterruptedException, StreamBaseException {
        assertNotNull(server);
        server.shutdownServer();
        server = null;
    }

    /**
     * Start the containers
     *
     * @throws StreamBaseException on start container error
     */
    @Before
    public void startContainers() throws StreamBaseException {
        // Setup test framework before running tests
        this.initialize();

        // before each test, startup fresh container instances
        server.startContainers();

    }

    /**
     * administration command discovery test case
     *
     * @throws IOException error when parse response to JSON
     */
    @Test
    public void administrationCommandDiscoveryTest() throws IOException {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget;
        Response response;
        String responseEntity;

        LOGGER.info("find all administration targets, equals to 'epadmin help targets'");
        webTarget = client.target(new JerseyUriBuilder().path(url).path(ADMIN).path(VERSION_NUMBER).path(TARGETS).build());
        var req = webTarget.request();
        cookies.forEach(c -> req.cookie(c));
        response = req.get();
        Assert.assertEquals("should return status 200", Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertNotNull("Should return help information", response.getEntity());
        responseEntity = response.readEntity(String.class);
        LOGGER.info(responseEntity);

        JsonNode results = mapper.readTree(responseEntity).get(RESPONSE_KEY_RESULTS);
        Assert.assertEquals("Results should be a JSON array", JsonNodeType.ARRAY, results.getNodeType());
        JsonNode currentNodeResult = results.get(0);
        Assert.assertEquals(0, currentNodeResult.get(RESPONSE_KEY_RETURN_CODE).asInt());
        Assert.assertEquals(SERVICE_NAME, currentNodeResult.get(RESPONSE_KEY_SERVICE_NAME).asText());
        Assert.assertEquals(JsonNodeType.ARRAY, currentNodeResult.get(RESPONSE_KEY_COLUMN_HEADERS).getNodeType());
        Assert.assertEquals(JsonNodeType.ARRAY, currentNodeResult.get(RESPONSE_KEY_ROWS).getNodeType());

    }

    /**
     * administration command execution test case
     *
     * @throws IOException error when parse response to JSON
     */
    @Test
    public void administrationCommandExecutingTest() throws IOException {
        LOGGER.info("execute display node command");

        final String TARGET_NODE = "node";
        final String COMMAND_DISPLAY = "display";
        final String PARAMETER_COMMAND = "command";

        Client client = ClientBuilder.newClient().register(MultiPartFeature.class);
        WebTarget webTarget;
        Response response;
        FormDataMultiPart form;
        FormDataBodyPart parametersBody;
        String responseEntity;

        form = new FormDataMultiPart();
        parametersBody = new FormDataBodyPart("parameters", "");
        form.bodyPart(parametersBody);
        webTarget = client.target(new JerseyUriBuilder().path(url)
                                                        .path(ADMIN)
                                                        .path(VERSION_NUMBER)
                                                        .path(TARGETS)
                                                        .path(TARGET_NODE)
                                                        .queryParam(PARAMETER_COMMAND, COMMAND_DISPLAY)
                                                        .build());

        var req = webTarget.request();
        cookies.forEach(c -> req.cookie(c));
        response = req.post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
        Assert.assertEquals("should return status 200", Response.Status.OK.getStatusCode(), response.getStatus());
        responseEntity = response.readEntity(String.class);
        LOGGER.info(responseEntity);

        JsonNode results = mapper.readTree(responseEntity).get(RESPONSE_KEY_RESULTS);
        Assert.assertEquals("", JsonNodeType.ARRAY, results.getNodeType());
        JsonNode currentNodeResult = results.get(0);
        Assert.assertEquals("", SERVICE_NAME, currentNodeResult.get(RESPONSE_KEY_SERVICE_NAME).asText());
        Assert.assertEquals("", 0, currentNodeResult.get(RESPONSE_KEY_RETURN_CODE).asInt());
        Assert.assertEquals("", JsonNodeType.ARRAY, currentNodeResult.get(RESPONSE_KEY_COLUMN_HEADERS).getNodeType());
        Assert.assertEquals("", JsonNodeType.ARRAY, currentNodeResult.get(RESPONSE_KEY_ROWS).getNodeType());

    }

    /**
     * Stop containers
     *
     * @throws StreamBaseException                    on stop container error
     * @throws TransactionalMemoryLeakException       Leak detected
     * @throws TransactionalDeadlockDetectedException Deadlock detected
     */
    @After
    public void stopContainers() throws StreamBaseException, TransactionalMemoryLeakException, TransactionalDeadlockDetectedException {
        // after each test, dispose of the container instances
        server.stopContainers();
        // Complete test framework and check for any errors
        this.complete();
    }
}
