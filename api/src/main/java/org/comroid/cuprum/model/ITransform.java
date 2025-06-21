package org.comroid.cuprum.model;

import org.comroid.api.data.Vector;

public interface ITransform extends PositionSupplier, ScaleSupplier {
    default void setPosition(Vector position) {
        throw new UnsupportedOperationException("Cannot set position on " + this);
    }

    default void setScale(Vector position) {
        throw new UnsupportedOperationException("Cannot set scale on " + this);
    }
}
