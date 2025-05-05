package org.comroid.cuprum.spatial;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.editor.View;

@Value
@NonFinal
public abstract class Transform {
    @NonFinal @Setter Vector.N2 position, scale;

    public Transform(Vector.N2 position) {
        this(position, Vector.N2.One);
    }

    public Transform(Vector.N2 position, Vector.N2 scale) {
        this.position = position;
        this.scale    = scale;
    }

    public interface Holder {
        Transform getTransform();

        default Vector.N2 getPosition() {
            return getTransform().getPosition();
        }

        default Vector.N2 getScale() {
            return getTransform().getScale();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public class Relative extends Transform {
        @NonFinal @Setter Vector.N2 positionOffset, scaleOffset;

        @Override
        public Vector.N2 getPosition() {
            return Transform.this.getPosition().addi(positionOffset).as2();
        }

        @Override
        public Vector.N2 getScale() {
            return Transform.this.getScale().muli(scaleOffset).as2();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public class CanvasToViewAdapter extends Transform {
        View view;

        @Override
        public Vector.N2 getPosition() {
            return view.transformToView(Transform.this.getPosition());
        }

        @Override
        public Vector.N2 getScale() {
            return Transform.this.getScale();
        }
    }
}
