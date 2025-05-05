package org.comroid.cuprum.component;

import org.comroid.annotations.Default;
import org.comroid.api.data.Vector;
import org.comroid.cuprum.physics.Material;
import org.jetbrains.annotations.Nullable;

public interface Wire extends Component {
    Vector.N2 getPositionA();

    Vector.N2 getPositionB();

    @Default("1")
    double getLength();

    @Default("1.5")
    double getCrossSection();

    @Nullable Material getMaterial();
}
