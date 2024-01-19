package gov.cdc.dataingestion.model;

public class AuthModel {

    private String clientId;
    private char[] clientSecret;

    private String serviceEndpoint;
    private String requestBody;

    public String getClientId() { return clientId; }

    public void setClientId(String username) { this.clientId = username; }

    public char[] getClientSecret() { return clientSecret; }

    public void setClientSecret(char[] password) { this.clientSecret = password; }

    public String getServiceEndpoint() {
        return serviceEndpoint;
    }

    public void setServiceEndpoint(String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
