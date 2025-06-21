package org.comroid.cuprum.component.model.basic;

import org.comroid.annotations.Default;
import org.comroid.cuprum.component.model.abstr.CuprumComponent;
import org.comroid.cuprum.physics.Material;
import org.jetbrains.annotations.Nullable;

public interface Conductive extends CuprumComponent {
    /**
     * @return length of the conductor
     */
    @Default("1")
    double getLength();

    default void setLength(@Nullable Double length) {
        throw new AbstractMethodError("Cannot set length for " + this);
    }

    /**
     * @return cross-section of the conductor
     */
    @Default("1.5")
    double getCrossSection();

    default void setCrossSection(@Nullable Double crossSection) {
        throw new AbstractMethodError("Cannot set cross section for " + this);
    }

    /**
     * @return material of the conductor
     */
    @Default("Material.COPPER")
    Material getMaterial();

    default void setMaterial(Material material) {
        throw new AbstractMethodError("Cannot set material for " + this);
    }
}
