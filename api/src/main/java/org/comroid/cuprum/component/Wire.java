package org.comroid.cuprum.component;

import lombok.With;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.component.model.basic.Conductive;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.physics.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public interface Wire extends SimulationComponent, Conductive {
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

    record Segment(Wire wire, Vector.N2 position, @With @Nullable Double length) implements Conductive {
        public Segment(Wire wire, Vector.N2 position) {
            this(wire, position, null);
        }

        @Override
        public double getLength() {
            return Optional.ofNullable(length).orElseGet(() -> {
                var iter = wire.getSegments().iterator();
                if (!iter.hasNext()) throw new IllegalStateException("Wire has no points");

                Segment last = null, it = null;
                while (iter.hasNext()) {
                    last = it;
                    if (equals(it = iter.next())) break;
                }
                if (last == null) throw new IllegalStateException("Segment is first in Wire");
                if (!equals(it)) throw new IllegalStateException("Segment not found in Wire");

                return Vector.dist(last.position, it.position);
            });
        }

        @Override
        public double getCrossSection() {
            return wire.getCrossSection();
        }

        @Override
        public Material getMaterial() {
            return wire.getMaterial();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Segment segment && Objects.equals(length(), segment.length()) && Objects.equals(position(), segment.position());
        }

        @Override
        public int hashCode() {
            return Objects.hash(position(), length());
        }

        @Override
        public @NotNull String toString() {
            return "Segment{position=%s, length=%s, material=%s}".formatted(position, length, getMaterial());
        }
    }
}
