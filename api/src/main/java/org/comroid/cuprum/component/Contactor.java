package org.comroid.cuprum.component;

import org.comroid.cuprum.component.model.Alternating;
import org.comroid.cuprum.component.model.Component;
import org.jetbrains.annotations.Nullable;

public interface Contactor extends Component, Alternating {
    /**
     * @return coil that triggers this contactor
     */
    @Nullable ContactorCoil getParent();
}
