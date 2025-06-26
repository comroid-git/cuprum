package org.comroid.cuprum.component.model.contact;

import org.comroid.cuprum.component.model.abstr.SnappableComponent;
import org.jetbrains.annotations.Nullable;

public interface NormallyClosedContact extends CommonContact {
    /**
     * @return normally closed contact
     */
    @Nullable SnappableComponent getNormallyClosedContact();
}
