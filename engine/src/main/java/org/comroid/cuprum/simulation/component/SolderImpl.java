package org.comroid.cuprum.simulation.component;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.cuprum.component.ConnectionPoint;
import org.comroid.cuprum.simulation.WireMesh;

@Value
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SolderImpl extends EditorComponentBase implements ConnectionPoint {
    @Setter @NonFinal WireMesh wireMesh = new WireMesh();
}
