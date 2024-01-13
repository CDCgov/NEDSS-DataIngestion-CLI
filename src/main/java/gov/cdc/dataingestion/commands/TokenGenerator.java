package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import gov.cdc.dataingestion.util.TokenUtil;
import picocli.CommandLine;


@CommandLine.Command(name = "token", mixinStandardHelpOptions = true, description = "Generates a JWT token to connect to DI Service.")
public class TokenGenerator extends PropUtil implements Runnable {

    @CommandLine.Option(names = {"--username"}, description = "Username to connect to DI service", interactive = true, echo = true, required = true)
    String username;

    @CommandLine.Option(names = {"--password"}, description = "Password to connect to DI service", interactive = true, required = true)
    char[] password;

    private String randomSaltForJwtEncryption = "DICLI_RandomSalt";

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();
    TokenUtil tokenUtil = new TokenUtil(randomSaltForJwtEncryption);

    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        if(username != null && password != null) {
            if(!username.isEmpty() && password.length > 0) {
                authModel.setUsername(username.trim());
                authModel.setPassword(password);
                // Serving data from INT1 environment as the production doesn't have data yet
                authModel.setServiceEndpoint(getProperty("service.env.url") + getProperty("service.env.tokenEndpoint"));

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