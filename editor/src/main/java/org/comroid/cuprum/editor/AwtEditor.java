package org.comroid.cuprum.editor;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.data.Vector;
import org.comroid.api.func.util.Stopwatch;
import org.comroid.cuprum.editor.component.ToolBar;
import org.comroid.cuprum.editor.render.AwtRenderObject;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.state.EditorMode;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = true)
public class AwtEditor extends Frame implements Editor {
    public static AwtEditor INSTANCE;

    public static void main(String[] args) {
        INSTANCE = new AwtEditor();
    }

    Set<AwtRenderObject> renderObjects = new HashSet<>();
    RenderObjectAdapter  renderObjectAdapter;
    View                 view;
    Canvas               canvas;
    ToolBar              toolbar;
    @NonFinal EditorMode mode = EditorMode.INTERACT;

    public AwtEditor() {
        var size = new Vector.N2(800, 600);

        this.renderObjectAdapter = new AwtRenderObject.Adapter();
        this.view                = new View(size);

        setSize((int) size.getX(), (int) size.getY());
        setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        int       x          = (screenSize.width - windowSize.width) / 2;
        int       y          = (screenSize.height - windowSize.height) / 2;
        setLocation(x, y);

        this.toolbar = new ToolBar();
        toolbar.getFileMenu().new Listener() {
            @Override
            public void fileNew() {
                // todo: clear everything
            }
        };
        toolbar.getModeMenu().new Listener() {
            @Override
            public void modeInteract() {
                mode = EditorMode.INTERACT;
                updateModeMenu();
            }

            @Override
            public void modeRemove() {
                mode = EditorMode.REMOVE;
                updateModeMenu();
            }
        };
        toolbar.getModeMenu().getInsertToolMenu().new Listener() {
            @Override
            public void toolWire() {
                mode = EditorMode.TOOL_WIRE;
                updateModeMenu();
            }

            @Override
            public void toolSolder() {
                mode = EditorMode.TOOL_SOLDER;
                updateModeMenu();
            }
        };
        setMenuBar(toolbar);
        updateModeMenu();

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

        add(canvas, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                var size = getSize();
                view.setSize(new Vector.N2(size.width, size.height));
                canvas.repaint();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void updateModeMenu() {
        var m = toolbar.getModeMenu();
        m.getModeInteract().setEnabled(mode != EditorMode.INTERACT);
        m.getModeRemove().setEnabled(mode != EditorMode.REMOVE);

        var itm = m.getInsertToolMenu();
        itm.getToolWire().setEnabled(mode != EditorMode.TOOL_WIRE);
        itm.getToolSolder().setEnabled(mode != EditorMode.TOOL_SOLDER);
    }
}
