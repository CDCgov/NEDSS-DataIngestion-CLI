package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import picocli.CommandLine;

import java.io.Console;
import java.util.Properties;

@CommandLine.Command(name = "register", mixinStandardHelpOptions = true, description = "Client will be onboarded providing username and secret.")
public class RegisterUser implements Runnable {

    @CommandLine.Option(names = {"--client-username"}, description = "Username provided by the client", interactive = true, echo = true, required = true)
    String username;

    @CommandLine.Option(names = {"--client-secret"}, description = "Secret provided by the client", interactive = true, required = true)
    char[] password;

    @CommandLine.Option(names = {"--admin-user"}, description = "Admin Username to connect to DI service", interactive = true, echo = true, required = true)
    String adminUser;

    @CommandLine.Option(names = {"--admin-password"}, description = "Admin Password to connect to DI service", interactive = true, required = true)
    char[] adminPassword;

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();
    PropUtil propUtil = new PropUtil();


    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        if(username != null && password != null && adminUser != null && adminPassword != null) {
            if(!username.isEmpty() && password.length > 0 && !adminUser.isEmpty() && adminPassword.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();
                // Serving data from INT1 environment as the production doesn't have data yet
                String serviceEndpoint = properties.getProperty("service.int1.registrationEndpoint");
                String jsonRequestBody = "{\"username\": \"" + username.trim() + "\", \"password\": \"" + new String(password) + "\"}";

                authModel.setAdminUser(adminUser.trim());
                authModel.setAdminPassword(adminPassword);
                authModel.setServiceEndpoint(serviceEndpoint);
                authModel.setRequestBody(jsonRequestBody);

                String apiResponse = authUtil.getResponseFromDIService(authModel, "register");
                if(apiResponse != null) {
                    System.out.println(apiResponse);
                }
                else {
                    System.err.println("Something went wrong with API. Response came back as null.");
                }

            }
            else {
                System.err.println("One or more inputs are empty.");
            }
        }
        else {
            System.err.println("One or more inputs are null.");
        }
    }
}
