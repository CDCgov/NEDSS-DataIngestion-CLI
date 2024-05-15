package gov.cdc.dataingestion.util;

import gov.cdc.dataingestion.model.AuthModel;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/*
*
* THIS CLASS HAS BEEN CREATED TO TEST THE AUTH UTIL IF CASES AND ALSO TO BRING THE CODE COVERAGE
* TO THE STANDARD BAR OF 90%. THIS CLASS REQUIRES ENVIRONMENT VARIABLES TO RUN THE UNIT TESTS. TO
* RUN THESE UNIT TESTS IN LOCAL, PASS USERNAME AND PASSWORD THROUGH THE ENVIRONMENT VARIABLES.
*/

class AuthUtilTest {

    @Mock
    private CloseableHttpClient httpClientMock;

    @Mock
    private HttpPost httpPostMock;

    @Mock
    private HttpGet httpGetMock;
    @Mock
    private CloseableHttpResponse httpResponseMock;

    private AuthUtil authUtil;

    private AuthModel authModelMock;
    private PropUtil propUtilMock;
    private String serviceIngestionEndpoint;
    private String serviceDltEndpoint;
    private String serviceValidationEndpoint;
    private String serviceTokenEndpoint;
    private AutoCloseable closeable;
    @BeforeEach
    void setUp() {
        closeable=MockitoAnnotations.openMocks(this);
        authUtil = new AuthUtil();
        authModelMock = new AuthModel();
        propUtilMock = new PropUtil();
        serviceIngestionEndpoint = propUtilMock.getProperty("service.env.elrIngestionEndpoint");
        serviceDltEndpoint = propUtilMock.getProperty("service.env.dltErrorMessages");
        serviceValidationEndpoint = propUtilMock.getProperty("service.env.hl7Validation");
        serviceTokenEndpoint = propUtilMock.getProperty("service.env.tokenEndpoint");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetResponseFromDIServiceSuccessful() throws Exception {
        authModelMock.setRequestBody("Dummy HL7 Input");
        authModelMock.setServiceEndpoint(serviceIngestionEndpoint);

        when(httpClientMock.execute(eq(httpPostMock))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));
        when(httpResponseMock.getEntity().getContent()).thenReturn(toInputStream("Dummy_UUID"));

        authUtil.getResponseFromDIService(authModelMock, "status");
    }

    @Test
    void testGetResponseFromDIServiceUnsuccessful() throws Exception {
        authModelMock.setRequestBody("Dummy HL7 Input");
        authModelMock.setServiceEndpoint(serviceIngestionEndpoint + "dummy_endpoint");

        when(httpClientMock.execute(eq(httpPostMock))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));
        when(httpResponseMock.getEntity().getContent()).thenReturn(toInputStream("Dummy_UUID"));

        authUtil.getResponseFromDIService(authModelMock, "injecthl7");
    }
    private InputStream toInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void testGetResponseForDltMessagesSuccessful() throws Exception {
        authModelMock.setServiceEndpoint(serviceDltEndpoint);
        when(httpClientMock.execute(httpGetMock)).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));
        when(httpResponseMock.getEntity().getContent()).thenReturn(toInputStream("Dummy_DLTMSG"));

        String response=authUtil.getResponseFromDIService(authModelMock, "dltmessages");
        assertNotNull(response);
    }

    @Test
    void testGetResponseForHL7ValidationSuccessful() throws Exception {
        authModelMock.setRequestBody("Dummy HL7 Input");
        authModelMock.setServiceEndpoint(serviceValidationEndpoint);
        when(httpClientMock.execute(httpGetMock)).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));
        when(httpResponseMock.getEntity().getContent()).thenReturn(toInputStream("Dummy_UUID"));

        String response=authUtil.getResponseFromDIService(authModelMock, "hl7validation");
        assertNotNull(response);
    }

    @Test
    void testGetResponseForTokenSuccessful() throws Exception {
        authModelMock.setClientId(System.getProperty("USERNAME"));
        authModelMock.setClientSecret(System.getProperty("PASSWORD").toCharArray());
        authModelMock.setServiceEndpoint(serviceTokenEndpoint);
        when(httpClientMock.execute(httpGetMock)).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(httpResponseMock.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(mock(HttpEntity.class));
        when(httpResponseMock.getEntity().getContent()).thenReturn(toInputStream("Dummy_Token"));

        String response=authUtil.getResponseFromDIService(authModelMock, "token");
        assertNotNull(response);
    }

    @Test
    void testConvertInputStreamToString() throws IOException {
        String sampleResponse = "Dummy response from API";
        InputStream inputStream = new ByteArrayInputStream(sampleResponse.getBytes());

        String result = authUtil.convertInputStreamToString(inputStream);

        assertEquals(sampleResponse, result);
    }
}