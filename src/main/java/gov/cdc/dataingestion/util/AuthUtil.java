package gov.cdc.dataingestion.util;

import gov.cdc.dataingestion.model.AuthModel;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthenticationException;
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
    private static final String TOKEN_KEY = "apiJwt";
    private static final String CLIENT_ID_KEY = "clientId";
    private static final String CLIENT_SECRET_KEY = "clientSecret";
    EncryptionUtil encryptionUtil = new EncryptionUtil(randomSaltForJwtEncryption);

    public String getResponseFromDIService(AuthModel authModel, String name) {
        try {
            UsernamePasswordCredentials credentials = null;
            String apiToken = null;
            if(authModel.getClientId() != null) {
                credentials = new UsernamePasswordCredentials(authModel.getClientId(), new String(authModel.getClientSecret()));
            }

            CloseableHttpClient httpsClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;

            int statusCode = 0;
            response = executeHttpRequest(authModel, name, credentials, httpsClient, apiToken);
            if(response == null) {
                return "Unable to execute the request. Internal Server Error.";
            }
            else {
                statusCode = response.getStatusLine().getStatusCode();
            }
            return getResultFromResponse(name, httpsClient, response, statusCode);
        } catch (Exception e) {
            return "Exception occurred: " + e.getMessage();
        }
    }

    private String getResultFromResponse(String name, CloseableHttpClient httpsClient, CloseableHttpResponse response, int statusCode) throws IOException {
        if (statusCode == 200) {
            InputStream content = response.getEntity().getContent();
            String result = convertInputStreamToString(content);
            httpsClient.close();
            return result;
        } else if (statusCode == 401) {
            httpsClient.close();
            return "Unauthorized: Your token may have expired. Please review and confirm your authentication settings.";
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
    }

    private CloseableHttpResponse executeHttpRequest(AuthModel authModel, String name, UsernamePasswordCredentials credentials, CloseableHttpClient httpsClient, String apiToken) throws IOException, AuthenticationException {
        CloseableHttpResponse response;
        String clientId;
        String clientSecret;
        if(authModel.getClientId() == null) {
            apiToken = encryptionUtil.retrieveString(TOKEN_KEY);
            clientId = encryptionUtil.retrieveString(CLIENT_ID_KEY);
            clientSecret = encryptionUtil.retrieveString(CLIENT_SECRET_KEY);
        }
        else {
            clientId = authModel.getClientId();
            clientSecret = new String(authModel.getClientSecret());
        }
        if(name.equals("status") || name.equals("dltmessages")) {
            HttpGet getRequest = new HttpGet(authModel.getServiceEndpoint());
            getRequest.addHeader("authorization", "Bearer " + apiToken);
            getRequest.addHeader("accept", "*/*");
            getRequest.addHeader(CLIENT_ID_KEY, clientId);
            getRequest.addHeader(CLIENT_SECRET_KEY, clientSecret);
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
            postRequest.addHeader(CLIENT_ID_KEY, clientId);
            postRequest.addHeader(CLIENT_SECRET_KEY, clientSecret);
            postRequest.addHeader("accept", "*/*");

            if (name.equals("injecthl7")) {
                postRequest.addHeader("msgType", "HL7");
                postRequest.addHeader("validationActive", "true");
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
        return response;
    }

    String convertInputStreamToString(InputStream content) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}