package org.comroid.cuprum.simulation.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.NonFinal;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.simulation.WireMeshNode;

@Data
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public abstract class SimulationComponentBase implements SimulationComponent {
    protected           ITransform   transform;
    protected @NonFinal WireMeshNode wireMeshNode = null;

    public SimulationComponentBase(ITransform transform) {
        this.transform = transform;
    }

    public final WireMeshNode getWireMeshNode() {
        return !isWireMeshNodeInitialized() ? wireMeshNode = new WireMeshNode(this, getPosition()) : wireMeshNode;
    }

    @Override
    public boolean isWireMeshNodeInitialized() {
        return wireMeshNode != null;
    }

    public final void setWireMeshNode(WireMeshNode wireMeshNode, boolean recursive) {
        if (recursive && isWireMeshNodeInitialized() && !this.wireMeshNode.equals(wireMeshNode)) {
            wireMeshNode.addAll(this.wireMeshNode);
            wireMeshNode.clear();
        }
        this.wireMeshNode = wireMeshNode;
    }
}
