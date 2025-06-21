package org.comroid.cuprum.editor.component;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.annotations.Instance;
import org.comroid.api.Polyfill;
import org.comroid.api.data.Vector;
import org.comroid.api.func.ext.Wrap;
import org.comroid.cuprum.component.model.abstr.CuprumComponent;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.component.model.abstr.WireMeshPart;
import org.comroid.cuprum.component.model.basic.Conductive;
import org.comroid.cuprum.component.model.basic.Resistive;
import org.comroid.cuprum.editor.NativeEditor;
import org.comroid.cuprum.editor.model.CPanel;
import org.comroid.cuprum.model.ITransform;
import org.comroid.cuprum.physics.Material;
import org.comroid.cuprum.simulation.WireMeshNode;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.comroid.cuprum.engine.util.BasicMathInterpreter.*;

@Value
@EqualsAndHashCode(callSuper = true, exclude = { "editor" })
public class Inspector extends CPanel {
    NativeEditor             editor;
    List<? extends Panel<?>> panels = new ArrayList<>(List.of(new TransformPanel(),
            new WireMeshNodePanel(),
            new ConductivePanel(),
            new ResistivePanel()));

    public Inspector(NativeEditor editor) {
        this.editor = editor;
    }

    public void update() {
        var component = editor.getInspectComponent();
        setVisible(component != null);
        if (component == null) {
            removeAll();
            return;
        }

        add(new JLabel(component.getClass().getSimpleName(), SwingConstants.CENTER));
        for (var panel : panels) panel.setFrom(component);
    }

    public interface InputComponent {
        void update();

        void apply();
    }

