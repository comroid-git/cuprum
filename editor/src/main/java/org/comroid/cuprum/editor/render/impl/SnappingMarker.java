package org.comroid.cuprum.editor.render.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.editor.NativeEditor;
import org.comroid.cuprum.editor.model.SnappingPoint;
import org.comroid.cuprum.editor.render.NativeRenderObject;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.spatial.Transform;

import java.awt.*;
import java.util.stream.Stream;

@Value
@EqualsAndHashCode(callSuper = true)
public class SnappingMarker extends NativeRenderObject<EditorComponent> implements SnappingPoint {
    public static final int DIAMETER = 11;
    ITransform transform;

    public SnappingMarker(EditorComponent target, Vector position) {
        super(target);
        this.transform = new Transform.EditorToCanvasAdapter(new Transform(position), getView());
    }

    @Override
    public Stream<Vector> getVertices() {
        return Stream.of(getPosition());
    }

    @Override
    public void paint(NativeEditor e, Graphics2D g) {
        if (outOfView()) return;

        var pos    = getPosition().subi(Vector.N2.One);
        var radius = DIAMETER / 2;
        g.setColor(Color.RED);
        g.drawOval((int) (pos.getX() - radius), (int) (pos.getY() - radius), DIAMETER, DIAMETER);
    }
}
