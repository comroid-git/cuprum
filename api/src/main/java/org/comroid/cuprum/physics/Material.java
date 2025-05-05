package org.comroid.cuprum.physics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.comroid.annotations.Default;

import java.awt.*;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public enum Material {
    @Default COPPER(58, new Color(0xc68346)), ALUMINIUM(37.8, new Color(0xd0d5db));
    double conductivity;
    Color  color;
}
