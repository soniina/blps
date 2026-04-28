package itmo.blps.warehouse;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;
import jakarta.resource.cci.ConnectionFactory;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Set;
import javax.security.auth.Subject;

//@ConnectionDefinition(
//        connectionFactory = ConnectionFactory.class,
//        connectionFactoryImpl = WarehouseConnectionFactory.class,
//        connection = jakarta.resource.cci.Connection.class,
//        connectionImpl = WarehouseConnection.class
//)
public class ManagedConnectionFactoryImpl implements ManagedConnectionFactory, ResourceAdapterAssociation, Serializable {
    private ResourceAdapter ra;
    private PrintWriter logWriter;

    public ManagedConnectionFactoryImpl() {}

    @Override
    public Object createConnectionFactory(ConnectionManager cm) throws ResourceException { return new WarehouseConnectionFactory(this, cm); }

    @Override
    public Object createConnectionFactory() throws ResourceException { return new WarehouseConnectionFactory(this, null); }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo info) throws ResourceException { return new WarehouseManagedConnection(); }

    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo info) throws ResourceException {
        return (ManagedConnection) connectionSet.stream().findFirst().orElse(null);
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
        if (obj == null) return false;
        if (obj == this) return true;
        return (obj instanceof ManagedConnectionFactoryImpl);
    }

    @Override
    public int hashCode() {
        return ManagedConnectionFactoryImpl.class.hashCode();
    }
}