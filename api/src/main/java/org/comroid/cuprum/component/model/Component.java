package org.comroid.cuprum.component.model;

import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.spatial.Transform;

public interface Component extends Transform.Holder {
    interface Holder extends Transform.Holder {
        Component getComponent();

        @Override
        default ITransform getTransform() {
            return getComponent().getTransform();
        }
    }
}
