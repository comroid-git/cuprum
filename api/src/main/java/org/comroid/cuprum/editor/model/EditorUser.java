package org.comroid.cuprum.editor.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.EngineDelegate;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.editor.Editor;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.spatial.Transform;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Log
@Getter
@RequiredArgsConstructor
@ToString(exclude = { "editor" })
@EqualsAndHashCode(exclude = { "editor" })
public class EditorUser {
    private final             Editor                   editor;
    private final             Set<UniformRenderObject> renderObjects = new HashSet<>();
    private final             Transform                cursor        = new Transform();
    private                   EditorMode               mode          = EditorMode.INTERACT;
    private @Setter @Nullable Supplier<SimComponent>   componentCtor;
    private @Nullable         SimComponent             component;

    public @Nullable SimComponent getComponent() {
        return component == null && componentCtor != null ? createComponent() : component;
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

    public synchronized @Nullable SimComponent createComponent() {
        try {
            return componentCtor != null ? component = componentCtor.get() : null;
        } finally {
            if (component != null && !(component instanceof Wire)) component.setTransform(cursor);
        }
    }

    public synchronized void refreshVisual() {
        renderObjects.clear();

        if (component != null) {
            var renderObject = component.createRenderObject(editor.getRenderObjectAdapter());
            if (renderObject != null) renderObjects.add(renderObject);
        }
    }

    public synchronized void triggerClickPrimary(Vector.N2 position, boolean shift) {
        getComponent();

        switch (mode) {
            case INTERACT:
                // todo Interact
                return;

            case TOOL_WIRE:
                // buffer position
                if (component instanceof Wire wire && wire.addSegment(new Wire.Segment(position))) return;
                throw new IllegalStateException("Failed to trigger click primary on wire");

            case TOOL_SOLDER, TOOL_OBJECT:
                if (component == null) {
                    setMode(EditorMode.INTERACT);
                    return;
                }

                component.setTransform(new Transform(position));
                editor.add(component);
                this.component = null;

                break;

            case REMOVE:
                // todo Remove
                return;
        }
    }

    public synchronized void triggerClickSecondary(Vector.N2 position, boolean shift) {
        getComponent();

        //noinspection SwitchStatementWithTooFewBranches
        switch (mode) {
            case TOOL_WIRE:
                if (!(component instanceof Wire wire)) {
                    setMode(EditorMode.INTERACT);
                    return;
                }

                // place last point and finalize
                if (shift) wire.addSegment(new Wire.Segment(position));
                editor.add(component);
                setMode(shift ? EditorMode.TOOL_WIRE : EditorMode.INTERACT);

                break;

            default:
                setMode(EditorMode.INTERACT);
                break;
        }
    }
}
