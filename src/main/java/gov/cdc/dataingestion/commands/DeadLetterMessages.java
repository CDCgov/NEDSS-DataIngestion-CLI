package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "dltmessages", mixinStandardHelpOptions = true, description = "This functionality to view the messages in the dead letter messages.")
public class DeadLetterMessages  extends PropUtil implements Runnable {

    @CommandLine.Option(names = {"--msg-size"}, description = "Number of messages to be displayed.Default is 10", interactive = true, echo = true, required = false)//NOSONAR
    String msgsize = "10";

    AuthModel authModel = new AuthModel();//NOSONAR
    AuthUtil authUtil = new AuthUtil();//NOSONAR

    @Override
    @SuppressWarnings("java:S106")
    public void run() {
        // Serving data from INT1 environment as the production doesn't have data yet
        authModel.setServiceEndpoint(getProperty("service.env.url") + getProperty("service.env.dltErrorMessages"));

        String apiResponse = authUtil.getResponseFromDIService(authModel, "dltmessages");
        displayDLTMessages(apiResponse, msgsize);
    }

    private void displayDLTMessages(String dltMsgs, String msgSize) {
        if (dltMsgs != null && !dltMsgs.trim().startsWith("[")) {
            System.out.println(dltMsgs);//NOSONAR
        } else {
            int nonOfMsgDisplay = 0;
            if (!msgSize.isEmpty()) {
                try {
                    nonOfMsgDisplay =Double.valueOf(msgSize).intValue();
                }catch (NumberFormatException ex){
                    System.out.println("Invalid input. Please enter a positive number.");//NOSONAR
                    return;
                }
                if(nonOfMsgDisplay<=0){
                    System.out.println("Invalid input. Please enter a positive number.");//NOSONAR
                    return;
                }
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