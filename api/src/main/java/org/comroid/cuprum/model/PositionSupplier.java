package org.comroid.cuprum.model;

import org.comroid.api.data.Vector;

public interface PositionSupplier {
    static PositionSupplier of(Vector position) {
        return () -> position;
    }

    Vector getPosition();
}
