package itmo.blps.citilink.warehouse;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class WarehouseConnection implements Connection {

    private final String jiraUrl;
    private final String apiToken;
    private final String userEmail;

    public WarehouseConnection(String jiraUrl, String apiToken, String userEmail) {
        this.jiraUrl = jiraUrl;
        this.apiToken = apiToken;
        this.userEmail = userEmail;
    }


    public String createJiraIssue(String summary, String description) throws ResourceException {
        try {
            String auth = userEmail + ":" + apiToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String safeSummary = summary.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
            String safeDescription = description.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");

            String jsonBody = "{" +
                    "\"fields\": {" +
                    "\"project\": {\"key\": \"SCRUM\"}," +
                    "\"summary\": \"" + safeSummary + "\"," +
                    "\"description\": \"" + safeDescription + "\"," +
                    "\"issuetype\": {\"name\": \"Task\"}" +
                    "}" +
                    "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jiraUrl + "/rest/api/2/issue"))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/json; charset=utf-8") // Указываем кодировку в заголовке
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, java.nio.charset.StandardCharsets.UTF_8)) // И здесь!
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                System.err.println("Sent JSON: " + jsonBody);
                throw new ResourceException("Jira error: " + response.body());
            }

            String responseBody = response.body();
            String key = responseBody.split("\"key\":\"")[1].split("\"")[0];

            System.out.println("Jira issue created: " + key);
            return key;

        } catch (Exception e) {
            throw new ResourceException("Failed to create Jira issue", e);
        }
    }

    public void deleteJiraIssue(String issueKey) throws ResourceException {
        try {
            String auth = userEmail + ":" + apiToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jiraUrl + "/rest/api/2/issue/" + issueKey))
                    .header("Authorization", "Basic " + encodedAuth)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                System.out.println("Failed to delete Jira issue: " + response.body());
            } else {
                System.out.println("Jira issue " + issueKey + " deleted successfully (Rollback done)");
            }
        } catch (Exception e) {
            throw new ResourceException("Rollback failed", e);
        }
    }

    @Override public Interaction createInteraction() throws ResourceException { return null; }
    @Override public LocalTransaction getLocalTransaction() throws ResourceException { return null; }
    @Override public ConnectionMetaData getMetaData() throws ResourceException { return null; }
    @Override public ResultSetInfo getResultSetInfo() throws ResourceException { return null; }
    @Override public void close() throws ResourceException {}
}