package org.comroid.cuprum.editor;

import org.comroid.api.func.util.Streams;
import org.comroid.cuprum.component.model.abstr.CuprumComponent;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.component.model.abstr.WireMeshComponent;
import org.comroid.cuprum.editor.model.SnappingPoint;
import org.comroid.cuprum.editor.model.ViewContainer;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.simulation.WireMesh;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Editor extends ViewContainer {
    Collection<? extends CuprumComponent> getCuprumComponents();

    default Stream<? extends EditorComponent> getEditorComponents() {
        return getCuprumComponents().stream().flatMap(Streams.cast(EditorComponent.class));
    }

    default Stream<? extends WireMeshComponent> getWireMeshComponents() {
        return getCuprumComponents().stream().flatMap(Streams.cast(WireMeshComponent.class));
    }

    RenderObjectAdapter getRenderObjectAdapter();

    @Nullable SnappingPoint getSnappingPoint();

    default void add(EditorComponent... components) {
        Arrays.stream(components).forEach(this::add);
    }

    void add(EditorComponent component);

    default long remove(SimulationComponent... components) {
        return Arrays.stream(components).filter(this::remove).count();
    }

    boolean remove(SimulationComponent component);

    default void rescanMesh(WireMeshComponent newComponent, PositionSupplier position) {
        var overlaps = getWireMeshComponents().filter(Predicate.not(newComponent::equals))
                .flatMap(wmc -> newComponent.getSnappingPoints().flatMap(src -> wmc.findOverlap(src)))
                .toList();
        if (overlaps.isEmpty()) return;
        WireMesh mesh = newComponent.getWireMesh();
        for (var overlap : overlaps)
            mesh = mesh.integrate(overlap);
        newComponent.setWireMesh(mesh);
    }
}
