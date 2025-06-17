package org.comroid.cuprum.component.model.abstr;

import org.comroid.api.data.Vector;

import java.util.stream.Stream;

public interface SnappableComponent {
    Stream<Vector.N2> getSnappingPoints();
}
