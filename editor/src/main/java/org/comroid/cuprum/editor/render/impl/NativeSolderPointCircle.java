package org.comroid.cuprum.editor.render.impl;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.editor.NativeEditor;
import org.comroid.cuprum.editor.component.SolderPointCircle;
import org.comroid.cuprum.editor.render.NativeRenderObject;

import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class NativeSolderPointCircle extends NativeRenderObject<ConnectionPoint> implements SolderPointCircle {
    public static final int DIAMETER = 8;

    public NativeSolderPointCircle(ConnectionPoint component) {
        super(component);
    }

    @Override
    public void paint(NativeEditor e, Graphics2D g) {
        if (outOfView()) return;

        var pos    = getPosition();
        var radius = DIAMETER / 2;
        g.setColor(Color.BLACK);
        g.fillOval((int) (pos.getX() - radius), (int) (pos.getY() - radius), DIAMETER, DIAMETER);
    }
}
