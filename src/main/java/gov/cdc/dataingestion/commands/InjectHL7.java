package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@CommandLine.Command(name = "injecthl7", mixinStandardHelpOptions = true, description = "This functionality will let developers use the /api/reports endpoint of DI Service.")
public class InjectHL7 extends PropUtil implements Runnable {

    @CommandLine.Option(names = {"--hl7-file"}, description = "HL7 file name with fully qualified path", interactive = true, echo = true, required = true)
    String hl7FilePath;

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();

    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        if(hl7FilePath != null) {
                StringBuilder requestBody = new StringBuilder();

                try(BufferedReader reader = new BufferedReader(new FileReader(hl7FilePath))) {
                    String line;
                    while((line = reader.readLine()) != null) {
                        requestBody.append(line + "\n");
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("HL7 file not found at the given location.");
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (requestBody.toString().trim().isEmpty()) {
                    throw new RuntimeException("Input cannot not be empty"); //NOSONAR
                }

                // Serving data from INT1 environment as the production doesn't have data yet
                authModel.setServiceEndpoint(getProperty("service.env.url") + getProperty("service.env.reportsEndpoint"));
                authModel.setRequestBody(requestBody.toString());

                String apiResponse = authUtil.getResponseFromDIService(authModel, "injecthl7");
                System.out.println(apiResponse);
            }
        else {
            System.err.println("HL7 file path is null.");
        }
    }
}
