package org.comroid.cuprum.simulation.component;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.simulation.model.SimulationComponentBase;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Value
@ToString(exclude = "segments")
@EqualsAndHashCode(callSuper = true)
public class WireImpl extends SimulationComponentBase implements Wire {
    List<Segment> segments = new ArrayList<>();
    @NonFinal @Nullable Material material;
    @NonFinal @Nullable Double   crossSection;

    public WireImpl(ITransform transform, PositionSupplier relativeSecondPoint) {
        this(transform, Double.NaN, Material.NEGLIGIBLE);

        addSegment(new Segment(this, relativeSecondPoint));
    }

    public WireImpl(ITransform transform, double crossSection, @Nullable Material material) {
        super(transform);

        this.crossSection = crossSection;
        this.material     = material;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    @Override
    public void addSegment(Segment segment) {
        segments.add(segment);
    }

    @Override
    public void setLength(@Nullable Double length) {
        var first = getSegments().getFirst();
        first.setLength(getLength() - first.getLength() + length);
    }

    public double getCrossSection() {
        return crossSection == null ? 1.5 : crossSection;
    }

    @Override
    public void setCrossSection(@Nullable Double crossSection) {
        this.crossSection = crossSection;
    }

    public Material getMaterial() {
        return Objects.requireNonNullElse(material, Material.COPPER);
    }

    @Override
    public void setMaterial(@Nullable Material material) {
        this.material = material;
    }
}
