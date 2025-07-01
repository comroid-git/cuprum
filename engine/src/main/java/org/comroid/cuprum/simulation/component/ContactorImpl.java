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
import org.comroid.cuprum.spatial.Transform;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactorImpl extends SimulationComponentBase implements Contactor {
    public static final Vector OFFSET_COM = new Vector.N2(0, 20);
    public static final Vector OFFSET_NO  = new Vector.N2(0, -20);
    public static final Vector OFFSET_NC  = new Vector.N2(10, -20);
    public static final Vector REL_WIRES  = new Vector.N2(0, 5);

    public static Contactor createNormallyOpened(ITransform transform) {
        return new ContactorImpl(transform, createNO(transform), null);
    }

    public static Contactor createNormallyClosed(ITransform transform) {
        return new ContactorImpl(transform, null, createNC(transform));
    }

    public static Contactor createAlternating(ITransform transform) {
        return new ContactorImpl(transform, createNO(transform), createNC(transform));
    }

    ConnectionPoint commonContact;
    @Nullable ConnectionPoint normallyOpenedContact;
    @Nullable ConnectionPoint normallyClosedContact;
    @Nullable ContactorCoil   parent;
    boolean manuallyOperated;

    private ContactorImpl(
            ITransform transform, @Nullable ConnectionPoint normallyOpenedContact,
            @Nullable ConnectionPoint normallyClosedContact
    ) {
        super(transform);

        this.commonContact         = createCOM(transform);
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

    private static ConnectionPoint createCOM(ITransform transform) {
        return new SolderImpl(new Transform.Relative(transform, OFFSET_COM));
    }

    private static ConnectionPoint createNO(ITransform transform) {
        return new SolderImpl(new Transform.Relative(transform, OFFSET_NO));
    }

    private static ConnectionPoint createNC(ITransform transform) {
        return new SolderImpl(new Transform.Relative(transform, OFFSET_NC));
    }
}
