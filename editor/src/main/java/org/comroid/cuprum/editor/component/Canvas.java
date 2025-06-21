package org.comroid.cuprum.editor.component;

import lombok.Value;
import org.comroid.api.func.util.Stopwatch;
import org.comroid.api.func.util.Streams;
import org.comroid.cuprum.component.Wire;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.component.model.abstr.WireMeshContainer;
import org.comroid.cuprum.editor.NativeEditor;
import org.comroid.cuprum.editor.render.NativeRenderObject;
import org.comroid.cuprum.model.PositionSupplier;
import org.comroid.cuprum.spatial.Transform;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Canvas extends JPanel {
    private final NativeEditor nativeEditor;
    private final Stopwatch    stopwatch;
    private       long         frameTimeNanos;

    public Canvas(NativeEditor nativeEditor) {
        this.nativeEditor = nativeEditor;
        stopwatch         = new Stopwatch(this.nativeEditor);
    }

    @Override
    public void paint(Graphics g) {
        stopwatch.start();

        super.paint(g);
        var g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawString(String.format("FPS: %.0f (%.2fms)", 1_000_000_000f / frameTimeNanos, frameTimeNanos / 1_000_000f),
                10,
                20);
        g2.drawString(String.format("Position: %.0f %.0f",
                nativeEditor.getUser().getCursor().getPosition().getX(),
                nativeEditor.getUser().getCursor().getPosition().getY()), 10, 30);
        g2.drawString(String.format("Mode: %s", nativeEditor.getUser().getMode()), 10, 40);

        drawDebugInfo(g2);
        drawGrid(g2);

        Stream.concat(nativeEditor.getPrimaryRenderObjects(), nativeEditor.getSecondaryRenderObjects())
                .flatMap(Streams.cast(NativeRenderObject.class))
                .forEach(obj -> obj.paint(nativeEditor, g2));

        frameTimeNanos = stopwatch.stop().toNanos();
    }

    private void drawDebugInfo(Graphics2D g2) {
        var offset = 50;
        var component = Optional.ofNullable(nativeEditor.getUser().getComponent())
                .orElseGet(nativeEditor::getInspectComponent);

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
        var cursor = nativeEditor.getView().transformEditorToCanvas(nativeEditor.getUser().getCursor().getPosition());
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
}
