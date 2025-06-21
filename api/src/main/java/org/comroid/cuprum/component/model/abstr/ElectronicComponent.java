package org.comroid.cuprum.component.model.abstr;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.simulation.WireMeshNode;

import java.util.stream.Stream;

public interface ElectronicComponent extends CuprumComponent, SnappableComponent, WireMeshPart {
    default Stream<WireMeshNode.OverlapPoint> findOverlap(PositionSupplier src) {
        var pos = src.getPosition();
        return getSnappingPoints().filter(snap -> Vector.dist(pos, snap.getPosition()) < 8)
                .map(snap -> new WireMeshNode.OverlapPoint(snap instanceof WireMeshPart wmc ? wmc : this, pos));
    }
}
