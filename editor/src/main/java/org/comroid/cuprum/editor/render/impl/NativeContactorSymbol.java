package org.comroid.cuprum.editor.render.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.cuprum.component.Contactor;
import org.comroid.cuprum.editor.NativeEditor;
import org.comroid.cuprum.editor.render.NativeRenderObject;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.editor.render.util.GraphicsUtils;
import org.comroid.cuprum.simulation.component.ContactorImpl;

import java.awt.*;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@EqualsAndHashCode(callSuper = true)
public class NativeContactorSymbol extends NativeRenderObject<Contactor> {
    Set<UniformRenderObject> children;

    public NativeContactorSymbol(Contactor contactor) {
        super(contactor);

        this.children = Stream.of(Stream.of(NativeWireLine.simple(component, ContactorImpl.REL_WIRES.negate())),
                        Stream.ofNullable(component.getNormallyOpenedContact())
                                .map(no -> NativeWireLine.simple(no, ContactorImpl.REL_WIRES)),
                        Stream.ofNullable(component.getNormallyClosedContact())
                                .map(nc -> NativeWireLine.simple(nc, ContactorImpl.REL_WIRES)))
                .flatMap(Function.identity())
                .map(new NativeRenderObject.Adapter()::createWireLine)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void paint(NativeEditor e, Graphics2D g) {
        GraphicsUtils.prepareConductiveGraphics(component, g);

        super.paint(e, g);

        // paint contactor line
        var start = component.getCommonContact().getPosition().subi(ContactorImpl.REL_WIRES.negate());
        var end   = component.getActiveOutputContact().getPosition().subi(ContactorImpl.REL_WIRES);
        g.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
    }
}
