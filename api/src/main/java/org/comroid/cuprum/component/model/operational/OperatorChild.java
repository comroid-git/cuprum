package org.comroid.cuprum.component.model.operational;

import org.jetbrains.annotations.Nullable;

public interface OperatorChild {
    @Nullable Operator getParent();
}
