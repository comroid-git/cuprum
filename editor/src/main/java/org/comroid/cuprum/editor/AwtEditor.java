package org.comroid.cuprum.editor;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.data.Vector;
import org.comroid.api.func.util.Stopwatch;
import org.comroid.api.func.util.Streams;
import org.comroid.cuprum.component.model.SimComponent;
import org.comroid.cuprum.editor.component.ToolBar;
import org.comroid.cuprum.editor.model.EditorMode;
import org.comroid.cuprum.editor.model.EditorUser;
import org.comroid.cuprum.editor.render.AwtRenderObject;
import org.comroid.cuprum.editor.render.RenderObjectAdapter;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.editor.render.impl.SnappingMarker;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Value
@EqualsAndHashCode(callSuper = true)
public class AwtEditor extends Frame implements Editor {
    public static AwtEditor INSTANCE;

    public static void main(String[] args) {
        INSTANCE = new AwtEditor();
    }

    Set<SimComponent>                  simComponents = new HashSet<>();
    Map<SimComponent, AwtRenderObject> renderObjects = new ConcurrentHashMap<>();
    EditorUser                         user;
    RenderObjectAdapter                renderObjectAdapter;
    View                               view;
    Canvas                             canvas;
    ToolBar                            toolbar;
    Executor                           renderer      = Executors.newSingleThreadExecutor();
    @NonFinal @Nullable SnappingMarker snappingPoint;

    public AwtEditor() {
        var size = new Vector.N2(800, 600);

        this.user                = new EditorUser(this);
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
                user.setMode(EditorMode.INTERACT);
                refreshEditorModeVisual();
            }

            @Override
            public void modeRemove() {
                user.setMode(EditorMode.REMOVE);
                refreshEditorModeVisual();
            }
        };
        toolbar.getModeMenu().getInsertToolMenu()
                .new Listener() {
            @Override
            public void toolWire() {
                user.setMode(EditorMode.TOOL_WIRE);
                refreshEditorModeVisual();
            }

            @Override
            public void toolSolder() {
                user.setMode(EditorMode.TOOL_SOLDER);
                refreshEditorModeVisual();
            }
        };
        setMenuBar(toolbar);

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
                g2.drawString(String.format("Position: %.0f %.0f", user.getCursor().getPosition().getX(), user.getCursor().getPosition().getY()), 10, 30);
                g2.drawString(String.format("Object: %s", user.getComponent()), 10, 40);

                Stream.concat(getPrimaryRenderObjects(), getSecondaryRenderObjects())
                        .flatMap(Streams.cast(AwtRenderObject.class))
                        .forEach(obj -> obj.paint(AwtEditor.this, g2));

                frameTimeNanos = stopwatch.stop().toNanos();
            }
        };
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                var pos = view.transformCanvasToEditor(new Vector.N2(e.getX(), e.getY()));
                user.getCursor().setPosition(pos);

                var hadCandidate = snappingPoint != null;
                var hasCandidate = new boolean[]{ false };
                snappingPoint = simComponents.stream()
                        .flatMap(comp -> comp.getSnappingPoints().map(snap -> new SnappingPointCandidate(comp, snap)))
                        .filter(candidate -> Vector.dist(candidate.position, pos) < 10)
                        .min(Comparator.comparingDouble(candidate -> Vector.dist(candidate.position, pos)))
                        .map(candidate -> {
                            hasCandidate[0] = true;
                            return candidate.renderObject();
                        })
                        .orElse(null);
                if (hadCandidate != hasCandidate[0] || hasCandidate[0]) refreshVisual();
            }

            private record SnappingPointCandidate(SimComponent component, Vector.N2 position) {
                private SnappingMarker renderObject() {
                    return new SnappingMarker(component, position);
                }
            }
        });
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var pos = snappingPoint == null ? new Vector.N2(e.getX(), e.getY()) : snappingPoint.getPosition();
                pos = view.transformCanvasToEditor(pos);

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1: // left click
                        user.triggerClickPrimary(pos, e.isShiftDown());
                        break;
                    case MouseEvent.BUTTON2: // mouse wheel
                        break;
                    case MouseEvent.BUTTON3: // right click
                        user.triggerClickSecondary(pos, e.isShiftDown());
                        break;
                }

                refreshEditorModeVisual();
                refreshVisual();
            }
        });
        canvas.setBackground(Color.LIGHT_GRAY);

        add(canvas, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                var size = getSize();
                view.setSize(new Vector.N2(size.width, size.height));
                refreshVisual();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
        refreshEditorModeVisual();
    }

    public Map<SimComponent, AwtRenderObject> getRenderObjects() {
        return Collections.unmodifiableMap(renderObjects);
    }

    @Override
    public Stream<? extends SimComponent> getSimComponents() {
        return simComponents.stream();
    }

    public void add(SimComponent component) {
        final var adapter = getRenderObjectAdapter();
        if (simComponents.add(component)) {
            var renderObject = (AwtRenderObject) component.createRenderObject(adapter);
            if (renderObject != null) renderObjects.put(component, renderObject);
        }
    }

    @Override
    public boolean remove(SimComponent component) {
        return simComponents.remove(component) & renderObjects.remove(component) != null;
    }

    @Override
    public void refreshVisual() {
        renderer.execute(canvas::repaint);
    }

    private Stream<UniformRenderObject> getPrimaryRenderObjects() {
        return Stream.concat(Stream.ofNullable(snappingPoint), renderObjects.values().stream());
    }

    private Stream<UniformRenderObject> getSecondaryRenderObjects() {
        return user.getRenderObjects().stream();
    }

    private void refreshEditorModeVisual() {
        user.refreshVisual();

        // apply user mode to toolbar
        var mode = user.getMode();
        var menu = toolbar.getModeMenu();
        menu.getModeInteract().setEnabled(mode != EditorMode.INTERACT);
        menu.getModeRemove().setEnabled(mode != EditorMode.REMOVE);

        var itm = menu.getInsertToolMenu();
        itm.getToolWire().setEnabled(mode != EditorMode.TOOL_WIRE);
        itm.getToolSolder().setEnabled(mode != EditorMode.TOOL_SOLDER);
    }
}
