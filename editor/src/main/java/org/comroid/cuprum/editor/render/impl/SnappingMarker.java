package org.comroid.cuprum.editor.render.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.editor.AwtEditor;
import org.comroid.cuprum.editor.model.SnappingPoint;
import org.comroid.cuprum.editor.render.AwtRenderObject;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.spatial.Transform;

import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class SnappingMarker extends AwtRenderObject implements SnappingPoint {
    public static final int DIAMETER = 11;
    SimComponent component;
    ITransform   transform;

    public SnappingMarker(SimComponent target, Vector.N2 position) {
        this.component = target;
        this.transform = new Transform.CanvasToViewAdapter(new Transform(position), getView());
    }

    @Override
    public void paint(AwtEditor e, Graphics2D g) {
        if (outOfView()) return;

        var pos    = getPosition().subi(Vector.One);
        var radius = DIAMETER / 2;
        g.setColor(Color.RED);
        g.drawOval((int) (pos.getX() - radius), (int) (pos.getY() - radius), DIAMETER, DIAMETER);
    }
}
