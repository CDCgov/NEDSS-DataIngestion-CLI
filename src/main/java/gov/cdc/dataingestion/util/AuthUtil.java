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

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AuthUtil {

    public String getResponseFromDIService(AuthModel authModel, String name) {
        try {
            UsernamePasswordCredentials credentials;
            if(name.equals("register")) {
                credentials = new UsernamePasswordCredentials(authModel.getAdminUser(), new String(authModel.getAdminPassword()));
            }
            else {
                credentials = new UsernamePasswordCredentials(authModel.getUsername(), new String(authModel.getPassword()));
            }

            CloseableHttpClient httpsClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            int statusCode = 0;
            if(name.equals("status") || name.equals("dltmessages")) {
                HttpGet getRequest = new HttpGet(authModel.getServiceEndpoint());
                Header authHeader = new BasicScheme(StandardCharsets.UTF_8).authenticate(credentials, getRequest, null);
                getRequest.addHeader("accept", "*/*");
                getRequest.addHeader(authHeader);
                response = httpsClient.execute(getRequest);
            }
            else {

                HttpPost postRequest = new HttpPost(authModel.getServiceEndpoint());
                Header authHeader = new BasicScheme(StandardCharsets.UTF_8).authenticate(credentials, postRequest, null);
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
                postRequest.addHeader(authHeader);

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
