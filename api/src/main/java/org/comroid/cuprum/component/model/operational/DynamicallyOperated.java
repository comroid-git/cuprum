package org.comroid.cuprum.component.model.operational;

public interface DynamicallyOperated extends ManuallyOperated, OperatorChild {
    default boolean isOperated() {
        return isManuallyOperated() || getParent() != null && getParent().isSufficientlyPowered();
    }
}
