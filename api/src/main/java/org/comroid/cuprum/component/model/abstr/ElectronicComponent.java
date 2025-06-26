package org.comroid.cuprum.component.model.abstr;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.simulation.WireMeshNode;

import java.util.stream.Stream;

public interface ElectronicComponent extends CuprumComponent, SnappableComponent, WireMeshPart {
    double CONNECTION_DISTANCE = 8;

    default Stream<WireMeshNode.OverlapPoint> findOverlap(Vector pos) {
        return getSnappingPoints().filter(snap -> Vector.dist(pos, snap.getPosition()) < CONNECTION_DISTANCE)
                .map(snap -> new WireMeshNode.OverlapPoint(snap instanceof WireMeshPart wmc ? wmc : this, pos));
    }
}
