package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import gov.cdc.dataingestion.util.TokenUtil;
import picocli.CommandLine;

import java.util.Properties;
import java.util.prefs.Preferences;

@CommandLine.Command(name = "token", mixinStandardHelpOptions = true, description = "Generates a JWT token to connect to DI Service.")
public class TokenGenerator implements Runnable {

    @CommandLine.Option(names = {"--username"}, description = "Username to connect to DI service", interactive = true, echo = true, required = true)
    String username;

    @CommandLine.Option(names = {"--password"}, description = "Password to connect to DI service", interactive = true, required = true)
    char[] password;

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();
    PropUtil propUtil = new PropUtil();
    TokenUtil tokenUtil = new TokenUtil();

    @Override
    public void run() {
        if(username != null && password != null) {
            if(!username.isEmpty() && password.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();
                authModel.setUsername(username.trim());
                authModel.setPassword(password);
                authModel.setServiceEndpoint(properties.getProperty("service.local.tokenEndpoint"));

                String apiResponse = authUtil.getResponseFromDIService(authModel, "token");

                if(apiResponse.contains("Error") || apiResponse.contains("Unauthorized") || apiResponse.contains("Exception")) {
                    System.out.println(apiResponse);
                }
                else {
                    tokenUtil.storeToken(apiResponse);
                    System.out.println("Token generated.");
                }
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
