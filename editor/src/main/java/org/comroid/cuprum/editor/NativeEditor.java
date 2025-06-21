package org.comroid.cuprum.editor;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.data.Vector;
import org.comroid.api.info.Log;
import org.comroid.cuprum.component.model.abstr.CuprumComponent;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.editor.component.Canvas;
import org.comroid.cuprum.editor.component.Inspector;
import org.comroid.cuprum.editor.component.ToolBar;
import org.comroid.cuprum.editor.model.EditorMode;
import org.comroid.cuprum.editor.model.EditorUser;
import org.comroid.cuprum.editor.model.SnappingPoint;
import org.comroid.cuprum.editor.model.SnappingPointCandidate;
import org.comroid.cuprum.editor.render.NativeRenderObject;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.editor.render.impl.SnappingMarker;
import org.comroid.cuprum.model.PositionSupplier;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Stream;

@Value
@EqualsAndHashCode(callSuper = true)
public class NativeEditor extends JFrame implements Editor {
    public static final double       WINDOW_FACTOR = 0.8;
    public static       NativeEditor INSTANCE;

    public static void main(String[] args) {
        INSTANCE = new NativeEditor();
    }

    // core components
    Set<CuprumComponent>                        cuprumComponents = new HashSet<>();
    Map<EditorComponent, NativeRenderObject<?>> renderObjects    = new ConcurrentHashMap<>();
    ExecutorService                             renderer         = Executors.newSingleThreadExecutor();
    // backend components
    EditorUser                 user;
    NativeRenderObject.Adapter renderObjectAdapter;
    View                       view;
    // frontend components
    ToolBar                    toolbar;
    Canvas                     canvas;
    Inspector                  inspector;
    @NonFinal @Nullable EditorComponent inspectComponent;
    @NonFinal @Nullable SnappingPoint   snappingPoint;
    @NonFinal @Nullable Vector.N2       dragFromEditorPosition;

    public NativeEditor() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        var       size       = new Vector.N2(screenSize.width * WINDOW_FACTOR, screenSize.height * WINDOW_FACTOR);

        this.user                = new EditorUser(this);
        this.renderObjectAdapter = new NativeRenderObject.Adapter();
        this.view                = new View(size);

        setSize((int) size.getX(), (int) size.getY());
        setLayout(new BorderLayout());

        Dimension windowSize = getSize();
        int       x          = (screenSize.width - windowSize.width) / 2;
        int       y          = (screenSize.height - windowSize.height) / 2;
        setLocation(x, y);

        this.toolbar = new ToolBar();
        toolbar.getFileMenu().new Listener() {
            @Override
            public void fileNew() {
                clear();
            }
        };
        toolbar.getModeMenu().new Listener() {
            @Override
            public void modeInteract() {
                user.setMode(EditorMode.INTERACT);
            }

            @Override
            public void modeRemove() {
                user.setMode(EditorMode.REMOVE);
            }
        };
        toolbar.getModeMenu().getInsertToolMenu()
                .new Listener() {
            @Override
            public void toolWire() {
                user.setMode(EditorMode.TOOL_WIRE);
            }

            @Override
            public void toolSolder() {
                user.setMode(EditorMode.TOOL_SOLDER);
            }
        };
        setMenuBar(toolbar);

        canvas = new Canvas(this);
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                var pos = view.transformCanvasToEditor(new Vector.N2(e.getX(), e.getY()));
                user.getCursor().setPosition(pos);

                // mouse dragging
                if (dragFromEditorPosition != null) view.setPosition(pos.subi(dragFromEditorPosition).as2());

                // refresh snapping point
                snappingPoint = getEditorComponents().flatMap(comp -> comp.getSnappingPoints()
                                .map(PositionSupplier::getPosition)
                                .map(snap -> new SnappingPointCandidate(comp, snap)))
                        .filter(candidate -> Vector.dist(candidate.position(),
                                pos) < (double) SnappingMarker.DIAMETER / 2)
                        .min(Comparator.<SnappingPointCandidate>comparingInt(snap -> snap.component().priorityLayer())
                                .reversed()
                                .thenComparingDouble(candidate -> Vector.dist(candidate.position(), pos)))
                        .map(candidate -> {
                            inspectComponent = candidate.component();
                            return candidate.renderObject();
                        })
                        .orElse(null);
            }
        });
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var pos = PositionSupplier.of(view.transformCanvasToEditor(snappingPoint == null
                                                                           ? new Vector.N2(e.getX(), e.getY())
                                                                           : snappingPoint.getPosition()));

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1: // left click
                        user.triggerClickPrimary(pos, e.isShiftDown());
                        break;
                    case MouseEvent.BUTTON3: // right click
                        user.triggerClickSecondary(pos, e.isShiftDown());
                        break;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON2) return;
                var pos = snappingPoint == null ? new Vector.N2(e.getX(), e.getY()) : snappingPoint.getPosition();
                dragFromEditorPosition = view.transformCanvasToEditor(pos);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON2) return;
                dragFromEditorPosition = null;
            }
        });
        canvas.setBackground(Color.LIGHT_GRAY);
        add(canvas, BorderLayout.CENTER);

        inspector = new Inspector(this);
        add(inspector, BorderLayout.EAST);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                    case KeyEvent.VK_W:
                        user.setMode(EditorMode.TOOL_WIRE);
                        break;
                    case KeyEvent.VK_S:
                        user.setMode(EditorMode.TOOL_SOLDER);
                        break;
                }
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                var size = getSize();
                view.setSize(new Vector.N2(size.width, size.height));
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
        renderer.execute(this::renderer);
    }

    public Map<EditorComponent, NativeRenderObject<?>> getRenderObjects() {
        synchronized (renderObjects) {
            return Map.copyOf(renderObjects);
        }
    }

    public Stream<UniformRenderObject> getPrimaryRenderObjects() {
        return Stream.concat(Stream.ofNullable(snappingPoint), getRenderObjects().values().stream());
    }

    public Stream<UniformRenderObject> getSecondaryRenderObjects() {
        return user.getRenderObjects().stream();
    }

    public void clear() {
        user.clear();
        cuprumComponents.clear();

        inspectComponent       = null;
        snappingPoint          = null;
        dragFromEditorPosition = null;

        synchronized (renderObjects) {
            renderObjects.clear();
        }
    }

    private void renderer() {
        while (isEnabled()) {
            try {
                canvas.repaint();
                user.refreshVisual();

                // apply user mode to toolbar
                var mode = user.getMode();
                var menu = toolbar.getModeMenu();
                menu.getModeInteract().setEnabled(mode != EditorMode.INTERACT);
                menu.getModeRemove().setEnabled(mode != EditorMode.REMOVE);

                var itm = menu.getInsertToolMenu();
                itm.getToolWire().setEnabled(mode != EditorMode.TOOL_WIRE);
                itm.getToolSolder().setEnabled(mode != EditorMode.TOOL_SOLDER);
            } catch (Throwable t) {
                Log.at(Level.SEVERE, "Error in render thread", t);
            }
        }
    }

    public void add(EditorComponent component) {
        final var adapter = getRenderObjectAdapter();
        if (cuprumComponents.add(component)) {
            var renderObject = (NativeRenderObject<?>) component.createRenderObject(adapter);
            if (renderObject != null) synchronized (renderObjects) {
                renderObjects.put(component, renderObject);
            }
        }
    }

    @Override
    public boolean remove(SimulationComponent component) {
        // todo: remove component from meshes
        synchronized (renderObjects) {
            return component.removeFromAncestors() & cuprumComponents.remove(component) & renderObjects.remove(component) != null;
        }
    }
}
