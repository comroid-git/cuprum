package org.comroid.cuprum.simulation;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.WireMeshComponent;
import org.comroid.cuprum.component.model.abstr.WireMeshContainer;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Value
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class WireMesh extends HashSet<WireMesh.OverlapPoint> {
    UUID              id = UUID.randomUUID();
    WireMeshComponent initComponent;
    Vector.N2         initComponentPosition;

    public WireMesh(WireMeshComponent initComponent, Vector.N2 initComponentPosition) {
        this.initComponent         = initComponent;
        this.initComponentPosition = initComponentPosition;

        clear();
    }

    @Override
    public boolean isEmpty() {
        return size() <= 1;
    }

    @Override
    public boolean contains(Object o) {
        return find(o).filter(o::equals).isPresent();
    }

    @Override
    public boolean add(OverlapPoint overlapPoint) {
        overlapPoint.component.setWireMesh(this, false);
        return super.add(overlapPoint);
    }

    @Override
    public boolean remove(Object o) {
        return find(0).filter(other -> {
            other.component.setWireMesh(null);
            return remove(other);
        }).isPresent();
    }

    @Override
    public void clear() {
        super.clear();
        add(new OverlapPoint(initComponent, initComponentPosition));
    }

    public Optional<OverlapPoint> find(Object o) {
        return stream().filter(overlap -> overlap.equals(o) || overlap.component.equals(o) || overlap.position.equals(o))
                .findAny();
    }

    @Override
    public String toString() {
        return "WireMesh{}";
    }

    public WireMesh integrate(OverlapPoint overlap) {
        var      component = overlap.component;
        WireMesh it        = this, other = component.isWireMeshInitialized() ? component.getWireMesh() : null;

        if (other != null && !other.isEmpty() && ((other != null && it.isEmpty()) || !it.equals(other)))
            // try other if we only contain ourselves
            component.setWireMesh(it = other);

        // dont forget to add component to mesh
        it.add(overlap);
        return it;
    }

    public record OverlapPoint(WireMeshContainer component, Vector.N2 position) {}
}
