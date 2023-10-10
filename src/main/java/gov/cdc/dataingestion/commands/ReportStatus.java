package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import picocli.CommandLine;

import java.util.Properties;

@CommandLine.Command(name = "status", mixinStandardHelpOptions = true, description = "This functionality will print out the status of the report for the provided UUID.")
public class ReportStatus implements Runnable{
    @CommandLine.Option(names = {"--report-id"}, description = "UUID provided by Data Ingestion Service during report ingestion", interactive = true, echo = true, required = true)
    String reportUuid;

    @CommandLine.Option(names = {"--admin-user"}, description = "Admin Username to connect to DI service", interactive = true, echo = true, required = true)
    String adminUser;

    @CommandLine.Option(names = {"--admin-password"}, description = "Admin Password to connect to DI service", interactive = true, required = true)
    char[] adminPassword;

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();
    PropUtil propUtil = new PropUtil();

    @Override
    public void run() {
        if(adminUser != null && adminPassword != null && reportUuid != null) {
            if(!adminUser.isEmpty() && adminPassword.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();
                String serviceEndpoint = properties.getProperty("service.reportsEndpoint");

                authModel.setAdminUser(adminUser);
                authModel.setAdminPassword(adminPassword);
                authModel.setServiceEndpoint(serviceEndpoint + "/" + reportUuid);

                String apiResponse = authUtil.getResponseFromDIService(authModel, "status");
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
