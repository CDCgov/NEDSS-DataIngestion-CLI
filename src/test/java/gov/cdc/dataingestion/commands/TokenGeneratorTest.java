package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenGeneratorTest {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    @Mock
    private AuthUtil authUtilMock;
    @Mock
    private PropUtil propUtilMock;
    private TokenGenerator tokenGenerator;
    Properties mockProperties = mock(Properties.class);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
        tokenGenerator = new TokenGenerator();
        tokenGenerator.propUtil = propUtilMock;
        tokenGenerator.authUtil = authUtilMock;
        tokenGenerator.authModel = new AuthModel();
        when(mockProperties.getProperty("service.tokenEndpoint")).thenReturn("testTokenEndpoint");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtilMock);
        Mockito.reset(propUtilMock);
    }

    @Test
    void testRunSuccess() {
        String adminUser = "admin";
        char[] adminPassword = "adminPassword".toCharArray();
        String apiResponse = "Dummy_Token";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("token"))).thenReturn(apiResponse);

        tokenGenerator.username = adminUser;
        tokenGenerator.password = adminPassword;
        tokenGenerator.run();

        verify(authUtilMock).getResponseFromDIService(tokenGenerator.authModel, "token");
        assertEquals(apiResponse, outStream.toString().trim());
    }

    @Test
    void testRunAdminUnauthorized() {
        String adminUser = "notAdmin";
        char[] adminPassword = "notAdminPassword".toCharArray();
        String apiResponse = "Unauthorized. Admin username/password is incorrect.";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("token"))).thenReturn(apiResponse);

        tokenGenerator.username = adminUser;
        tokenGenerator.password = adminPassword;
        tokenGenerator.run();

        verify(authUtilMock).getResponseFromDIService(tokenGenerator.authModel, "token");
        assertEquals(apiResponse, outStream.toString().trim());
    }

    @Test
    void testRunEmptyAdminUsernameOrPassword() {
        String adminUser = "";
        char[] adminPassword = "adminPassword".toCharArray();
        String expectedOutput = "Username or password is empty.";

        tokenGenerator.username = adminUser;
        tokenGenerator.password = adminPassword;
        tokenGenerator.run();

        verify(authUtilMock, never()).getResponseFromDIService(any(AuthModel.class), anyString());
        assertEquals(expectedOutput, errStream.toString().trim());
    }

    @Test
    void testRunNullAdminUsernameOrPassword() {
        String adminUser = "admin";
        char[] adminPassword = null;
        String expectedOutput = "Username or password is null.";

        tokenGenerator.username = adminUser;
        tokenGenerator.password = adminPassword;
        tokenGenerator.run();

        verify(authUtilMock, never()).getResponseFromDIService(any(AuthModel.class), anyString());
        assertEquals(expectedOutput, errStream.toString().trim());
    }
}