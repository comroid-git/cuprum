package org.comroid.cuprum.editor.component;

import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.editor.render.RenderComponent;

import java.util.stream.Stream;

public interface WireLine extends RenderComponent<Wire> {
    @Override
    default Stream<Vector> getVertices() {
        var view     = getView();
        var position = getPosition();
        return Stream.concat(Stream.of(position),
                getComponent().getSegments()
                        .stream()
                        .map(Wire.Segment::getPosition)
                        .map(view::transformEditorToCanvas));
    }
}
