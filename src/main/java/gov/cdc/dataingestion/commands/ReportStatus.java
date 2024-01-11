package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.config.AppConfig;
import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import picocli.CommandLine;

import java.util.Properties;

@CommandLine.Command(name = "status", mixinStandardHelpOptions = true, description = "This functionality will print out the status of the report for the provided UUID.")
public class ReportStatus extends AppConfig implements Runnable{
    @CommandLine.Option(names = {"--report-id"}, description = "UUID provided by Data Ingestion Service during report ingestion", interactive = true, echo = true, required = true)
    String reportUuid;

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();

    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        if(reportUuid != null) {

            // Serving data from INT1 environment as the production doesn't have data yet
            String serviceEndpoint = getProperty("service.env.url") + getProperty("service.env.reportStatusEndpoint");

            authModel.setServiceEndpoint(serviceEndpoint + "/" + reportUuid);

            String apiResponse = authUtil.getResponseFromDIService(authModel, "status");
            System.out.println(apiResponse);
            }
        else {
            System.err.println("Report UUID is null.");
        }
    }
}
