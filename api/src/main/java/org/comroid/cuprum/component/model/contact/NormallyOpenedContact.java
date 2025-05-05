package org.comroid.cuprum.component.model.contact;

import org.comroid.cuprum.component.ConnectionPoint;
import org.jetbrains.annotations.Nullable;

public interface NormallyOpenedContact extends CommonContact {
    /**
     * @return normally opened contact
     */
    @Nullable ConnectionPoint getNormallyOpened();
}
