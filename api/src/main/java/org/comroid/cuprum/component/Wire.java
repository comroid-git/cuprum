package org.comroid.cuprum.component;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.component.model.basic.Conductive;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;

import static java.lang.Math.*;

public interface Wire extends SimComponent, Conductive {
    /**
     * @return start position of the wire
     */
    default Vector.N2 getPositionA() {
        return getTransform().getPosition();
    }

    /**
     * @return end position of the wire
     */
    default Vector.N2 getPositionB() {
        var transform = getTransform();
        return transform.getPosition().addi(transform.getScale()).as2();
    }

    @Override
    default double getLength() {
        Vector.N2 pa = getPositionA(), pb = getPositionB();
        Vector    a  = Vector.min(pa, pb), b = Vector.max(pa, pb);
        return sqrt(pow(b.getX() - a.getX(), 2) + pow(b.getY() - a.getY(), 2) + pow(b.getZ() - a.getZ(), 2));
    }

    @Override
    default UniformRenderObject createRenderObject(RenderObjectAdapter adapter) {
        return adapter.createWireLine(this);
    }
}
