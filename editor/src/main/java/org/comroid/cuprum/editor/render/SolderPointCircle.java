package org.comroid.cuprum.editor.render;

import lombok.Value;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.editor.AwtEditor;

import java.awt.*;

@Value
public class SolderPointCircle implements AwtRenderObject {
    ConnectionPoint connectionPoint;

    @Override
    public void paint(AwtEditor e, Graphics2D g) {
        // Get canvas size
        int width  = e.getCanvas().getWidth();
        int height = e.getCanvas().getHeight();

        // Start and end points for the line
        int x2 = width - 50;
        int y2 = height / 2;

        // Draw 8pt wide circle at the end
        int circleDiameter = 8;
        int radius         = circleDiameter / 2;
        g.fillOval(x2 - radius, y2 - radius, circleDiameter, circleDiameter);
    }
}
