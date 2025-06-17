package org.comroid.cuprum.component.model.abstr;

import org.comroid.cuprum.simulation.WireMesh;

public interface WireMeshComponent extends CuprumComponent, SnappableComponent {
    WireMesh getWireMesh();

    void setWireMesh(WireMesh mesh);

    default boolean removeFromAncestors() {
        return getWireMesh().remove(this);
    }
}
