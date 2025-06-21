package org.comroid.cuprum.component.model.abstr;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.simulation.WireMesh;

import java.util.stream.Stream;

public interface WireMeshComponent extends CuprumComponent, SnappableComponent, WireMeshContainer {
    default Stream<WireMesh.OverlapPoint> findOverlap(Vector.N2 snap) {
        return getSnappingPoints().filter(pos -> Vector.dist(snap, pos) < 8)
                .map(pos -> new WireMesh.OverlapPoint(this, pos));
    }
}
