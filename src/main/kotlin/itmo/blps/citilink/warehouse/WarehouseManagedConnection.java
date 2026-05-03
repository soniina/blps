package itmo.blps.citilink.warehouse;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;
import javax.transaction.xa.XAResource;
import javax.security.auth.Subject;
import java.io.PrintWriter;

public class WarehouseManagedConnection implements ManagedConnection {

    private final String jiraUrl;
    private final String apiToken;
    private final String userEmail;

    public WarehouseManagedConnection(String jiraUrl, String apiToken, String userEmail) {
        this.jiraUrl = jiraUrl;
        this.apiToken = apiToken;
        this.userEmail = userEmail;
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo info) throws ResourceException {
        return new WarehouseConnection(jiraUrl, apiToken, userEmail);
    }

    @Override public void destroy() throws ResourceException {}
    @Override public void cleanup() throws ResourceException {}
    @Override public void associateConnection(Object connection) throws ResourceException {}
    @Override public void addConnectionEventListener(ConnectionEventListener listener) {}
    @Override public void removeConnectionEventListener(ConnectionEventListener listener) {}
    @Override public XAResource getXAResource() throws ResourceException { return null; }
    @Override public LocalTransaction getLocalTransaction() throws ResourceException { return null; }
    @Override public void setLogWriter(PrintWriter out) {}
    @Override public PrintWriter getLogWriter() { return null; }
    @Override public ManagedConnectionMetaData getMetaData() throws ResourceException { return null; }
}