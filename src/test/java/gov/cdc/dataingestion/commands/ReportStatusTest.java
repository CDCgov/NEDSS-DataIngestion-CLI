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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReportStatusTest {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    @Mock
    private AuthUtil authUtilMock;
    @Mock
    private PropUtil propUtilMock;
    private ReportStatus reportStatus;
    Properties mockProperties = mock(Properties.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
        reportStatus = new ReportStatus();
        reportStatus.authUtil = authUtilMock;
        reportStatus.propUtil = propUtilMock;
        when(mockProperties.getProperty("service.reportStatusEndpoint")).thenReturn("testReportStatusEndpoint");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtilMock);
        Mockito.reset(propUtilMock);
    }

    @Test
    void testRunSuccessfulStatus() {
        reportStatus.reportUuid = "12345";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("status"))).thenReturn("Success");

        reportStatus.run();

        verify(authUtilMock).getResponseFromDIService(reportStatus.authModel, "status");
        assertEquals("Success", outStream.toString().trim());
    }

    @Test
    void testRunNullReportId() {
        reportStatus.reportUuid = null;

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("status"))).thenReturn("Success");

        reportStatus.run();

        assertEquals("Report UUID is null.", errStream.toString().trim());
    }

    @Test
    void testRunUserUnauthorized() {
        reportStatus.reportUuid = "12345";
        String apiResponse = "Unauthorized. Username/password is incorrect.";

        when(propUtilMock.loadPropertiesFile()).thenReturn(mockProperties);
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("status"))).thenReturn(apiResponse);

        reportStatus.run();

        verify(authUtilMock).getResponseFromDIService(reportStatus.authModel, "status");
        assertEquals(apiResponse, outStream.toString().trim());
    }
}