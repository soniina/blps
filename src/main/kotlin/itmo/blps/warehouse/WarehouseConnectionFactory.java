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

    /**
     * Обязательный пустой конструктор для сериализации
     */
    public WarehouseConnectionFactory() {
    }

    /**
     * Основной конструктор, используемый ManagedConnectionFactory
     */
    public WarehouseConnectionFactory(ManagedConnectionFactory mcf, ConnectionManager cm) {
        this.mcf = mcf;
        this.cm = cm;
    }

    @Override
    public Connection getConnection() throws ResourceException {
        if (cm == null) {
            throw new ResourceException("ConnectionManager is null");
        }
        return (Connection) cm.allocateConnection(mcf, null);
    }

    @Override
    public Connection getConnection(ConnectionSpec spec) throws ResourceException {
        return getConnection();
    }

    @Override
    public RecordFactory getRecordFactory() throws ResourceException {
        return null;
    }

    @Override
    public ResourceAdapterMetaData getMetaData() throws ResourceException {
        return null;
    }

    @Override
    public void setReference(Reference ref) {
        this.ref = ref;
    }

    @Override
    public Reference getReference() {
        return ref;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        return (obj instanceof WarehouseConnectionFactory);
    }

    @Override
    public int hashCode() {
        return WarehouseConnectionFactory.class.hashCode();
    }
}