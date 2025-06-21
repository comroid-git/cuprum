package org.comroid.cuprum.component.model.abstr;

import org.comroid.cuprum.model.PositionSupplier;

import java.util.stream.Stream;

public interface SnappableComponent extends CuprumComponent {
    Stream<PositionSupplier> getSnappingPoints();
}
