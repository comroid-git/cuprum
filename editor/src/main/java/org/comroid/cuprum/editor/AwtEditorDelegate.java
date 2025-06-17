package org.comroid.cuprum.editor;

import lombok.Value;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.delegate.EditorDelegate;
import org.comroid.cuprum.editor.model.SnappingPoint;
import org.comroid.cuprum.editor.render.impl.SnappingMarker;

@Value
public class AwtEditorDelegate implements EditorDelegate {
    @Override
    public SnappingPoint createSnappingPoint(EditorComponent component, Vector.N2 position) {
        return new SnappingMarker(component, position);
    }
}
