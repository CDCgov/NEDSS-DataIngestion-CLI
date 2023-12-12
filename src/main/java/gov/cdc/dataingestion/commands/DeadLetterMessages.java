package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import picocli.CommandLine;

import java.util.List;
import java.util.Properties;

@CommandLine.Command(name = "dltmessages", mixinStandardHelpOptions = true, description = "This functionality to view the messages in the dead letter messages.")
public class DeadLetterMessages implements Runnable {

    @CommandLine.Option(names = {"--msgsize"}, description = "Number of Messages to display.Default is 10", interactive = true, echo = true, required = false)//NOSONAR
    String msgsize = "10";

    @CommandLine.Option(names = {"--username"}, description = "Admin Username to connect to DI service", interactive = true, echo = true, required = true)//NOSONAR
    String username;

    @CommandLine.Option(names = {"--password"}, description = "Admin Password to connect to DI service", interactive = true, required = true)//NOSONAR
    char[] password;

    AuthModel authModel = new AuthModel();//NOSONAR
    AuthUtil authUtil = new AuthUtil();//NOSONAR
    PropUtil propUtil = new PropUtil();//NOSONAR

    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        if (username != null && !username.isEmpty() && password != null && password.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();

                authModel.setUsername(username.trim());
                authModel.setPassword(password);
                authModel.setServiceEndpoint(properties.getProperty("service.local.dltErrorMessages"));

                String apiResponse = authUtil.getResponseFromDIService(authModel, "dltmessages");
                displayDLTMessages(apiResponse, msgsize);
        } else {
            System.err.println("Username or password is empty.");//NOSONAR
        }
    }

    private void displayDLTMessages(String dltMsgs, String msgSize) {
        if (dltMsgs != null && !dltMsgs.trim().startsWith("[")) {
            System.out.println(dltMsgs);//NOSONAR
        } else {
            int nonOfMsgDisplay = 0;
            if (!msgSize.isEmpty()) {
                nonOfMsgDisplay = Integer.parseInt(msgSize);
            }
            JSONArray jsonArray = new JSONArray(dltMsgs);
            int availableMsgSize = jsonArray.length();
            if (nonOfMsgDisplay > availableMsgSize) {
                nonOfMsgDisplay = availableMsgSize;
            }
            List<Object> errorSubList = jsonArray.toList().subList(0, nonOfMsgDisplay);
            JSONArray subListJsonArray = new JSONArray(errorSubList);

            StringBuilder sb=new StringBuilder();
            for (int i = 0; i < subListJsonArray.length(); i++) {
                JSONObject origObject = subListJsonArray.getJSONObject(i);
                sb.append("ERROR_STACK_TRACE:").append(origObject.get("errorStackTraceShort"));
                sb.append(" MSG_ID:").append(origObject.get("errorMessageId"));
                sb.append(" CREATED_ON:").append(origObject.get("createdOn"));
                sb.append("\n");
            }
            System.out.println(sb.toString());//NOSONAR
        }
    }
}