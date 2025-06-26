package org.comroid.cuprum.physics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.comroid.annotations.Default;

import java.awt.*;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public enum Material {
    NEGLIGIBLE(0, Color.BLACK),
    @Default COPPER(58, new Color(0xc68346)),
    ALUMINIUM(37.8, new Color(0xd0d5db)),
    GOLD(47.6, new Color(0xc9a752)),
    IRON(10, new Color(0xbeb9b3)),
    SILVER(67.1, new Color(0xbfbfbd));

    double conductivity;
    Color  color;
}
