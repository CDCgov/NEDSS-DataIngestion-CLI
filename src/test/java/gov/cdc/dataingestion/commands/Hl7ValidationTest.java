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

class Hl7ValidationTest {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    @Mock
    private AuthUtil authUtilMock;
    @Mock
    private PropUtil propUtilMock;
    private Hl7Validation target;
    Properties mockProperties = mock(Properties.class);


    private String hl7FilePath = "path/to/hl7-input.hl7";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
        target = new Hl7Validation();
        target.authUtil = authUtilMock;
        target.propUtil = propUtilMock;
        target.authModel = new AuthModel();
        when(mockProperties.getProperty("service.hl7Validation")).thenReturn("testHl7Validation");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtilMock);
        Mockito.reset(propUtilMock);
    }

    @Test
    void testRunSuccessfulInjection() throws IOException {
        String apiResponse = "Dummy_UUID";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), anyString())).thenReturn(apiResponse);
        File tempHL7File = getFile();

        target.hl7FilePath = tempHL7File.getAbsolutePath();

        target.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), anyString());

        String expectedOutput = "Dummy_UUID";
        assertEquals(expectedOutput, outStream.toString().trim());

        assertTrue(tempHL7File.delete());
    }

    @Test
    void testRunInvalidPath() {
        target.hl7FilePath = "invalid-path/to/hl7-input.hl7";

        assertThrows(RuntimeException.class, target::run);
    }

    @Test
    void testRunAdminUnauthorized() throws IOException {
        String apiResponse = "Unauthorized. Admin username/password is incorrect.";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("hl7validation"))).thenReturn(apiResponse);
        File tempHL7File = getFile();

        target.hl7FilePath = tempHL7File.getAbsolutePath();
        target.run();

        verify(authUtilMock).getResponseFromDIService(target.authModel, "hl7validation");
        assertEquals(apiResponse, outStream.toString().trim());
    }

    @Test
    void testRunAllEmptyInputs() {
        target.hl7FilePath = null;

        target.run();

        String expectedOutput = "HL7 file path is null.";
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