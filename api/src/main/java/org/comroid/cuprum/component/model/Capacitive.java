package org.comroid.cuprum.component.model;

import org.comroid.annotations.Default;

public interface Capacitive {
    /**
     * @return capacity in Farad
     */
    @Default("0.000_001")
    double getCapacity();
}
