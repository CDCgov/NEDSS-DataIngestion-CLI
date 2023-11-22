package gov.cdc.dataingestion.commands;

import gov.cdc.dataingestion.model.AuthModel;
import gov.cdc.dataingestion.util.AuthUtil;
import gov.cdc.dataingestion.util.PropUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import picocli.CommandLine;

import java.util.List;
import java.util.Properties;

@CommandLine.Command(name = "dltmsgs", mixinStandardHelpOptions = true, description = "This functionality to view the messages in the dead letter messages.")
public class DeadLetterMessages implements Runnable {
    @CommandLine.Option(names = {"--msgsize"}, description = "Number of Messages to display", interactive = true, echo = true, required = false)
    String msgsize = "10";
    @CommandLine.Option(names = {"--username"}, description = "Admin Username to connect to DI service", interactive = true, echo = true, required = true)
    String username;

    @CommandLine.Option(names = {"--password"}, description = "Admin Password to connect to DI service", interactive = true, required = true)
    char[] password;

    AuthModel authModel = new AuthModel();
    AuthUtil authUtil = new AuthUtil();
    PropUtil propUtil = new PropUtil();

    @Override
    public void run() {
        if (username != null && password != null) {
            if (!username.isEmpty() && password.length > 0) {
                Properties properties = propUtil.loadPropertiesFile();

                authModel.setUsername(username.trim());
                authModel.setPassword(password);
                authModel.setServiceEndpoint(properties.getProperty("service.int1.dltErrorMessages"));

                String apiResponse = authUtil.getResponseFromDIService(authModel, "dltmsgs");
                displayDLTMessages(apiResponse,msgsize);
                
            } else {
                System.err.println("Username or password is empty.");
            }
        } else {
            System.err.println("Username or password or HL7 file path is null.");
        }
    }
    private void displayDLTMessages(String dltMsgs,String msgSize){
        int nonOfMsgDisplay=0;
        if(msgSize!=null && !msgSize.isEmpty()){
            nonOfMsgDisplay=Integer.parseInt(msgSize);
        }
        JSONArray jsonArray = new JSONArray(dltMsgs);
        int availableMsgSize= jsonArray.length();
        System.out.println("Total DLT Messages:"+availableMsgSize);
        if(nonOfMsgDisplay>availableMsgSize){
            nonOfMsgDisplay=availableMsgSize;
        }
        List errorSubList=jsonArray.toList().subList(0,nonOfMsgDisplay);
        JSONArray subListJsonArray = new JSONArray(errorSubList);

        JSONArray newJsonArray = new JSONArray();
        for (int i = 0; i < subListJsonArray.length(); i++) {
            JSONObject origObject = subListJsonArray.getJSONObject(i);

            System.out.println(origObject.get("errorMessageId"));

            JSONObject newJsonObject=new JSONObject();
            newJsonObject.put("ERROR_MSG_ID",origObject.get("errorMessageId"));
            newJsonObject.put("ERROR_MSG_SOURCE",origObject.get("errorMessageSource"));
            newJsonObject.put("ERROR_STACK_TRACE",origObject.get("errorStackTraceShort"));
            newJsonObject.put("CREATED_ON",origObject.get("createdOn"));

            newJsonArray.put(newJsonObject);
        }
        System.out.println(newJsonArray.toString());
    }
}