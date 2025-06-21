package org.comroid.cuprum.editor.component;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.render.RenderComponent;

import java.util.stream.Stream;

public interface WireLine extends RenderComponent<Wire> {
    @Override
    default Stream<Vector.N2> getVertices() {
        return Stream.concat(Stream.of(getPosition()),
                getComponent().getSegments()
                        .stream()
                        .map(Wire.Segment::getPosition)
                        .map(getView()::transformEditorToCanvas));
    }
}
