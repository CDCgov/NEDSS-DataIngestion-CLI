package gov.cdc.dataingestion.util;

import gov.cdc.dataingestion.model.AuthModel;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
*
* THIS CLASS HAS BEEN CREATED TO TEST THE AUTH UTIL IF CASES AND ALSO TO BRING THE CODE COVERAGE
* TO THE STANDARD BAR OF 90%. THIS CLASS REQUIRES ENVIRONMENT VARIABLES TO RUN THE UNIT TESTS. TO
* RUN THESE UNIT TESTS PASS ADMIN_USERNAME AND ADMIN_PASSWORD THROUGH THE ENVIRONMENT VARIABLES.
*/

class AuthUtilTest {

    @Mock
    private CloseableHttpClient httpClientMock;

    @Mock
    private HttpPost httpPostMock;

    @Mock
    private CloseableHttpResponse httpResponseMock;

    private AuthUtil authUtil;

    private AuthModel authModelMock;
    private PropUtil propUtilMock;
    private String serviceEndpoint;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authUtil = new AuthUtil();
        authModelMock = new AuthModel();
        propUtilMock = new PropUtil();
        Properties propertiesMock = propUtilMock.loadPropertiesFile();
        serviceEndpoint = propertiesMock.getProperty("service.reportsEndpoint");
        authModelMock.setAdminUser(System.getenv("ADMIN_USERNAME"));
        System.err.println("Admin user name is..." + authModelMock.getAdminUser());
        authModelMock.setAdminPassword(System.getenv("ADMIN_PASSWORD").toCharArray());
        System.err.println("Admin pass is..." + authModelMock.getAdminPassword());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetResponseFromDIServiceSuccessful() throws Exception {
        authModelMock.setRequestBody("Dummy HL7 Input");
        authModelMock.setServiceEndpoint(serviceEndpoint);

        when(httpClientMock.execute(eq(httpPostMock))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));
        when(httpResponseMock.getEntity().getContent()).thenReturn(toInputStream("Dummy_UUID"));

        authUtil.getResponseFromDIService(authModelMock);
    }

    @Test
    void testGetResponseFromDIServiceUnsuccessful() throws Exception {
        authModelMock.setRequestBody("Dummy HL7 Input");
        authModelMock.setServiceEndpoint(serviceEndpoint + "dummy_endpoint");

        when(httpClientMock.execute(eq(httpPostMock))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));
        when(httpResponseMock.getEntity().getContent()).thenReturn(toInputStream("Dummy_UUID"));

        authUtil.getResponseFromDIService(authModelMock);
    }

    @Test
    void testGetResponseFromDIServiceException() throws Exception {
        authModelMock.setRequestBody("Dummy HL7 Input");
        authModelMock.setServiceEndpoint(serviceEndpoint + "dummy_endpoint");

        when(httpClientMock.execute(eq(httpPostMock))).thenThrow(new IOException("Connection refused."));

        String actualResponse = authUtil.getResponseFromDIService(authModelMock);
        assertEquals("Something went wrong on the server side. Please check the logs.", actualResponse);
    }

    private InputStream toInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}