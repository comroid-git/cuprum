package org.comroid.cuprum.simulation.component;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.comroid.api.data.Vector;
import org.comroid.api.func.util.Streams;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.component.Contactor;
import org.comroid.cuprum.component.ContactorCoil;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.simulation.model.SimulationComponentBase;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ContactorImpl extends SimulationComponentBase implements Contactor {
    public static final Vector REL_WIRES = new Vector.N2(0, 5);

    public static Contactor createNormallyOpened(ITransform transform) {
        return new ContactorImpl(transform, create(Type.NormallyOpened, transform), null);
    }

    public static Contactor createNormallyClosed(ITransform transform) {
        return new ContactorImpl(transform, null, create(Type.NormallyClosed, transform));
    }

    public static Contactor createAlternating(ITransform transform) {
        return new ContactorImpl(transform,
                create(Type.NormallyOpened, transform),
                create(Type.NormallyClosed, transform));
    }

    ConnectionPoint commonContact;
    @Nullable ConnectionPoint normallyOpenedContact, normallyClosedContact;
    @Nullable ContactorCoil parent;
    boolean manuallyOperated;

    private ContactorImpl(
            ITransform transform, @Nullable ConnectionPoint normallyOpenedContact,
            @Nullable ConnectionPoint normallyClosedContact
    ) {
        super(transform);

        this.commonContact = create(Type.Common, transform);
        this.normallyOpenedContact = normallyOpenedContact;
        this.normallyClosedContact = normallyClosedContact;
    }

    @Override
    public Stream<? extends PositionSupplier> getSnappingPoints() {
        return Stream.of(commonContact, normallyClosedContact, normallyOpenedContact)
                .flatMap(Streams.cast(PositionSupplier.class));
    }

    @Override
    public void toggleOperation() {
        manuallyOperated = !manuallyOperated;
    }

    private static ConnectionPoint create(Type contactorType, ITransform transform) {
        return new SolderImpl(contactorType.relative(transform));
    }
}
