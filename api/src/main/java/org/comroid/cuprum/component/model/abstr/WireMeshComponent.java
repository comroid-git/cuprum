package org.comroid.cuprum.component.model.abstr;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.simulation.WireMesh;

import java.util.stream.Stream;

public interface WireMeshComponent extends CuprumComponent, SnappableComponent, WireMeshContainer {
    default Stream<WireMesh.OverlapPoint> findOverlap(PositionSupplier posSrc) {
        var pos = posSrc.getPosition();
        return getSnappingPoints().filter(snap -> Vector.dist(pos, snap.getPosition()) < 8)
                .map(snap -> new WireMesh.OverlapPoint(snap instanceof WireMeshContainer wmc ? wmc : this, pos));
    }
}
