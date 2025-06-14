package org.comroid.cuprum.simulation.component;

import lombok.Data;
import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.spatial.Transform;

@Data
public abstract class SimComponentBase implements SimComponent {
    protected ITransform transform;

    public SimComponentBase() {
        this(new Transform());
    }

    public SimComponentBase(ITransform transform) {
        this.transform = transform;
    }
}
