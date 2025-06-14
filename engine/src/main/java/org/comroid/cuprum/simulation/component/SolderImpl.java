package org.comroid.cuprum.simulation.component;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.comroid.cuprum.component.ConnectionPoint;

@Value
@EqualsAndHashCode(callSuper = true)
public class SolderImpl extends SimComponentBase implements ConnectionPoint {
}
