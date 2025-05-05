package org.comroid.cuprum.component.model.operational;

import org.comroid.cuprum.simulation.ElectricContext;

public interface FlexiblyOperated extends ManuallyOperated, OperatorChild {
    default boolean isOperated(ElectricContext ctx) {
        return isManuallyOperated() || getParent() == null && getParent().isSufficientlyPowered(ctx);
    }
}
