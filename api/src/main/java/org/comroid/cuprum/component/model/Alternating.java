package org.comroid.cuprum.component.model;

import org.comroid.cuprum.component.ConnectionPoint;
import org.jetbrains.annotations.Nullable;

public interface Alternating {
    /**
     * @return common contact of this alternator
     */
    ConnectionPoint getCommon();

    /**
     * @return normally closed contact of this alternator
     */
    @Nullable ConnectionPoint getNormallyClosed();

    /**
     * @return normally opened contact of this alternator
     */
    @Nullable ConnectionPoint getNormallyOpened();
}
