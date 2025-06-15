package org.comroid.cuprum.component;

import lombok.With;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.component.model.basic.Conductive;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public interface Wire extends SimComponent, Conductive {
    List<Segment> getSegments();

    @Override
    default double getLength() {
        var iter = getSegments().iterator();
        if (!iter.hasNext()) throw new IllegalStateException("Wire has no points");
        var last = iter.next();
        var dist = 0.0;
        while (iter.hasNext()) {
            var it = iter.next();
            dist += it.length != null ? it.length : Vector.dist(last.position, it.position);
            last = it;
        }
        return dist;
    }

    @Override
    default Stream<Vector.N2> getSnappingPoints() {
        return getSegments().stream().map(Segment::position);
    }

    @Override
    default UniformRenderObject createRenderObject(RenderObjectAdapter adapter) {
        return adapter.createWireLine(this);
    }

    @Override
    default int priorityLayer() {
        return -100;
    }

    boolean addSegment(Segment segment);

    record Segment(Vector.N2 position, @With @Nullable Double length) {
        public Segment(Vector.N2 position) {
            this(position, null);
        }
    }
}
