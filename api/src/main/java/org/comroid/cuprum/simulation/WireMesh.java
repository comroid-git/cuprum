package org.comroid.cuprum.simulation;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.WireMeshComponent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Value
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class WireMesh extends ConcurrentHashMap<Vector.N2, WireMeshComponent> {
    UUID id = UUID.randomUUID();

    public WireMesh() {}

    @Override
    public String toString() {
        return "WireMesh{}";
    }

    public WireMesh add(WireMeshComponent component, Vector.N2 position) {
        WireMesh it = this, other = component.getWireMesh();
        if (!it.equals(other)) component.setWireMesh(it = other);
        it.put(position, component);
        return it;
    }

    public boolean remove(WireMeshComponent component) {
        component.setWireMesh(null);
        return super.remove(component) != null;
    }
}
