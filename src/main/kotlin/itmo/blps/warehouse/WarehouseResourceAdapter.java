package itmo.blps.warehouse;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;
import java.io.Serializable;

public class WarehouseResourceAdapter implements ResourceAdapter, Serializable {
    private static final long serialVersionUID = 1L;

    public WarehouseResourceAdapter() {}

    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {}

    @Override
    public void stop() {}

    @Override
    public void endpointActivation(MessageEndpointFactory ad, ActivationSpec spec) throws ResourceException {}

    @Override
    public void endpointDeactivation(MessageEndpointFactory ad, ActivationSpec spec) {}

    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        return new XAResource[0];
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