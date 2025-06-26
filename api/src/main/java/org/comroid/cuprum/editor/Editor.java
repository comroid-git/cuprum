package org.comroid.cuprum.editor;

import org.comroid.api.func.util.Streams;
import org.comroid.cuprum.component.model.abstr.CuprumComponent;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.component.model.abstr.ElectronicComponent;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.component.model.abstr.SnappableComponent;
import org.comroid.cuprum.component.model.abstr.WireMeshPart;
import org.comroid.cuprum.editor.model.SnappingPoint;
import org.comroid.cuprum.editor.model.ViewContainer;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.simulation.WireMeshNode;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Editor extends ViewContainer {
    Collection<? extends CuprumComponent> getCuprumComponents();

    default Stream<? extends EditorComponent> getEditorComponents() {
        return getCuprumComponents().stream().flatMap(Streams.cast(EditorComponent.class));
    }

    default Stream<? extends ElectronicComponent> getWireMeshComponents() {
        return getCuprumComponents().stream().flatMap(Streams.cast(ElectronicComponent.class));
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

    default void rescanMesh() {
        getWireMeshComponents().flatMap(SnappableComponent::getSnappingPoints).forEach(this::rescanMesh);
    }

    default void rescanMesh(PositionSupplier trigger) {
        var position = trigger.getPosition();

        // find all parts at this trigger
        var parts = getWireMeshComponents().flatMap(wmc -> wmc.findOverlap(position))
                .map(WireMeshNode.OverlapPoint::component)
                .collect(Collectors.toUnmodifiableSet());

        // skip if there is no parts
        if (parts.isEmpty() || (parts.stream().allMatch(trigger::equals))) return;

        // find or create a WireMeshNode
        var any = parts.stream().findAny().orElseThrow();
        var meshNode = parts.stream()
                .filter(WireMeshPart::isWireMeshNodeInitialized)
                .findAny()
                .map(WireMeshPart::getWireMeshNode)
                // try create new WireMeshNode at any's position
                .or(() -> Optional.of(new WireMeshNode(any, any.as(ITransform.class).assertion().getPosition())))
                // validate that the WireMeshNode is actually at this position
                .filter(wmn -> wmn.getInitPosition().equals(position))
                // otherwise just make a blank WireMeshNode
                .orElseGet(() -> new WireMeshNode(any, position));

        // set WireMeshNode for new components
        //noinspection SuspiciousMethodCalls
        parts.stream().filter(Predicate.not(meshNode::contains)).forEach(wmp -> wmp.setWireMeshNode(meshNode));

        // report node if it contains components at weird positions
        meshNode.checkSuspicious();
    }
}
