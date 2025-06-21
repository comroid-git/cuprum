package org.comroid.cuprum.component.model.abstr;

import org.comroid.cuprum.simulation.WireMeshNode;

public interface WireMeshPart {
    WireMeshNode getWireMeshNode();

    default void setWireMeshNode(WireMeshNode mesh) {
        setWireMeshNode(mesh, true);
    }

    boolean isWireMeshNodeInitialized();

    void setWireMeshNode(WireMeshNode mesh, boolean recursive);

    @SuppressWarnings("SuspiciousMethodCalls")
    default boolean removeFromAncestors() {
        return getWireMeshNode().remove(this);
    }
}
