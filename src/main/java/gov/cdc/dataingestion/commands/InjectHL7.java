package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@CommandLine.Command(name = "injecthl7", mixinStandardHelpOptions = true, description = "This functionality will let developers use the /api/reports endpoint of DI Service.")
public class InjectHL7 implements Runnable {

    @CommandLine.Option(names = {"--hl7-file"}, description = "HL7 file name with fully qualified path", interactive = true, echo = true, required = true)
    String hl7FilePath;

    @CommandLine.Option(names = {"--admin-user"}, description = "Admin Username to connect to DI service", interactive = true, echo = true, required = true)
    String adminUser;

    @CommandLine.Option(names = {"--admin-password"}, description = "Admin Password to connect to DI service", interactive = true, required = true)
    char[] adminPassword;

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();
    PropUtil propUtil = new PropUtil();


    @Override
    public void run() {
        if(adminUser != null && adminPassword != null && hl7FilePath != null) {
            if(!adminUser.isEmpty() && adminPassword.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();
                StringBuilder requestBody = new StringBuilder();

                try(BufferedReader reader = new BufferedReader(new FileReader(hl7FilePath))) {
                    String line;
                    while((line = reader.readLine()) != null) {
                        requestBody.append(line);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                authModel.setAdminUser(adminUser);
                authModel.setAdminPassword(adminPassword);
                authModel.setServiceEndpoint(properties.getProperty("service.reportsEndpoint"));
                authModel.setRequestBody(requestBody.toString());

                String apiResponse = authUtil.getResponseFromDIService(authModel, "injecthl7");
                System.out.println(apiResponse);
            }
            else {
                System.err.println("Admin username or password is empty.");
            }
        }
        else {
            System.err.println("One or more inputs are null.");
        }
    }
}
