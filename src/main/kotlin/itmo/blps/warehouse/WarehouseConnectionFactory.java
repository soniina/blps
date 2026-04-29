package itmo.blps.warehouse;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.*;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ManagedConnectionFactory;
import javax.naming.Reference;
import java.io.Serializable;

public class WarehouseConnectionFactory implements ConnectionFactory, Serializable {
    private static final long serialVersionUID = 1L;
    private Reference ref;
    private ManagedConnectionFactory mcf;
    private ConnectionManager cm;

    public WarehouseConnectionFactory() {}

    public WarehouseConnectionFactory(ManagedConnectionFactory mcf, ConnectionManager cm) {
        this.mcf = mcf;
        this.cm = cm;
    }

    @Override
    public Connection getConnection() throws ResourceException {
        // если ConnectionManager равен null, значит мы работаем вне сервера (unmanaged mode)
        if (cm == null) {
            // mcf приводим к реализации, чтобы достать настройки Jira
            ManagedConnectionFactoryImpl mcfImpl = (ManagedConnectionFactoryImpl) mcf;

            return new WarehouseConnection(
                    mcfImpl.getJiraUrl(),
                    mcfImpl.getApiToken(),
                    mcfImpl.getUserEmail()
            );
        }

        // В режиме сервера (managed mode) cm сам создаст соединение через ManagedConnection
        return (Connection) cm.allocateConnection(mcf, null);
    }

    @Override
    public Connection getConnection(ConnectionSpec spec) throws ResourceException {
        return getConnection();
    }

    @Override
    public RecordFactory getRecordFactory() throws ResourceException { return null; }
    @Override
    public ResourceAdapterMetaData getMetaData() throws ResourceException { return null; }
    @Override
    public void setReference(Reference ref) { this.ref = ref; }
    @Override
    public Reference getReference() { return ref; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WarehouseConnectionFactory other = (WarehouseConnectionFactory) obj;
        if (mcf == null) return other.mcf == null;
        return mcf.equals(other.mcf);
    }

    @Override
    public int hashCode() {
        return (mcf != null ? mcf.hashCode() : 0);
    }
}