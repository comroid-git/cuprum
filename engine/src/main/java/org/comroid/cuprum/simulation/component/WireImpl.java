package org.comroid.cuprum.simulation.component;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.physics.Material;
import org.jetbrains.annotations.Nullable;

@Value
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WireImpl extends SimComponentBase implements Wire {
    double   crossSection;
    Material material;
    @NonFinal @Nullable Double length;

    public double getLength() {
        return length == null ? Wire.super.getLength() : length;
    }
}
