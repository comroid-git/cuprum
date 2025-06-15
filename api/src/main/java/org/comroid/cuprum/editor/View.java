package org.comroid.cuprum.editor;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.api.data.Vector;

@Value@Setter
public class View {
    public static final int       BORDER_PADDING = 25;
    @NonFinal           Vector.N2 position       = Vector.N2.Zero;
    @NonFinal           Vector.N2 size;

    public View(Vector.N2 size) {
        this.size = size;
    }

    public boolean outOfView(Vector position) {
        position = position.subi(this.position);
        var compare = size.addi(BORDER_PADDING);
        return !(position.getX() > -BORDER_PADDING && position.getY() > -BORDER_PADDING && position.getX() <= compare.getX() && position.getY() <= compare.getY());
    }

    public Vector.N2 transformEditorToCanvas(Vector position) {
        return position.subi(tl()).as2();
    }

    public Vector.N2 transformCanvasToEditor(Vector position) {
        return position.addi(tl()).as2();
    }

    private Vector tl() {
        return position.subi(size.divi(2));
    }
}
