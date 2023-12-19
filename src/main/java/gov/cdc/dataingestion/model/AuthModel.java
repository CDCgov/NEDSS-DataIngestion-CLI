package gov.cdc.dataingestion.model;

public class AuthModel {

    private String username;
    private char[] password;
    private String adminUser;
    private char[] adminPassword;
    private String serviceEndpoint;
    private String requestBody;

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public char[] getPassword() { return password; }

    public void setPassword(char[] password) { this.password = password; }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public char[] getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(char[] adminPassword) {
        this.adminPassword = adminPassword;
    }

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
