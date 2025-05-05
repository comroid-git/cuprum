package org.comroid.cuprum.component.model;

import org.comroid.annotations.Default;
import org.comroid.cuprum.physics.Material;

public interface Conductive {
    /**
     * @return length of the conductor
     */
    @Default("1")
    double getLength();

    /**
     * @return cross-section of the conductor
     */
    @Default("1.5")
    double getCrossSection();

    /**
     * @return material of the conductor
     */
    @Default("Material.COPPER")
    Material getMaterial();
}
