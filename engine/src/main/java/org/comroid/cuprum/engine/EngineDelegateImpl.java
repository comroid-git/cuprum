package org.comroid.cuprum.engine;

import lombok.Value;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Contactor;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.delegate.EngineDelegate;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.simulation.component.ContactorImpl;
import org.comroid.cuprum.simulation.component.SolderImpl;
import org.comroid.cuprum.simulation.component.WireImpl;
import org.comroid.cuprum.spatial.Transform;

@Value
public class EngineDelegateImpl implements EngineDelegate {
    @Override
    public Wire createWire() {
        return new WireImpl(new Transform(), 1.5, Material.COPPER);
    }

    @Override
    public ConnectionPoint createConnectionPoint() {
        return new SolderImpl(new Transform());
    }

    @Override
    public Contactor createNormallyOpenedContactor() {
        return ContactorImpl.createNormallyOpened(new Transform());
    }

    @Override
    public Contactor createNormallyClosedContactor() {
        return ContactorImpl.createNormallyClosed(new Transform());
    }

    @Override
    public Contactor createAlternatingClosedContactor() {
        return ContactorImpl.createAlternating(new Transform());
    }
}
