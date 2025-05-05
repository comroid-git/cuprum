package org.comroid.cuprum.component;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.Component;
import org.comroid.cuprum.component.model.basic.Conductive;

public interface Wire extends Component, Conductive {
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
}
