package org.comroid.cuprum.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.component.model.basic.Conductive;
import org.comroid.cuprum.component.model.contact.AlternatingContacts;
import org.comroid.cuprum.component.model.operational.DynamicallyOperated;
import org.comroid.cuprum.component.model.operational.OperatorChild;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.spatial.Transform;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

public interface Contactor
        extends SimulationComponent, AlternatingContacts, OperatorChild, DynamicallyOperated, Conductive {
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

    default Type getActiveContactType() {
        return switch (getContactorType()) {
            case Common -> Type.Common;
            case NormallyOpened, Alternating -> isOperated() ? Type.NormallyOpened : Type.NormallyClosed;
            case NormallyClosed -> isOperated() ? Type.NormallyClosed : Type.NormallyOpened;
        };
    }

    default @Nullable ConnectionPoint getActiveOutputContact() {
        return isOperated() ? getNormallyOpenedContact() : getNormallyClosedContact();
    }

    @Override
    default @Nullable UniformRenderObject createRenderObject(RenderObjectAdapter adapter) {
        return adapter.createContactor(this);
    }

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    enum Type implements Predicate<Contactor> {
        Common(new Vector.N2(0, 20)) {
            @Override
            public boolean test(Contactor contactor) {
                return false;
            }
        }, NormallyOpened(new Vector.N2(0, -20)) {
            @Override
            public boolean test(Contactor contactor) {
                return contactor.getNormallyOpenedContact() != null;
            }
        }, NormallyClosed(new Vector.N2(10, -20)) {
            @Override
            public boolean test(Contactor contactor) {
                return contactor.getNormallyClosedContact() != null;
            }
        }, Alternating(Vector.Zero) {
            @Override
            public boolean test(Contactor contactor) {
                return NormallyOpened.test(contactor) && NormallyClosed.test(contactor);
            }
        };

        Vector offset;

        public ITransform relative(ITransform transform) {
            return new Transform.Relative(transform, offset);
        }
    }
}