    @Value
    @NonFinal
    @EqualsAndHashCode(callSuper = true)
    public static abstract class Row extends CPanel implements InputComponent {
        public Row() {
            setVisible(false);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class DecimalRow extends Row {
        InputListener  inputListener;
        DoubleSupplier getter;
        DoubleConsumer setter;
        JTextField     value;

        public DecimalRow(String name, DoubleSupplier getter, DoubleConsumer setter) {
            this.inputListener = new InputListener(this);
            this.getter        = getter;
            this.setter        = setter;

            setToolTipText(name);

            add(new JLabel(name, SwingConstants.LEFT));
            add(value = new JTextField() {{
                setToolTipText("Value");
                setInputVerifier(DecimalInputVerifier.INSTANCE);
                addInputMethodListener(inputListener);
            }});
        }

        @Override
        public void update() {
            if (!isVisible()) return;
            value.setText(String.valueOf(getter.getAsDouble()));
        }

        @Override
        public void apply() {
            setter.accept(eval(value.getText()));
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class TransformVectorRow extends Row {
        InputListener    inputListener;
        Supplier<Vector> getter;
        Consumer<Vector> setter;
        JTextField       inputX, inputY;

        public TransformVectorRow(String name, Supplier<Vector> getter, Consumer<Vector> setter) {
            this.inputListener = new InputListener(this);
            this.getter        = getter;
            this.setter        = setter;

            setToolTipText(name);

            add(new JLabel(name, SwingConstants.LEFT));
            add(inputX = new JTextField() {{
                setToolTipText("X");
                setInputVerifier(DecimalInputVerifier.INSTANCE);
                addInputMethodListener(inputListener);
            }});
            add(inputY = new JTextField() {{
                setToolTipText("Y");
                setInputVerifier(DecimalInputVerifier.INSTANCE);
                addInputMethodListener(inputListener);
            }});

            update();
        }

        public void update() {
            if (!isVisible()) return;
            var value = getter.get();
            inputX.setText(String.valueOf(value.getX()));
            inputY.setText(String.valueOf(value.getY()));
        }

        public void apply() {
            setter.accept(vector());
        }

        private Vector vector() {
            return new Vector.N2(eval(inputX.getText()), eval(inputY.getText()));
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class DropdownRow<T> extends Row {
        InputListener inputListener;
        Supplier<T>   getter;
        Consumer<T>   setter;
        JComboBox<T>  value;

        public DropdownRow(String name, T[] values, Supplier<T> getter, Consumer<T> setter) {
            this.inputListener = new InputListener();
            this.getter        = getter;
            this.setter        = setter;

            setToolTipText(name);

            add(new JLabel(name, SwingConstants.LEFT));
            add(value = new JComboBox<>(values) {{
                setToolTipText("Value");
                addItemListener(inputListener);
            }});

            update();
        }

        @Override
        public void update() {
            if (!isVisible()) return;
            value.setSelectedItem(getter.get());
        }

        @Override
        public void apply() {
            setter.accept(Polyfill.uncheckedCast(value.getSelectedItem()));
        }

        private class InputListener implements ItemListener {
            @Override
            public void itemStateChanged(ItemEvent e) {
                apply();
            }
        }
    }

    public static abstract class Panel<Target extends CuprumComponent> extends CPanel
            implements Predicate<EditorComponent>, Function<EditorComponent, Target>, Wrap<Target> {
        @Nullable Target target;

        final void setTarget(@Nullable Target target) {
            this.target = target;
            setVisible(target != null);
        }

        @Override
        public final void setVisible(boolean aFlag) {
            if (aFlag) update();
            super.setVisible(aFlag);
        }

        final void setFrom(EditorComponent component) {
            if (component != null && test(component)) setTarget(apply(component));
            else clear();
        }

        @Override
        public final @Nullable Target get() {
            return target;
        }

        final void clear() {
            target = null;
            setVisible(false);
        }

        abstract void update();
    }

    private static class TransformPanel extends Panel<ITransform> {
        TransformVectorRow positionRow, scaleRow;

        @SuppressWarnings("DataFlowIssue")
        public TransformPanel() {
            add(new JLabel("Transform", SwingConstants.CENTER));
            add(this.positionRow = new TransformVectorRow("Position",
                    () -> target.getPosition(),
                    pos -> target.setPosition(pos)));
            add(this.scaleRow = new TransformVectorRow("Scale",
                    () -> target.getScale(),
                    scale -> target.setScale(scale)));
        }

        @Override
        public ITransform apply(EditorComponent component) {
            return component.getTransform();
        }

        @Override
        public boolean test(EditorComponent component) {
            return true;
        }

        @Override
        void update() {
            positionRow.update();
            scaleRow.update();
        }
    }

    private static class ConductivePanel extends Panel<Conductive> {
        DecimalRow length, crossSection;
        DropdownRow<Material> material;

        @SuppressWarnings("DataFlowIssue")
        public ConductivePanel() {
            add(new JLabel("Conductive", SwingConstants.CENTER));
            add(length = new DecimalRow("Length", () -> target.getLength(), length -> target.setLength(length)));
            add(crossSection = new DecimalRow("Cross Section",
                    () -> target.getCrossSection(),
                    crossSection -> target.setCrossSection(crossSection)));
            add(material = new DropdownRow<>("Material",
                    Material.values(),
                    () -> target.getMaterial(),
                    material -> target.setMaterial(material)));
        }

        @Override
        public Conductive apply(EditorComponent component) {
            return (Conductive) component;
        }

        @Override
        public boolean test(EditorComponent component) {
            return component instanceof Conductive;
        }

        @Override
        void update() {
            length.update();
            crossSection.update();
            material.update();
        }
    }

    private static class ResistivePanel extends Panel<Resistive> {
        DecimalRow resistance;

        @SuppressWarnings("DataFlowIssue")
        public ResistivePanel() {
            add(new JLabel("Resistance", SwingConstants.CENTER));
            add(resistance = new DecimalRow("Resistance",
                    () -> target.getResistance(),
                    resistance -> target.setResistance(resistance)));
        }

        @Override
        public Resistive apply(EditorComponent component) {
            return (Resistive) component;
        }

        @Override
        public boolean test(EditorComponent component) {
            return component instanceof Resistive;
        }

        @Override
        void update() {
            resistance.update();
        }
    }

    private static class WireMeshNodePanel extends Panel<WireMeshNode> {
        public WireMeshNodePanel() {
            add(new JLabel("Wire Mesh Node", SwingConstants.CENTER));
        }

        @Override
        public WireMeshNode apply(EditorComponent component) {
            return ((WireMeshPart) component).getWireMeshNode();
        }

        @Override
        public boolean test(EditorComponent component) {
            return component instanceof WireMeshPart;
        }

        @Override
        void update() {
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    private static class DecimalInputVerifier extends InputVerifier {
        static final @Instance DecimalInputVerifier INSTANCE = new DecimalInputVerifier();

        @Override
        public boolean verify(JComponent input) {
            return input instanceof JTextField txt && txt.getText().matches("-?\\d+([.,]\\d+)?");
        }
    }

    @Value
    private static class InputListener implements InputMethodListener {
        InputComponent inputComponent;

        @Override
        public void inputMethodTextChanged(InputMethodEvent event) {
            inputComponent.apply();
        }

        @Override
        public void caretPositionChanged(InputMethodEvent event) {
        }
    }
}
