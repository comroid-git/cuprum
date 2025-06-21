package org.comroid.cuprum.simulation.component;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.model.ITransform;

@Value
@EqualsAndHashCode(callSuper = true)
public class SolderImpl extends SimulationComponentBase implements ConnectionPoint {
    public SolderImpl(ITransform transform) {
        super(transform);
    }
}
