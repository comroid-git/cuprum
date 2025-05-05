package org.comroid.cuprum.simulation;

import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public class ElectricContext {
    double voltage, amperage;

    //public class Series extends ElectricContext {}
    //public class Parallel extends ElectricContext {}
}
