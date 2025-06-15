package org.comroid.cuprum.simulation.component;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.physics.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Value
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WireImpl extends SimComponentBase implements Wire {
    List<Segment> segments = new ArrayList<>();
    double        crossSection;
    Material      material;

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    @Override
    public boolean addSegment(Segment segment) {
        return segments.add(segment);
    }
}
