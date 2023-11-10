package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import picocli.CommandLine;

import java.util.Properties;

@CommandLine.Command(name = "token", mixinStandardHelpOptions = true, description = "Generates a JWT token to connect to DI Service.")
public class TokenGenerator implements Runnable {

    @CommandLine.Option(names = {"--username"}, description = "Username to connect to DI service", interactive = true, echo = true, required = true)
    String username;

    @CommandLine.Option(names = {"--password"}, description = "Password to connect to DI service", interactive = true, required = true)
    char[] password;

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();
    PropUtil propUtil = new PropUtil();

    @Override
    public void run() {
        if(username != null && password != null) {
            if(!username.isEmpty() && password.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();

                authModel.setUsername(username.trim());
                authModel.setPassword(password);
                authModel.setServiceEndpoint(properties.getProperty("service.tokenEndpoint"));

                String apiResponse = authUtil.getResponseFromDIService(authModel, "token");
                System.out.println(apiResponse);
            }
            else {
                System.err.println("Username or password is empty.");
            }
        }
        else {
            System.err.println("Username or password is null.");
        }
    }
}
