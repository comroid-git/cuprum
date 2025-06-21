package org.comroid.cuprum.component;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.component.model.abstr.WireMeshContainer;
import org.comroid.cuprum.component.model.basic.Conductive;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.simulation.WireMesh;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Wire extends SimulationComponent, Conductive {
    List<Segment> getSegments();

    @Override
    default double getLength() {
        return getSegments().stream().mapToDouble(Segment::getLength).sum();
    }

    @Override
    default Stream<PositionSupplier> getSnappingPoints() {
        return Stream.concat(Stream.of(this), getSegments().stream());
    }

    @Override
    default UniformRenderObject createRenderObject(RenderObjectAdapter adapter) {
        return adapter.createWireLine(this);
    }

    @Override
    default int priorityLayer() {
        return -100;
    }

    void addSegment(Segment segment);

    @Value
    @AllArgsConstructor
    @RequiredArgsConstructor
    @ToString(exclude = { "wire", "wireMesh" }, doNotUseGetters = true)
    @EqualsAndHashCode(exclude = { "wire", "wireMesh" }, doNotUseGetters = true)
    class Segment implements Conductive, WireMeshContainer, PositionSupplier {
        Wire      wire;
        Vector position;
        @Setter @NonFinal @Nullable Double   length   = null;
        @Setter @NonFinal @Nullable WireMesh wireMesh = null;

        public WireMesh getWireMesh() {
            return !isWireMeshInitialized() ? wireMesh = new WireMesh(this, position) : wireMesh;
        }

        @Override
        public boolean isWireMeshInitialized() {
            return wireMesh != null;
        }

        @Override
        public void setWireMesh(WireMesh mesh, boolean recursive) {
            setWireMesh(mesh);
        }

        @Override
        public double getLength() {
            return Optional.ofNullable(length).orElseGet(() -> {
                var segments = wire.getSegments();
                var index    = segments.indexOf(this);
                var prev     = index == 0 ? wire.getPosition() : segments.get(index - 1).position;
                return Vector.dist(prev, position);
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
    }
}
