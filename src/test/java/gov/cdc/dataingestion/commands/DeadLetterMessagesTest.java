package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DeadLetterMessagesTest {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    @Mock
    private AuthUtil authUtilMock;
    @Mock
    private PropUtil propUtilMock;
    private DeadLetterMessages target;
    Properties mockProperties = mock(Properties.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
        target = new DeadLetterMessages();
        target.authUtil = authUtilMock;
        target.propUtil = propUtilMock;
        target.authModel = new AuthModel();
        when(mockProperties.getProperty("service.dltMessagesEndpoint")).thenReturn("testDltMessagesEndpoint");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtilMock);
        Mockito.reset(propUtilMock);
    }

    @Test
    void testRunSuccessViewDltMessages() throws IOException {
        String user = "testUser";
        char[] password = "testPassword".toCharArray();
        String apiResponse = "[{\"errorMessageId\":\"E8F2D31D-520F-492F-97A1-8A2557DC129A\",\"errorMessageSource\":\"elr_raw\",\"message\":null,\"errorStackTrace\":null,\"errorStackTraceShort\":\"DiHL7Exception: Invalid Message Found unknown segment: SFT at SFT\",\"dltOccurrence\":1,\"dltStatus\":\"ERROR\",\"createdOn\":\"2023-11-22T03:51:18.380+00:00\",\"updatedOn\":null,\"createdBy\":\"elr_raw_dlt\",\"updatedBy\":\"elr_raw_dlt\"}]";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), anyString())).thenReturn(apiResponse);

        target.username = user;
        target.password = password;
        target.msgsize = "2";

        target.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), anyString());
        String expectedOutput = "ERROR_STACK_TRACE:DiHL7Exception: Invalid Message Found unknown segment: SFT at SFT MSG_ID:E8F2D31D-520F-492F-97A1-8A2557DC129A CREATED_ON:2023-11-22T03:51:18.380+00:00";

        assertEquals("testUser", authModelCaptor.getValue().getUsername());
        assertArrayEquals("testPassword".toCharArray(), authModelCaptor.getValue().getPassword());
        assertEquals(expectedOutput, outStream.toString().trim());
    }
    @Test
    void testRunSuccessForEmptyMessage() throws IOException {
        String user = "testUser";
        char[] password = "testPassword".toCharArray();
        String apiResponse = "";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), anyString())).thenReturn(apiResponse);

        target.username = user;
        target.password = password;
        target.msgsize = "";

        target.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), anyString());
        String expectedOutput = "";

        assertEquals("testUser", authModelCaptor.getValue().getUsername());
        assertArrayEquals("testPassword".toCharArray(), authModelCaptor.getValue().getPassword());
        assertEquals(expectedOutput, outStream.toString().trim());
    }
    @Test
    void testRunUserUnauthorized() throws IOException {
        String username = "notUser";
        char[] password = "notPassword".toCharArray();
        String apiResponse = "Unauthorized. Username/password is incorrect.";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("dltmessages"))).thenReturn(apiResponse);

        target.username = username;
        target.password = password;
        target.run();

        verify(authUtilMock).getResponseFromDIService(target.authModel, "dltmessages");
        assertEquals(apiResponse, outStream.toString().trim());
    }

    @Test
    void testRunEmptyUsernameOrPassword() {
        String user = null;
        char[] password = "testpassword".toCharArray();
        String expectedOutput = "Username or password is empty.";

        target.username = user;
        target.password = password;

        target.run();

        verify(authUtilMock, never()).getResponseFromDIService(any(AuthModel.class), eq("dltmsgs"));
        assertEquals(expectedOutput, errStream.toString().trim());
    }

    @Test
    void testRunNullUsernameOrPassword() {
        String username = "testuser";
        char[] password = null;
        String expectedOutput = "Username or password is empty.";
        target.username = username;
        target.password = password;

        target.run();

        verify(authUtilMock, never()).getResponseFromDIService(any(AuthModel.class), anyString());
        assertEquals(expectedOutput, errStream.toString().trim());
    }

    @Test
    void testRunAllEmptyInputs() {
        target.username = null;
        target.password = null;
        target.run();
        String expectedOutput = "Username or password is empty.";
        assertEquals(expectedOutput, errStream.toString().trim());
        verifyNoInteractions(authUtilMock);
    }
}