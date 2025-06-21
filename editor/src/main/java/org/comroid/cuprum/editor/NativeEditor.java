package org.comroid.cuprum.editor;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.data.Vector;
import org.comroid.api.func.util.Stopwatch;
import org.comroid.api.func.util.Streams;
import org.comroid.api.info.Log;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.component.model.abstr.CuprumComponent;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.component.model.abstr.SimulationComponent;
import org.comroid.cuprum.component.model.abstr.WireMeshComponent;
import org.comroid.cuprum.component.model.abstr.WireMeshContainer;
import org.comroid.cuprum.editor.component.ToolBar;
import org.comroid.cuprum.editor.model.EditorMode;
import org.comroid.cuprum.editor.model.EditorUser;
import org.comroid.cuprum.editor.model.SnappingPoint;
import org.comroid.cuprum.editor.model.SnappingPointCandidate;
import org.comroid.cuprum.editor.render.NativeRenderObject;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.editor.render.impl.SnappingMarker;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.simulation.WireMesh;
import org.comroid.cuprum.spatial.Transform;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Stream;

@Value
@EqualsAndHashCode(callSuper = true)
public class NativeEditor extends JFrame implements Editor {
    public static NativeEditor INSTANCE;

    public static void main(String[] args) {
        INSTANCE = new NativeEditor();
    }

    Set<CuprumComponent>                        cuprumComponents = new HashSet<>();
    Map<EditorComponent, NativeRenderObject<?>> renderObjects    = new ConcurrentHashMap<>();
    EditorUser                                  user;
    NativeRenderObject.Adapter                  renderObjectAdapter;
    View                                        view;
    JPanel                                      canvas;
    ToolBar                                     toolbar;
    ExecutorService                             renderer         = Executors.newSingleThreadExecutor();
    @NonFinal @Nullable SnappingPoint snappingPoint;
    @NonFinal @Nullable Vector.N2     dragFromEditorPosition;

    public NativeEditor() {
        var size = new Vector.N2(800, 600);

        this.user                = new EditorUser(this);
        this.renderObjectAdapter = new NativeRenderObject.Adapter();
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

        canvas = new JPanel() {
            private final Stopwatch stopwatch = new Stopwatch(NativeEditor.this);
            private       long      frameTimeNanos;

            @Override
            public void paint(Graphics g) {
                stopwatch.start();

                super.paint(g);
                var g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.drawString(String.format("FPS: %.0f (%.2fms)",
                        1_000_000_000f / frameTimeNanos,
                        frameTimeNanos / 1_000_000f), 10, 20);
                g2.drawString(String.format("Position: %.0f %.0f",
                        user.getCursor().getPosition().getX(),
                        user.getCursor().getPosition().getY()), 10, 30);
                g2.drawString(String.format("Mode: %s", user.getMode()), 10, 40);

                drawDebugInfo(g2);
                drawGrid(g2);

                Stream.concat(getPrimaryRenderObjects(), getSecondaryRenderObjects())
                        .flatMap(Streams.cast(NativeRenderObject.class))
                        .forEach(obj -> obj.paint(NativeEditor.this, g2));

                frameTimeNanos = stopwatch.stop().toNanos();
            }

            private void drawDebugInfo(Graphics2D g2) {
                var offset    = 50;
                var component = user.getComponent();

                if (component == null) return;
                for (var infoSource : ComponentDebugInfoSource.ALL)
                    if (infoSource.check.test(component)) {
                        var value = infoSource.mapper.apply(component);
                        g2.drawString("%s: %s".formatted(infoSource.name,
                                value.getClass().isArray() || value instanceof Iterable
                                ? Arrays.toString(value instanceof Iterable<?> iter
                                                  ? Streams.of(iter).toArray()
                                                  : (Object[]) value)
                                : value), 10, offset += 10);
                    }
            }

            private void drawGrid(Graphics2D g2) {
                var halfHeight = getHeight() / 2;
                var halfWidth  = getWidth() / 2;

                // cross on 0
                g2.setColor(Color.BLACK);
                g2.drawLine(halfWidth, 0, halfWidth, getHeight());
                g2.drawLine(0, halfHeight, getWidth(), halfHeight);

                // cross on cursor
                g2.setColor(Color.DARK_GRAY);
                var cursor = view.transformEditorToCanvas(user.getCursor().getPosition());
                g2.drawLine((int) cursor.getX(), 0, (int) cursor.getX(), getHeight());
                g2.drawLine(0, (int) cursor.getY(), getWidth(), (int) cursor.getY());

                // measurement sections
                g2.setColor(Color.GRAY);
                // todo

                // grid lines
                g2.setColor(Color.LIGHT_GRAY);
                // todo
            }

            @Value
            static class ComponentDebugInfoSource<T> {
                static final Collection<ComponentDebugInfoSource<?>> ALL = List.of(new ComponentDebugInfoSource<>(
                                "Selected Object",
                                $ -> true,
                                Function.identity()),
                        new ComponentDebugInfoSource<>("Transform", $ -> true, Transform.Holder::getTransform),
                        new ComponentDebugInfoSource<>("Snapping Points",
                                $ -> true,
                                it -> it.getSnappingPoints().map(PositionSupplier::getPosition).toArray()),
                        new ComponentDebugInfoSource<>("WireMesh",
                                WireMeshContainer.class::isInstance,
                                comp -> ((WireMeshContainer) comp).getWireMesh()),
                        new ComponentDebugInfoSource<>("Wire Segments",
                                Wire.class::isInstance,
                                comp -> ((Wire) comp).getSegments().toArray()));
                String                       name;
                Predicate<EditorComponent>   check;
                Function<EditorComponent, T> mapper;
            }
        };
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
                        .map(SnappingPointCandidate::renderObject)
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

    private Stream<UniformRenderObject> getPrimaryRenderObjects() {
        return Stream.concat(Stream.ofNullable(snappingPoint), getRenderObjects().values().stream());
    }

    private Stream<UniformRenderObject> getSecondaryRenderObjects() {
        return user.getRenderObjects().stream();
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

    @Override
    public void rescanMesh(WireMeshComponent newComponent, PositionSupplier position) {
        var overlaps = getWireMeshComponents().filter(Predicate.not(newComponent::equals))
                .flatMap(wmc -> wmc.getSnappingPoints().flatMap(newComponent::findOverlap))
                .toList();
        if (overlaps.isEmpty()) return;
        WireMesh mesh = newComponent.getWireMesh();
        for (var overlap : overlaps)
            mesh = mesh.integrate(overlap);
        newComponent.setWireMesh(mesh);
    }
}
