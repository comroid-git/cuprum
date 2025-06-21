package org.comroid.cuprum.model;

import org.comroid.api.data.Vector;

public interface ScaleSupplier {
    static ScaleSupplier of(Vector scale) {
        return () -> scale;
    }

    Vector getScale();
}
