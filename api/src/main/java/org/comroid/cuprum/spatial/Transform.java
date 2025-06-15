package org.comroid.cuprum.spatial;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.editor.View;
import org.comroid.cuprum.editor.model.ViewContainer;
import org.comroid.cuprum.model.ITransform;

@Value
@NonFinal
public class Transform implements ITransform {
    @NonFinal @Setter Vector.N2 position, scale;

    public Transform() {
        this(Vector.N2.Zero);
    }

    public Transform(Vector.N2 position) {
        this(position, Vector.N2.One);
    }

    public Transform(Vector.N2 position, Vector.N2 scale) {
        this.position = position;
        this.scale    = scale;
    }

    public interface Holder extends ITransform {
        ITransform getTransform();

        default void setTransform(ITransform transform) {
            throw new UnsupportedOperationException("Cannot set Transform on " + this);
        }

        @Override
        default Vector.N2 getPosition() {
            return getTransform().getPosition();
        }

        @Override
        default Vector.N2 getScale() {
            return getTransform().getScale();
        }
    }

    @Value
    public static class Relative implements ITransform, Holder {
        ITransform transform;
        @NonFinal @Setter Vector.N2 positionOffset, scaleOffset;

        @Override
        public Vector.N2 getPosition() {
            return transform.getPosition().addi(positionOffset).as2();
        }

        @Override
        public Vector.N2 getScale() {
            return transform.getScale().muli(scaleOffset).as2();
        }
    }

    @Value
    public static class CanvasToViewAdapter implements ITransform, Holder, ViewContainer {
        ITransform transform;
        View       view;

        @Override
        public Vector.N2 getPosition() {
            return view.transformEditorToCanvas(transform.getPosition());
        }

        @Override
        public Vector.N2 getScale() {
            return transform.getScale();
        }
    }
}
