package org.comroid.cuprum.delegate;

import org.comroid.annotations.Instance;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Contactor;
import org.comroid.cuprum.component.Wire;

import java.util.ServiceLoader;

public interface EngineDelegate {
    @Instance EngineDelegate INSTANCE = ServiceLoader.load(EngineDelegate.class).findFirst().orElseThrow();

    Wire createWire();

    ConnectionPoint createConnectionPoint();

    Contactor createNormallyOpenedContactor();

    Contactor createNormallyClosedContactor();

    Contactor createAlternatingClosedContactor();
}
