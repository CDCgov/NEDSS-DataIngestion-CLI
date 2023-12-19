package gov.cdc.dataingestion.util;

import gov.cdc.dataingestion.model.AuthModel;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AuthUtil {
    private String randomSaltForJwtEncryption = "DICLI_RandomSalt";
    TokenUtil tokenUtil = new TokenUtil(randomSaltForJwtEncryption);

    public String getResponseFromDIService(AuthModel authModel, String name) {
        try {
            UsernamePasswordCredentials credentials = null;
            if(name.equals("register")) {
                credentials = new UsernamePasswordCredentials(authModel.getAdminUser(), new String(authModel.getAdminPassword()));
            }
            else {
                if(authModel.getUsername() != null) {
                    credentials = new UsernamePasswordCredentials(authModel.getUsername(), new String(authModel.getPassword()));
                }

            }

            CloseableHttpClient httpsClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;

            String apiToken = tokenUtil.retrieveToken();
            int statusCode = 0;
            if(name.equals("status") || name.equals("dltmessages")) {
                HttpGet getRequest = new HttpGet(authModel.getServiceEndpoint());
                getRequest.addHeader("authorization", "Bearer " + apiToken);
                getRequest.addHeader("accept", "*/*");
                response = httpsClient.execute(getRequest);
            }
            else {

                HttpPost postRequest = new HttpPost(authModel.getServiceEndpoint());
                if(credentials != null) {
                    Header authHeader = new BasicScheme(StandardCharsets.UTF_8).authenticate(credentials, postRequest, null);
                    postRequest.addHeader(authHeader);
                }
                else {
                    postRequest.addHeader("authorization", "Bearer " + apiToken);
                }
                postRequest.addHeader("accept", "*/*");

                if (name.equals("injecthl7")) {
                    postRequest.addHeader("msgType", "HL7");
                    postRequest.addHeader("validationActive", "true");
                }
                if(name.equals("register")) {
                    postRequest.addHeader("Content-Type", "application/json");
                }
                else {
                    postRequest.addHeader("Content-Type", "text/plain");
                }

                if(authModel.getRequestBody() != null && !authModel.getRequestBody().isEmpty() && !authModel.getRequestBody().equals("")) {
                    HttpEntity body = new StringEntity(authModel.getRequestBody());
                    postRequest.setEntity(body);
                }

                response = httpsClient.execute(postRequest);
            }
            if(response == null) {
                return "Unable to execute the request. Internal Server Error.";
            }
            else {
                statusCode = response.getStatusLine().getStatusCode();
            }
            if (statusCode == 200) {
                InputStream content = response.getEntity().getContent();
                String result = convertInputStreamToString(content);
                httpsClient.close();
                return result;
            } else if (statusCode == 401) {
                httpsClient.close();
                return "Unauthorized. Username/password is incorrect.";
            } else {
                String result;
                if (name.equals("hl7validation")) {
                    InputStream content = response.getEntity().getContent();
                    result = convertInputStreamToString(content);
                } else {
                    return "Something went wrong on the server side. Please check the logs.";
                }
                httpsClient.close();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred: " + e.getMessage();
        }
    }

    private String convertInputStreamToString(InputStream content) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}