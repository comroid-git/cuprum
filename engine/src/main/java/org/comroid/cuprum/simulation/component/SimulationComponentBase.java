package org.comroid.cuprum.simulation.component;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.NonFinal;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.simulation.WireMesh;

@Data
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public abstract class SimulationComponentBase implements SimulationComponent {
    protected           ITransform transform;
    protected @NonFinal WireMesh   wireMesh = null;

    public SimulationComponentBase(ITransform transform) {
        this.transform = transform;
    }

    public final WireMesh getWireMesh() {
        return wireMesh == null ? wireMesh = new WireMesh(this, getPosition()) : wireMesh;
    }

    @Override
    public boolean isWireMeshInitialized() {
        return wireMesh != null;
    }

    public final void setWireMesh(WireMesh wireMesh, boolean recursive) {
        if (recursive && isWireMeshInitialized() && !this.wireMesh.equals(wireMesh)) {
            wireMesh.addAll(this.wireMesh);
            wireMesh.clear();
        }
        this.wireMesh = wireMesh;
    }
}
