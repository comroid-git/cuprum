package org.comroid.cuprum.simulation;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.comroid.cuprum.editor.Editor;

@Value
public class SimulationCore implements Runnable {
    Editor editor;

    @Override
    public void run() {
    }
}
