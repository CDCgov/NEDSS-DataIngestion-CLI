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

import java.io.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegisterUserTest {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    @Mock
    private AuthUtil authUtilMock;
    @Mock
    private PropUtil propUtilMock;
    private RegisterUser registerUser;
    Properties mockProperties = mock(Properties.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
        registerUser = new RegisterUser();
        registerUser.authUtil = authUtilMock;
        registerUser.propUtil = propUtilMock;
        registerUser.authModel = new AuthModel();
        when(mockProperties.getProperty("service.registrationEndpoint")).thenReturn("testRegistrationEndpoint");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtilMock);
        Mockito.reset(propUtilMock);
    }

    @Test
    void testRunSuccessfulRegistration() {
        registerUser.username = "testUser";
        registerUser.password = "testUserPassword".toCharArray();
        registerUser.adminUser = "adminUser";
        registerUser.adminPassword = "adminPassword".toCharArray();

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("register"))).thenReturn("User Created Successfully.");

        registerUser.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), eq("register"));

        String expectedOutput = "User Created Successfully.";
        assertEquals("adminUser", authModelCaptor.getValue().getAdminUser());
        assertArrayEquals("adminPassword".toCharArray(), authModelCaptor.getValue().getAdminPassword());
        assertEquals(expectedOutput, outStream.toString().trim());
    }

    @Test
    void testRunUsernameAlreadyExists() {
        registerUser.username = "testUser";
        registerUser.password = "testUserPassword".toCharArray();
        registerUser.adminUser = "adminUser";
        registerUser.adminPassword = "adminPassword".toCharArray();

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("register"))).thenReturn("User already exists.Please choose another.");

        registerUser.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), eq("register"));

        String expectedOutput = "User already exists.Please choose another.";
        assertEquals("adminUser", authModelCaptor.getValue().getAdminUser());
        assertArrayEquals("adminPassword".toCharArray(), authModelCaptor.getValue().getAdminPassword());
        assertEquals(expectedOutput, outStream.toString().trim());
    }

    @Test
    void testRunAdminUnauthorized() {
        registerUser.username = "testUser";
        registerUser.password = "testUserPassword".toCharArray();
        registerUser.adminUser = "notAdminUser";
        registerUser.adminPassword = "notAdminPassword".toCharArray();

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("register"))).thenReturn("Unauthorized. Username/password is incorrect.");

        registerUser.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), eq("register"));

        String expectedOutput = "Unauthorized. Username/password is incorrect.";
        assertEquals("notAdminUser", authModelCaptor.getValue().getAdminUser());
        assertArrayEquals("notAdminPassword".toCharArray(), authModelCaptor.getValue().getAdminPassword());
        assertEquals(expectedOutput, outStream.toString().trim());
    }

    @Test
    void testRunNullResponse() {
        registerUser.username = "testUser";
        registerUser.password = "testUserPassword".toCharArray();
        registerUser.adminUser = "notAdminUser";
        registerUser.adminPassword = "notAdminPassword".toCharArray();

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("register"))).thenReturn(null);

        registerUser.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), eq("register"));

        String expectedOutput = "Something went wrong with API. Response came back as null.";
        assertEquals("notAdminUser", authModelCaptor.getValue().getAdminUser());
        assertArrayEquals("notAdminPassword".toCharArray(), authModelCaptor.getValue().getAdminPassword());
        assertEquals(expectedOutput, errStream.toString().trim());
    }

    @Test
    void testRunException() {
        registerUser.username = "testUser";
        registerUser.password = "testUserPassword".toCharArray();
        registerUser.adminUser = "notAdminUser";
        registerUser.adminPassword = "notAdminPassword".toCharArray();

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("register"))).thenThrow(new RuntimeException("An exception occurred."));

        assertThrows(RuntimeException.class, registerUser::run);
    }

    @Test
    void testRunAllEmptyInputs() {
        registerUser.username = "";
        registerUser.password = "".toCharArray();
        registerUser.adminUser = "";
        registerUser.adminPassword = "".toCharArray();

        registerUser.run();

        String expectedOutput = "One or more inputs are empty.";
        assertEquals(expectedOutput, errStream.toString().trim());
        verifyNoInteractions(authUtilMock);
    }

    @Test
    void testRunSomeEmptyInputs() {
        registerUser.username = "testUser";
        registerUser.password = "".toCharArray();
        registerUser.adminUser = "";
        registerUser.adminPassword = "adminPassword".toCharArray();

        registerUser.run();

        String expectedOutput = "One or more inputs are empty.";
        assertEquals(expectedOutput, errStream.toString().trim());
        verifyNoInteractions(authUtilMock);
    }

    @Test
    void testRunAllNullInputs() {
        registerUser.username = null;
        registerUser.password = null;
        registerUser.adminUser = null;
        registerUser.adminPassword = null;

        registerUser.run();

        String expectedOutput = "One or more inputs are null.";
        assertEquals(expectedOutput, errStream.toString().trim());
        verifyNoInteractions(authUtilMock);
    }

    @Test
    void testRunSomeNullInputs() {
        registerUser.username = null;
        registerUser.password = "testUserPassword".toCharArray();
        registerUser.adminUser = "adminUser";
        registerUser.adminPassword = null;

        registerUser.run();

        String expectedOutput = "One or more inputs are null.";
        assertEquals(expectedOutput, errStream.toString().trim());
        verifyNoInteractions(authUtilMock);
    }
}