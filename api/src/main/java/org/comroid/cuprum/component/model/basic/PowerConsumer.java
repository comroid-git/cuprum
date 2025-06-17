package org.comroid.cuprum.component.model.basic;

import org.comroid.annotations.Default;
import org.comroid.cuprum.simulation.ElectricContext;

public interface PowerConsumer {
    @Default("100")
    double getNominalWattage();

    @Default("0.98")
    double getNominalPowerFactor();

    boolean isSufficientlyPowered(ElectricContext ctx);
}
