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
        injectHL7.authModel = new AuthModel();
        when(mockProperties.getProperty("service.elrIngestionEndpoint")).thenReturn("testElrIngestionEndpoint");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtilMock);
        Mockito.reset(propUtilMock);
    }

    @Test
    void testRunSuccessfulInjection() throws IOException {
        String apiResponse = "Dummy_UUID";

        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), anyString())).thenReturn(apiResponse);
        File tempHL7File = getFile(false);

        injectHL7.hl7FilePath = tempHL7File.getAbsolutePath();

        injectHL7.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), anyString());

        String expectedOutput = "Dummy_UUID";
        assertEquals(expectedOutput, outStream.toString().trim());

        assertTrue(tempHL7File.delete());
    }

    @Test
    void testRunInvalidPath() {
        injectHL7.hl7FilePath = "invalid-path/to/hl7-input.hl7";

        assertThrows(RuntimeException.class, injectHL7::run);
    }

    @Test
    void testRunUserUnauthorized() throws IOException {
        String apiResponse = "Unauthorized. Username/password is incorrect.";

        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("injecthl7"))).thenReturn(apiResponse);
        File tempHL7File = getFile(false);

        injectHL7.hl7FilePath = tempHL7File.getAbsolutePath();
        injectHL7.run();

        verify(authUtilMock).getResponseFromDIService(injectHL7.authModel, "injecthl7");
        assertEquals(apiResponse, outStream.toString().trim());
    }

    @Test
    void testRunAllEmptyInputs() {
        injectHL7.hl7FilePath = null;

        injectHL7.run();

        String expectedOutput = "HL7 file path is null.";
        assertEquals(expectedOutput, errStream.toString().trim());
        verifyNoInteractions(authUtilMock);
    }

    @Test
    void testRunInjectionWherePayloadIsEmpty() throws IOException {
        String apiResponse = "Dummy_UUID";
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), anyString())).thenReturn(apiResponse);
        File tempHL7File = getFile(true);
        injectHL7.hl7FilePath = tempHL7File.getAbsolutePath();
        assertThrows(RuntimeException.class, injectHL7::run);

    }

    @SuppressWarnings("java:S6126")
    private static File getFile(boolean isEmpty) throws IOException {
        File tempHL7File = File.createTempFile("test-hl7-input", ".hl7");

        try (FileWriter writer = new FileWriter(tempHL7File)) {
            if (!isEmpty) {
                writer.write("MSH|^~\\&|SIMHOSP|SFAC|RAPP|RFAC|20200508130643||ADT^A01|5|T|2.3|||AL||44|ASCII\n" +
                        "EVN|A01|20200508130643|||C006^Wolf^Kathy^^^Dr^^^DRNBR^PRSNL^^^ORGDR|\n" +
                        "PID|1|2590157853^^^SIMULATOR MRN^MRN|2590157853^^^SIMULATOR MRN^MRN~2478684691^^^NHSNBR^NHSNMBR||Esterkin^AKI Scenario 6^^^Miss^^CURRENT||19890118000000|F|||170 Juice Place^^London^^RW21 6KC^GBR^HOME||020 5368 1665^HOME|||||||||R^Other - Chinese^^^||||||||\n" +
                        "PD1|||FAMILY PRACTICE^^12345|\n" +
                        "PV1|1|I|RenalWard^MainRoom^Bed 1^Simulated Dummy Hospital^^BED^Main Building^5|28b|||C006^Wolf^Kathy^^^Dr^^^DRNBR^PRSNL^^^ORGDR|||MED|||||||||6145914547062969032^^^^visitid||||||||||||||||||||||ARRIVED|||20200508130643||");
            } else {
                writer.write("");
            }

        }
        return tempHL7File;
    }
}