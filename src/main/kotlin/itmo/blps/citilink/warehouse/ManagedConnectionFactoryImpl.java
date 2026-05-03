package itmo.blps.citilink.warehouse;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Set;
import javax.security.auth.Subject;

public class ManagedConnectionFactoryImpl implements ManagedConnectionFactory, ResourceAdapterAssociation, Serializable {
    private static final long serialVersionUID = 1L;

    private ResourceAdapter ra;
    private PrintWriter logWriter;

    // интеграция с Jira
    private String jiraUrl;
    private String apiToken;
    private String userEmail;

    public ManagedConnectionFactoryImpl() {
    }

    public String getJiraUrl() { return jiraUrl; }
    public void setJiraUrl(String jiraUrl) { this.jiraUrl = jiraUrl; }

    public String getApiToken() { return apiToken; }
    public void setApiToken(String apiToken) { this.apiToken = apiToken; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    // контракт JCA
    @Override
    public Object createConnectionFactory(ConnectionManager cm) throws ResourceException {
        return new WarehouseConnectionFactory(this, cm);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return new WarehouseConnectionFactory(this, null);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo info) throws ResourceException {
        return new WarehouseManagedConnection(getJiraUrl(), getApiToken(), getUserEmail());
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo info) throws ResourceException {
        if (connectionSet == null || connectionSet.isEmpty()) return null;
        // возвращаем первое подходящее соединение из пула
        return (ManagedConnection) connectionSet.iterator().next();
    }

    @Override
    public void setLogWriter(PrintWriter out) { this.logWriter = out; }
    @Override
    public PrintWriter getLogWriter() { return logWriter; }
    @Override
    public ResourceAdapter getResourceAdapter() { return ra; }
    @Override
    public void setResourceAdapter(ResourceAdapter ra) { this.ra = ra; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ManagedConnectionFactoryImpl other = (ManagedConnectionFactoryImpl) obj;

        if (jiraUrl == null ? other.jiraUrl != null : !jiraUrl.equals(other.jiraUrl)) return false;
        if (apiToken == null ? other.apiToken != null : !apiToken.equals(other.apiToken)) return false;
        return userEmail == null ? other.userEmail == null : userEmail.equals(other.userEmail);
    }

    @Override
    public int hashCode() {
        int result = jiraUrl != null ? jiraUrl.hashCode() : 0;
        result = 31 * result + (apiToken != null ? apiToken.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        return result;
    }
}