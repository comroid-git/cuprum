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
import org.comroid.cuprum.component.model.abstr.WireMeshPart;
import org.comroid.cuprum.component.model.basic.Conductive;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.simulation.WireMeshNode;
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
    @ToString(exclude = { "wire", "wireMeshNode" }, doNotUseGetters = true)
    @EqualsAndHashCode(exclude = { "wire", "wireMeshNode" }, doNotUseGetters = true)
    class Segment implements Conductive, WireMeshPart, PositionSupplier {
        Wire wire;
        Vector position;
        @NonFinal @Nullable Double length = null, crossSection = null;
        @NonFinal @Nullable Material material = null;
        @Setter @NonFinal @Nullable WireMeshNode wireMeshNode = null;

        public WireMeshNode getWireMeshNode() {
            return !isWireMeshNodeInitialized() ? wireMeshNode = new WireMeshNode(this, position) : wireMeshNode;
        }

        @Override
        public boolean isWireMeshNodeInitialized() {
            return wireMeshNode != null;
        }

        public void setWireMeshNode(WireMeshNode mesh, boolean recursive) {
            setWireMeshNode(mesh);
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
        public void setLength(@Nullable Double length) {
            this.length = length;
        }

        @Override
        public double getCrossSection() {
            return Optional.ofNullable(crossSection).orElseGet(wire::getCrossSection);
        }

        @Override
        public void setCrossSection(@Nullable Double crossSection) {
            this.crossSection = crossSection;
        }

        @Override
        public Material getMaterial() {
            return Optional.ofNullable(material).orElseGet(wire::getMaterial);
        }

        @Override
        public void setMaterial(@Nullable Material material) {
            this.material = material;
        }
    }
}
