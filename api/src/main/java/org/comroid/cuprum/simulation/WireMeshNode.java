package org.comroid.cuprum.simulation;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.api.info.Log;
import org.comroid.cuprum.component.model.abstr.CuprumComponent;
import org.comroid.cuprum.component.model.abstr.WireMeshPart;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

@Value
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class WireMeshNode extends HashSet<WireMeshNode.OverlapPoint> implements CuprumComponent {
    UUID         id = UUID.randomUUID();
    WireMeshPart initParent;
    Vector       initPosition;

    public WireMeshNode(WireMeshPart initParent, Vector initPosition) {
        this.initParent   = initParent;
        this.initPosition = initPosition;

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
        overlapPoint.component.setWireMeshNode(this, false);
        return super.add(overlapPoint);
    }

    @Override
    public boolean remove(Object o) {
        return find(0).filter(other -> {
            other.component.setWireMeshNode(null);
            return remove(other);
        }).isPresent();
    }

    @Override
    public void clear() {
        super.clear();
        add(new OverlapPoint(initParent, initPosition));
    }

    public Optional<OverlapPoint> find(Object o) {
        return stream().filter(overlap -> overlap.equals(o) || overlap.component.equals(o) || overlap.position.equals(o))
                .findAny();
    }

    @Override
    public String toString() {
        return "WireMesh{}";
    }

    public WireMeshNode integrate(OverlapPoint overlap) {
        var          component = overlap.component;
        WireMeshNode it        = this, other = component.isWireMeshNodeInitialized()
                                               ? component.getWireMeshNode()
                                               : null;

        if (other != null && !other.isEmpty() && ((other != null && it.isEmpty()) || !it.equals(other)))
            // try other if we only contain ourselves
            component.setWireMeshNode(it = other);

        // dont forget to add component to mesh
        it.add(overlap);
        return it;
    }

    public void checkSuspicious() {
        if (stream().anyMatch(overlap -> !overlap.position.equals(initPosition))) Log.at(Level.WARNING,
                "Suspicious " + this);
    }

    public record OverlapPoint(WireMeshPart component, Vector position) {}
}
