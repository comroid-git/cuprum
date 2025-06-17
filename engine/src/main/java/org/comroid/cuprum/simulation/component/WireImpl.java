package org.comroid.cuprum.simulation.component;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.simulation.WireMesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Value
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WireImpl extends EditorComponentBase implements Wire {
    List<Segment> segments = new ArrayList<>();
    double        crossSection;
    Material      material;
    @Setter @NonFinal WireMesh wireMesh = new WireMesh();

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    @Override
    public boolean addSegment(Segment segment) {
        return segments.add(segment);
    }
}
