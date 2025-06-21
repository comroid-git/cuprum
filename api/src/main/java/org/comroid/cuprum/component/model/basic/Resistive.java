package org.comroid.cuprum.component.model.basic;

import org.comroid.annotations.Default;
import org.comroid.cuprum.component.model.abstr.CuprumComponent;

public interface Resistive extends CuprumComponent {
    /**
     * @return resistance in Ohm
     */
    @Default("470")
    double getResistance();

    default void setResistance(double resistance) {
        throw new AbstractMethodError("Cannot set resistance for " + this);
    }
}
