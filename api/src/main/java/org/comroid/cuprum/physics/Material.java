package org.comroid.cuprum.physics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.comroid.annotations.Default;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public enum Material {
    @Default COPPER(58), ALUMINIUM(37.8);
    double conductivity;
}
