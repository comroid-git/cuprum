package org.comroid.cuprum.component;

import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.component.model.abstr.SnappableComponent;
import org.comroid.cuprum.component.model.basic.Conductive;
import org.comroid.cuprum.component.model.contact.AlternatingContacts;
import org.comroid.cuprum.component.model.operational.DynamicallyOperated;
import org.comroid.cuprum.component.model.operational.OperatorChild;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.simulation.ElectricContext;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

public interface Contactor
        extends EditorComponent, AlternatingContacts, OperatorChild, DynamicallyOperated, Conductive {
    @Override
    default double getLength() {
        return 0;
    }

    @Override
    default double getCrossSection() {
        return Double.NaN;
    }

    @Override
    default Material getMaterial() {
        return Material.NEGLIGIBLE;
    }

    /**
     * @return coil that triggers this contactor
     */
    @Nullable ContactorCoil getParent();

    default boolean isDynamic() {
        return getParent() != null;
    }

    default Type getContactorType() {
        return Arrays.stream(Type.values())
                .sorted(Comparator.<Type>comparingInt(Enum::ordinal).reversed())
                .filter(type -> type.test(this))
                .findFirst()
                .orElseThrow();
    }

    default SnappableComponent getActiveOutputContact(ElectricContext ctx) {
        return isOperated(ctx) ? getNormallyOpenedContact() : getNormallyClosedContact();
    }

    enum Type implements Predicate<Contactor> {
        Dummy {
            @Override
            public boolean test(Contactor contactor) {
                return false;
            }
        }, NormallyOpened {
            @Override
            public boolean test(Contactor contactor) {
                return contactor.getNormallyOpenedContact() != null;
            }
        }, NormallyClosed {
            @Override
            public boolean test(Contactor contactor) {
                return contactor.getNormallyClosedContact() != null;
            }
        }, Alternating {
            @Override
            public boolean test(Contactor contactor) {
                return NormallyOpened.test(contactor) && NormallyClosed.test(contactor);
            }
        }
    }
}
