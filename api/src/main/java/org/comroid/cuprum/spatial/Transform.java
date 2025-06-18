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
    @ToString(of = { "transform", "positionOffset", "scaleOffset" })
    @EqualsAndHashCode(of = { "transform", "positionOffset", "scaleOffset" })
    public static class Relative implements Holder {
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
    @ToString(of = { "transform", "view" })
    @EqualsAndHashCode(of = { "transform", "view" })
    public static class EditorToCanvasAdapter implements Holder, ViewContainer {
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
