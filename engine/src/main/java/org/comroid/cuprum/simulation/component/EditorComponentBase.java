package org.comroid.cuprum.simulation.component;

import lombok.Data;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.spatial.Transform;

@Data
public abstract class EditorComponentBase implements EditorComponent {
    protected ITransform transform;

    public EditorComponentBase() {
        this(new Transform());
    }

    public EditorComponentBase(ITransform transform) {
        this.transform = transform;
    }
}
