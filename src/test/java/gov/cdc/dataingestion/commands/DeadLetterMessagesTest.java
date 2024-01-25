package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
        target = new DeadLetterMessages();
        target.authUtil = authUtilMock;
        target.authModel = new AuthModel();
        when(propUtilMock.getProperty("service.dltMessagesEndpoint")).thenReturn("testDltMessagesEndpoint");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtilMock);
        Mockito.reset(propUtilMock);
    }

    @Test
    void testRunSuccessForEmptyMessage() {
        String apiResponse = "";
        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), anyString())).thenReturn(apiResponse);

        target.msgsize = "";
        target.run();
        String expectedOutput = "";
        assertEquals(expectedOutput, outStream.toString().trim());
    }

    @ParameterizedTest
    @CsvSource(value={"2,ERROR_STACK_TRACE:DiHL7Exception: Invalid Message Found unknown segment: SFT at SFT MSG_ID:E8F2D31D-520F-492F-97A1-8A2557DC129A CREATED_ON:2023-11-22T03:51:18.380+00:00","-2,Invalid input. Please enter a positive number.","abc,Invalid input. Please enter a positive number."})
    void testRunSuccess_For_Valid_and_Invalid_Inputs(String messageSize, String expectedOutput) {
        String apiResponse = "[{\"errorMessageId\":\"E8F2D31D-520F-492F-97A1-8A2557DC129A\",\"errorMessageSource\":\"elr_raw\",\"message\":null,\"errorStackTrace\":null,\"errorStackTraceShort\":\"DiHL7Exception: Invalid Message Found unknown segment: SFT at SFT\",\"dltOccurrence\":1,\"dltStatus\":\"ERROR\",\"createdOn\":\"2023-11-22T03:51:18.380+00:00\",\"updatedOn\":null,\"createdBy\":\"elr_raw_dlt\",\"updatedBy\":\"elr_raw_dlt\"}]";

        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), anyString())).thenReturn(apiResponse);

        target.msgsize = messageSize;

        target.run();

        ArgumentCaptor<AuthModel> authModelCaptor = ArgumentCaptor.forClass(AuthModel.class);
        verify(authUtilMock).getResponseFromDIService(authModelCaptor.capture(), anyString());

        assertEquals(expectedOutput, outStream.toString().trim());
    }

    @Test
    void testRunUserUnauthorized() throws IOException {
        String apiResponse = "Unauthorized. Username/password is incorrect.";

        when(authUtilMock.getResponseFromDIService(any(AuthModel.class), eq("dltmessages"))).thenReturn(apiResponse);

        target.run();

        verify(authUtilMock).getResponseFromDIService(target.authModel, "dltmessages");
        assertEquals(apiResponse, outStream.toString().trim());
    }
}