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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

class InjectHL7Test {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    @Mock
    private AuthUtil authUtilMock;
    @Mock
    private PropUtil propUtilMock;
    private InjectHL7 injectHL7;
    Properties mockProperties = mock(Properties.class);


    private String hl7FilePath = "path/to/hl7-input.hl7";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
        injectHL7 = new InjectHL7();
        injectHL7.authUtil = authUtilMock;
        injectHL7.propUtil = propUtilMock;
        injectHL7.authModel = new AuthModel();
        when(mockProperties.getProperty("service.reportsEndpoint")).thenReturn("testReportsEndpoint");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtilMock);
        Mockito.reset(propUtilMock);
    }

    @Test
    void testRunSuccessfulInjection() throws IOException {
        String username = "testUser";
        char[] password = "testUserPassword".toCharArray();
        String apiResponse = "Dummy_UUID";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), anyString())).thenReturn(apiResponse);
        File tempHL7File = getFile();

        injectHL7.hl7FilePath = tempHL7File.getAbsolutePath();
        injectHL7.username = username;
        injectHL7.password = password;

        injectHL7.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), anyString());

        String expectedOutput = "Dummy_UUID";
        assertEquals("testUser", authModelCaptor.getValue().getUsername());
        assertArrayEquals("testUserPassword".toCharArray(), authModelCaptor.getValue().getPassword());
        assertEquals(expectedOutput, outStream.toString().trim());

        assertTrue(tempHL7File.delete());
    }

    @Test
    void testRunInvalidPath() {
        String username = "testUser";
        char[] password = "testUserPassword".toCharArray();

        injectHL7.hl7FilePath = "invalid-path/to/hl7-input.hl7";
        injectHL7.username = username;
        injectHL7.password = password;

        assertThrows(RuntimeException.class, injectHL7::run);
    }

    @Test
    void testRunUserUnauthorized() throws IOException {
        String username = "notTestUser";
        char[] password = "notTestUserPassword".toCharArray();
        String apiResponse = "Unauthorized. Username/password is incorrect.";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("injecthl7"))).thenReturn(apiResponse);
        File tempHL7File = getFile();

        injectHL7.username = username;
        injectHL7.password = password;
        injectHL7.hl7FilePath = tempHL7File.getAbsolutePath();
        injectHL7.run();

        verify(authUtilMock).getResponseFromDIService(injectHL7.authModel, "injecthl7");
        assertEquals(apiResponse, outStream.toString().trim());
    }

    @Test
    void testRunEmptyUsernameOrPassword() {
        String username = "";
        char[] password = "testUserPassword".toCharArray();
        String expectedOutput = "Username or password is empty.";

        injectHL7.username = username;
        injectHL7.password = password;
        injectHL7.hl7FilePath = hl7FilePath;
        injectHL7.run();

        verify(authUtilMock, never()).getResponseFromDIService(any(AuthModel.class), eq("injecthl7"));
        assertEquals(expectedOutput, errStream.toString().trim());
    }

    @Test
    void testRunNullUsernameOrPassword() {
        String username = "testUser";
        char[] password = null;
        String expectedOutput = "Username or password or HL7 file path is null.";

        injectHL7.username = username;
        injectHL7.password = password;
        injectHL7.hl7FilePath = hl7FilePath;
        injectHL7.run();

        verify(authUtilMock, never()).getResponseFromDIService(any(AuthModel.class), anyString());
        assertEquals(expectedOutput, errStream.toString().trim());
    }

    @Test
    void testRunAllEmptyInputs() {
        injectHL7.hl7FilePath = null;
        injectHL7.username = null;
        injectHL7.password = null;

        injectHL7.run();

        String expectedOutput = "Username or password or HL7 file path is null.";
        assertEquals(expectedOutput, errStream.toString().trim());
        verifyNoInteractions(authUtilMock);
    }

    private static File getFile() throws IOException {
        File tempHL7File = File.createTempFile("test-hl7-input", ".hl7");

        try (FileWriter writer = new FileWriter(tempHL7File)) {
            writer.write("MSH|^~\\&|SIMHOSP|SFAC|RAPP|RFAC|20200508130643||ADT^A01|5|T|2.3|||AL||44|ASCII\n" +
                    "EVN|A01|20200508130643|||C006^Wolf^Kathy^^^Dr^^^DRNBR^PRSNL^^^ORGDR|\n" +
                    "PID|1|2590157853^^^SIMULATOR MRN^MRN|2590157853^^^SIMULATOR MRN^MRN~2478684691^^^NHSNBR^NHSNMBR||Esterkin^AKI Scenario 6^^^Miss^^CURRENT||19890118000000|F|||170 Juice Place^^London^^RW21 6KC^GBR^HOME||020 5368 1665^HOME|||||||||R^Other - Chinese^^^||||||||\n" +
                    "PD1|||FAMILY PRACTICE^^12345|\n" +
                    "PV1|1|I|RenalWard^MainRoom^Bed 1^Simulated Dummy Hospital^^BED^Main Building^5|28b|||C006^Wolf^Kathy^^^Dr^^^DRNBR^PRSNL^^^ORGDR|||MED|||||||||6145914547062969032^^^^visitid||||||||||||||||||||||ARRIVED|||20200508130643||");
        }
        return tempHL7File;
    }
}