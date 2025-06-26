package org.comroid.cuprum.component;

import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.component.model.contact.AlternatingContacts;
import org.comroid.cuprum.component.model.operational.DynamicallyOperated;
import org.comroid.cuprum.component.model.operational.OperatorChild;
import org.comroid.cuprum.simulation.ElectricContext;
import org.jetbrains.annotations.Nullable;

public interface Contactor extends EditorComponent, AlternatingContacts, OperatorChild, DynamicallyOperated {
    /**
     * @return coil that triggers this contactor
     */
    @Nullable ContactorCoil getParent();

    default ConnectionPoint getOutputPoint(ElectricContext ctx) {
        return isOperated(ctx) ? getNormallyOpenedContact() : getNormallyClosedContact();
    }
}
