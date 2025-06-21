package org.comroid.cuprum.component.model.abstr;

import org.comroid.cuprum.simulation.WireMesh;

public interface WireMeshContainer {
    WireMesh getWireMesh();

    default void setWireMesh(WireMesh mesh) {
        setWireMesh(mesh, true);
    }

    boolean isWireMeshInitialized();

    void setWireMesh(WireMesh mesh, boolean recursive);

    @SuppressWarnings("SuspiciousMethodCalls")
    default boolean removeFromAncestors() {
        return getWireMesh().remove(this);
    }
}
