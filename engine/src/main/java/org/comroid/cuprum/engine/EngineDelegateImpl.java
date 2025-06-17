package org.comroid.cuprum.engine;

import lombok.Value;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.delegate.EngineDelegate;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.simulation.component.SolderImpl;
import org.comroid.cuprum.simulation.component.WireImpl;

@Value
public class EngineDelegateImpl implements EngineDelegate {
    @Override
    public Wire createWire() {
        return new WireImpl(1.5, Material.COPPER);
    }

    @Override
    public ConnectionPoint createConnectionPoint() {
        return new SolderImpl();
    }
}
