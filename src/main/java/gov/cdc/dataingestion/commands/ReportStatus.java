package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import gov.cdc.dataingestion.util.TokenUtil;
import picocli.CommandLine;

import java.util.Properties;
import java.util.prefs.Preferences;

@CommandLine.Command(name = "status", mixinStandardHelpOptions = true, description = "This functionality will print out the status of the report for the provided UUID.")
public class ReportStatus implements Runnable{
    @CommandLine.Option(names = {"--report-id"}, description = "UUID provided by Data Ingestion Service during report ingestion", interactive = true, echo = true, required = true)
    String reportUuid;

//    @CommandLine.Option(names = {"--username"}, description = "Username to connect to DI service", interactive = true, echo = true, required = true)
//    String username;
//
//    @CommandLine.Option(names = {"--password"}, description = "Password to connect to DI service", interactive = true, required = true)
//    char[] password;

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();
    PropUtil propUtil = new PropUtil();

    TokenUtil tokenUtil = new TokenUtil();

    @Override
    public void run() {
//        if(username != null && password != null && reportUuid != null) {

            if(reportUuid != null) {
//            if(!username.isEmpty() && password.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();
                String serviceEndpoint = properties.getProperty("service.local.reportStatusEndpoint");

//                authModel.setUsername(username.trim());
//                authModel.setPassword(password);
//                String apiToken = authModel.getJwToken();

//                System.out.println("endpoint is..." + serviceEndpoint);
//                System.out.println("authmodel token is is..." + tokenUtil.retrieveToken());
                authModel.setServiceEndpoint(serviceEndpoint + "/" + reportUuid);

                String apiResponse = authUtil.getResponseFromDIService(authModel, "status");
                System.out.println(apiResponse);
            }
//            else {
//                System.err.println("Username or password is empty.");
//            }
//        }
        else {
            System.err.println("Username or password or report UUID is null.");
        }
    }
}
