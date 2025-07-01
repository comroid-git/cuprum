package org.comroid.cuprum.spatial;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.editor.View;
import org.comroid.cuprum.editor.model.ViewContainer;
import org.comroid.cuprum.model.ITransform;

@Value
@NonFinal
@ToString(of = { "position", "scale" })
@EqualsAndHashCode(of = { "position", "scale" })
public class Transform implements ITransform, Cloneable {
    @NonFinal @Setter Vector position, scale;

    public Transform() {
        this(Vector.N2.Zero);
    }

    public Transform(Vector position) {
        this(position, Vector.N2.One);
    }

    public Transform(Vector position, Vector scale) {
        this.position = position;
        this.scale    = scale;
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Transform clone() {
        return new Transform(position.clone(), scale.clone());
    }

    public interface Holder extends ITransform {
        ITransform getTransform();

        default void setTransform(ITransform transform) {
            throw new UnsupportedOperationException("Cannot set Transform on " + this);
        }

        @Override
        default Vector getPosition() {
            return getTransform().getPosition();
        }

        @Override
        default Vector getScale() {
            return getTransform().getScale();
        }
    }

    @Value
    @ToString(of = { "transform", "positionOffset", "scaleOffset" })
    @EqualsAndHashCode(of = { "transform", "positionOffset", "scaleOffset" })
    public static class Relative implements Holder {
        ITransform transform;
        @NonFinal @Setter Vector positionOffset, scaleOffset;

        public Relative(ITransform transform, Vector positionOffset) {
            this(transform, positionOffset, Vector.One);
        }

        public Relative(ITransform transform, Vector positionOffset, Vector scaleOffset) {
            this.transform      = transform;
            this.positionOffset = positionOffset;
            this.scaleOffset    = scaleOffset;
        }

        @Override
        public Vector getPosition() {
            return transform.getPosition().addi(positionOffset);
        }

        @Override
        public Vector getScale() {
            return transform.getScale().muli(scaleOffset);
        }
    }

    @Value
    @ToString(of = { "transform", "view" })
    @EqualsAndHashCode(of = { "transform", "view" })
    public static class EditorToCanvasAdapter implements Holder, ViewContainer {
        ITransform transform;
        View       view;

        @Override
        public Vector getPosition() {
            return view.transformEditorToCanvas(transform.getPosition());
        }

        @Override
        public Vector getScale() {
            return transform.getScale();
        }
    }
}
