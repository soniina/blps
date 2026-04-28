package itmo.blps.warehouse;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.*;

public class WarehouseConnection implements Connection {
    public Interaction createInteraction() throws ResourceException { return null; }
    public LocalTransaction getLocalTransaction() throws ResourceException { return null; }
    public ConnectionMetaData getMetaData() throws ResourceException { return null; }
    public ResultSetInfo getResultSetInfo() throws ResourceException { return null; }
    public void close() throws ResourceException {}
}