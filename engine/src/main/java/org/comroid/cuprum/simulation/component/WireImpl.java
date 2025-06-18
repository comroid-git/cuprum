package org.comroid.cuprum.simulation.component;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.simulation.WireMesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class WireImpl extends SimulationComponentBase implements Wire {
    List<Segment> segments = new ArrayList<>();
    double        crossSection;
    Material      material;

    public WireImpl(ITransform transform, double crossSection, Material material) {
        super(transform);
        this.crossSection = crossSection;
        this.material     = material;
    }

    public WireMesh getWireMesh() {
        return wireMesh == null ? wireMesh = new WireMesh(this, segments.getFirst().position()) : wireMesh;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    @Override
    public boolean addSegment(Segment segment) {
        return segments.add(segment);
    }
}
