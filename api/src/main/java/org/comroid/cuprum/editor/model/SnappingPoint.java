package org.comroid.cuprum.editor.model;

import org.comroid.cuprum.component.model.abstr.EditorComponent;
import org.comroid.cuprum.editor.render.UniformRenderObject;
import org.comroid.cuprum.spatial.Transform;

public interface SnappingPoint extends UniformRenderObject, EditorComponent.Holder, Transform.Holder {}
