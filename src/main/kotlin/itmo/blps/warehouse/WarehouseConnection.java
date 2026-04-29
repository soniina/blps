package itmo.blps.warehouse;

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

    // настройки из ManagedConnection
    public WarehouseConnection(String jiraUrl, String apiToken, String userEmail) {
        this.jiraUrl = jiraUrl;
        this.apiToken = apiToken;
        this.userEmail = userEmail;
    }

    /**
     * Метод для создания задачи в Jira
     */
    public void createJiraIssue(String summary, String description) throws ResourceException {
        try {
            String auth = userEmail + ":" + apiToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            String jsonBody = String.format("""
                {
                  "fields": {
                    "project": { "key": "SCRUM" },
                    "summary": "%s",
                    "description": "%s",
                    "issuetype": { "name": "Task" }
                  }
                }
                """, summary, description);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jiraUrl + "/rest/api/2/issue"))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Отправляем запрос
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Проверяем статус ответа (201 Created)
            if (response.statusCode() >= 400) {
                throw new ResourceException("Jira API error: " + response.statusCode() + " - " + response.body());
            }

            System.out.println("Jira issue created successfully. Response: " + response.body());

        } catch (Exception e) {
            throw new ResourceException("Failed to create Jira issue", e);
        }
    }

    @Override
    public Interaction createInteraction() throws ResourceException { return null; }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException { return null; }

    @Override
    public ConnectionMetaData getMetaData() throws ResourceException { return null; }

    @Override
    public ResultSetInfo getResultSetInfo() throws ResourceException { return null; }

    @Override
    public void close() throws ResourceException {
        // Логика закрытия соединения, если требуется
    }
}