package org.comroid.cuprum.model;

import org.comroid.api.data.Vector;

public interface ITransform extends PositionSupplier {
    default void setPosition(Vector.N2 position) {
        throw new UnsupportedOperationException("Cannot set position on " + this);
    }

    Vector.N2 getScale();

    default void setScale(Vector.N2 position) {
        throw new UnsupportedOperationException("Cannot set scale on " + this);
    }
}
