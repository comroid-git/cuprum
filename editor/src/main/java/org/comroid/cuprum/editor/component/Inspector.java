package org.comroid.cuprum.editor.component;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.cuprum.editor.NativeEditor;

import javax.swing.*;
import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true, exclude = { "editor" })
public class Inspector extends JPanel {
    NativeEditor editor;

    public Inspector(NativeEditor editor) {
        this.editor = editor;

        add(new Label("Inspector", Label.CENTER));
    }
}
