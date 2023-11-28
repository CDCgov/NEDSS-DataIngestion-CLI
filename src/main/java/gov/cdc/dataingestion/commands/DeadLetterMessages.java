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
    String username;//NOSONAR

    @CommandLine.Option(names = {"--password"}, description = "Admin Password to connect to DI service", interactive = true, required = true)//NOSONAR
    char[] password;//NOSONAR

    AuthModel authModel = new AuthModel();//NOSONAR
    AuthUtil authUtil = new AuthUtil();//NOSONAR
    PropUtil propUtil = new PropUtil();//NOSONAR

    @Override
    public void run() {
        if (username != null && !username.isEmpty() && password != null && password.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();//NOSONAR

                authModel.setUsername(username.trim());//NOSONAR
                authModel.setPassword(password);//NOSONAR
                authModel.setServiceEndpoint(properties.getProperty("service.dltErrorMessages"));//NOSONAR

                String apiResponse = authUtil.getResponseFromDIService(authModel, "dltmessages");//NOSONAR
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

            JSONArray newJsonArray = new JSONArray();
            for (int i = 0; i < subListJsonArray.length(); i++) {
                JSONObject origObject = subListJsonArray.getJSONObject(i);

                JSONObject newJsonObject = new JSONObject();
                newJsonObject.put("ERROR_MSG_ID", origObject.get("errorMessageId"));
                newJsonObject.put("ERROR_STACK_TRACE", origObject.get("errorStackTraceShort"));
                newJsonObject.put("CREATED_ON", origObject.get("createdOn"));

                newJsonArray.put(newJsonObject);
            }
            System.out.println(newJsonArray.toString());//NOSONAR
        }
    }
}