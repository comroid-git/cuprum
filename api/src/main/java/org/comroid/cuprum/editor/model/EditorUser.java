package org.comroid.cuprum.editor.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.delegate.EngineDelegate;
import org.comroid.cuprum.editor.Editor;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.spatial.Transform;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Log
@Getter
@RequiredArgsConstructor
@ToString(exclude = { "editor" })
@EqualsAndHashCode(exclude = { "editor" })
public class EditorUser {
    private final             Editor                        editor;
    private final             Set<UniformRenderObject>      renderObjects = new HashSet<>();
    private final             Transform                     cursor        = new Transform();
    private                   EditorMode                    mode          = EditorMode.INTERACT;
    private @Setter @Nullable Supplier<SimulationComponent> componentCtor;
    private @Nullable         SimulationComponent           component;

    public Set<UniformRenderObject> getRenderObjects() {
        synchronized (renderObjects) {
            return Set.copyOf(renderObjects);
        }
    }

    public @Nullable EditorComponent getComponent() {
        return component == null ? createComponent() : component;
    }

    public synchronized void setMode(EditorMode mode) {
        component     = null;
        componentCtor = switch (this.mode = mode) {
            case TOOL_WIRE -> EngineDelegate.INSTANCE::createWire;
            case TOOL_SOLDER -> EngineDelegate.INSTANCE::createConnectionPoint;
            case TOOL_OBJECT -> componentCtor;
            default -> null;
        };
    }

    public synchronized @Nullable EditorComponent createComponent() {
        try {
            return componentCtor != null ? component = componentCtor.get() : null;
        } finally {
            if (component != null && !(component instanceof Wire)) component.setTransform(cursor);
        }
    }

    public void clear() {
        setMode(EditorMode.INTERACT);

        synchronized (renderObjects) {
            renderObjects.clear();
        }
    }

    public synchronized void refreshVisual() {
        synchronized (renderObjects) {
            renderObjects.clear();

            if (component != null) {
                var renderObject = component.createRenderObject(editor.getRenderObjectAdapter());
                if (renderObject != null) renderObjects.add(renderObject);
            }
        }
    }

    public synchronized void triggerClickPrimary(PositionSupplier source, boolean shift) {
        var position = source.getPosition();
        getComponent();

        switch (mode) {
            case INTERACT:
                // todo Interact
                break;

            case TOOL_WIRE:
                // buffer position
                if (component instanceof Wire wire) {
                    if (wire.getSegments().isEmpty() && !position.equals(Vector.N2.Zero) && wire.getTransform()
                            .getPosition()
                            .equals(Vector.N2.Zero)) wire.setTransform(new Transform(position));
                    else wire.addSegment(new Wire.Segment(wire, position));
                }
                break;

            case TOOL_SOLDER:
                // do not place solder when already snapped on one
                if (Optional.ofNullable(editor.getSnappingPoint())
                        .map(EditorComponent.Holder::getComponent)
                        .filter(ConnectionPoint.class::isInstance)
                        .isPresent()) return;
            case TOOL_OBJECT:
                if (component == null) {
                    setMode(EditorMode.INTERACT);
                    break;
                }

                component.setTransform(new Transform(position));
                editor.add(component);
                editor.rescanMesh(component, source);
                this.component = null;
                break;

            case REMOVE:
                var snap = editor.getSnappingPoint();
                if (snap == null) return;
                var comp = snap.getComponent();
                if (comp instanceof SimulationComponent simComp && !simComp.getWireMeshNode().remove(simComp))
                    throw new RuntimeException("Could not remove component %s from its WireMesh".formatted(simComp));
                break;
        }
    }

    public synchronized void triggerClickSecondary(PositionSupplier source, boolean shift) {
        var position = source.getPosition();
        getComponent();

        //noinspection SwitchStatementWithTooFewBranches
        switch (mode) {
            case TOOL_WIRE:
                if (!(component instanceof Wire wire)) {
                    setMode(EditorMode.INTERACT);
                    return;
                }

                // place last point and finalize
                //if (shift) wire.addSegment(new Wire.Segment(wire, position));
                if (!wire.getSegments().isEmpty()) {
                    editor.add(component);
                    component.getSnappingPoints().forEach(snap -> editor.rescanMesh(component, snap));
                }
                setMode(shift ? EditorMode.TOOL_WIRE : EditorMode.INTERACT);

                break;

            default:
                setMode(EditorMode.INTERACT);
                break;
        }
    }
}
