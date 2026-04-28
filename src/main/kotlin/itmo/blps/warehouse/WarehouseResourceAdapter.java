package itmo.blps.warehouse;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource; // ВАЖНО: javax, а не jakarta
import java.io.Serializable;

//@Connector(displayName = "Warehouse RA", vendorName = "ITMO", eisType = "Warehouse", version = "1.0")
public class WarehouseResourceAdapter implements ResourceAdapter, Serializable {

    public WarehouseResourceAdapter() {} // Пустой конструктор обязателен

    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {}

    @Override
    public void stop() {}

    @Override
    public void endpointActivation(MessageEndpointFactory ad, ActivationSpec spec) throws ResourceException {}

    @Override
    public void endpointDeactivation(MessageEndpointFactory ad, ActivationSpec spec) {}

    @Override
    public javax.transaction.xa.XAResource[] getXAResources(jakarta.resource.spi.ActivationSpec[] specs)
            throws jakarta.resource.ResourceException {
        return new javax.transaction.xa.XAResource[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        return (obj instanceof WarehouseResourceAdapter);
    }

    @Override
    public int hashCode() {
        return WarehouseResourceAdapter.class.hashCode();
    }
}