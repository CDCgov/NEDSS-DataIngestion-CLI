package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import gov.cdc.dataingestion.util.EncryptionUtil;
import picocli.CommandLine;

import java.util.Properties;

@CommandLine.Command(name = "token", mixinStandardHelpOptions = true, description = "Generates a JWT token to connect to DI Service.")
public class TokenGenerator implements Runnable {

    @CommandLine.Option(names = {"--client-id"}, description = "Client ID to connect to DI service", interactive = true, echo = true, required = true)
    String clientId;

    @CommandLine.Option(names = {"--client-secret"}, description = "Client Secret to connect to DI service", interactive = true, required = true)
    char[] clientSecret;

    private String randomSaltForJwtEncryption = "DICLI_RandomSalt";

    private static final String TOKEN_KEY = "apiJwt";
    private static final String CLIENT_ID_KEY = "clientId";
    private static final String CLIENT_SECRET_KEY = "clientSecret";

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();
    PropUtil propUtil = new PropUtil();
    EncryptionUtil encryptionUtil = new EncryptionUtil(randomSaltForJwtEncryption);

    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        if(clientId != null && clientSecret != null) {
            if(!clientId.isEmpty() && clientSecret.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();
                authModel.setClientId(clientId.trim());
                authModel.setClientSecret(clientSecret);
                // Serving data from INT1 environment as the production doesn't have data yet
                authModel.setServiceEndpoint(properties.getProperty("service.int1.tokenEndpoint"));

                String apiResponse = authUtil.getResponseFromDIService(authModel, "token");

                if(apiResponse.contains("Error") || apiResponse.contains("Unauthorized") || apiResponse.contains("Exception")) {
                    System.out.println(apiResponse);
                }
                else {
                    encryptionUtil.storeString(apiResponse, TOKEN_KEY);
                    encryptionUtil.storeString(clientId, CLIENT_ID_KEY);
                    encryptionUtil.storeString(new String(clientSecret), CLIENT_SECRET_KEY);
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