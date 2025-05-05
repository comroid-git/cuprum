package org.comroid.cuprum.editor;

import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.api.func.util.Stopwatch;
import org.comroid.cuprum.editor.render.AwtRenderObject;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

@Value
public class AwtEditor implements Editor {
    public static AwtEditor INSTANCE;

    public static void main(String[] args) {
        INSTANCE = new AwtEditor();
    }

    Set<AwtRenderObject> renderObjects = new HashSet<>();
    RenderObjectAdapter  renderObjectAdapter;
    View                 view;
    Frame                frame;
    Canvas               canvas;

    public AwtEditor() {
        var size = new Vector.N2(800, 600);

        this.renderObjectAdapter = new AwtRenderObject.Adapter();
        this.view                = new View(size);

        frame = new Frame();
        frame.setSize((int) size.getX(), (int) size.getY());
        frame.setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        int       x          = (screenSize.width - windowSize.width) / 2;
        int       y          = (screenSize.height - windowSize.height) / 2;
        frame.setLocation(x, y);

        canvas = new Canvas() {
            private final Stopwatch stopwatch = new Stopwatch(AwtEditor.this);
            private       long      frameTimeNanos;

            @Override
            public void paint(Graphics g) {
                stopwatch.start();

                super.paint(g);
                var g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawString(String.format("FPS: %.0f (%.2fms)", 1_000_000_000f / frameTimeNanos, frameTimeNanos / 1_000_000f), 10, 20);
                for (var renderObject : renderObjects)
                    renderObject.paint(AwtEditor.this, g2);

                frameTimeNanos = stopwatch.stop().toNanos();
            }
        };
        canvas.setBackground(Color.LIGHT_GRAY);

        frame.add(canvas, BorderLayout.CENTER);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                var size = frame.getSize();
                view.setSize(new Vector.N2(size.width, size.height));
                canvas.repaint();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });

        frame.setVisible(true);
    }
}
