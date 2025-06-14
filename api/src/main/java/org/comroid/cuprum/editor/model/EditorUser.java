package org.comroid.cuprum.editor.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.EngineDelegate;
import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.editor.Editor;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.spatial.Transform;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@Log
@Data
@EqualsAndHashCode(exclude = "editor")
public class EditorUser {
    private final     Editor                   editor;
    private final     Set<UniformRenderObject> renderObjects = new HashSet<>();
    private final     Transform                cursor        = new Transform();
    private           EditorMode               mode          = EditorMode.INTERACT;
    private @Nullable SimComponent             component;
    private @Nullable Vector.N2                bufferPosition;

    public void setMode(EditorMode mode) {
        setComponent(switch (this.mode = mode) {
            case TOOL_WIRE -> EngineDelegate.INSTANCE.createWire();
            case TOOL_SOLDER -> EngineDelegate.INSTANCE.createConnectionPoint();
            case TOOL_OBJECT -> component;
            default -> null;
        });
    }

    public void setComponent(@Nullable SimComponent component) {
        this.component = component;

        if (component != null) try {
            component.setTransform(cursor);
        } catch (UnsupportedOperationException e) {
            log.log(Level.FINE, "Wrongly tried to attach object to cursor: " + component, e);
        }

        refreshVisual();
    }

    public void refreshVisual() {
        renderObjects.clear();

        if (component != null) {
            var renderObject = component.createRenderObject(editor.getRenderObjectAdapter());
            if (renderObject != null) renderObjects.add(renderObject);
        }
    }

    public void triggerClickPrimary(Vector.N2 position) {
        switch (mode) {
            case INTERACT:
                // todo Interact
                return;
            case TOOL_WIRE:
                // buffer position
                bufferPosition = position;
                return;
            case TOOL_SOLDER, TOOL_OBJECT:
                if (component == null) {
                    mode = EditorMode.INTERACT;
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

        refreshVisual();
    }

    public void triggerClickSecondary(Vector.N2 position) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (mode) {
            case TOOL_WIRE:
                if (component == null || bufferPosition == null) {
                    mode = EditorMode.INTERACT;
                    return;
                }

                // place last point and finalize
                component.setTransform(new Transform(bufferPosition, Vector.rel(bufferPosition, position).as2()));
                editor.add(component);
                this.component = null;

                break;
            default:
                mode = EditorMode.INTERACT;
                break;
        }

        refreshVisual();
    }
}
