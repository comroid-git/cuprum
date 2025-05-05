package org.comroid.cuprum.component.model.basic;

import org.comroid.annotations.Default;

public interface Resistive {
    /**
     * @return resistance in Ohm
     */
    @Default("470")
    double getResistance();
}
