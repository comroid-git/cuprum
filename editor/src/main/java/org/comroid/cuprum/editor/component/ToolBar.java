package org.comroid.cuprum.editor.component;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Value
@EqualsAndHashCode(callSuper = true)
public class ToolBar extends MenuBar {
    FileMenu fileMenu;
    ModeMenu modeMenu;

    public ToolBar() {
        add(fileMenu = new FileMenu());
        add(modeMenu = new ModeMenu());
    }

    private static void invoke(MenuItem item, ActionEvent event, Runnable handler) {
        if (item.getActionCommand().equals(event.getActionCommand())) handler.run();
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class FileMenu extends Menu {
        MenuItem fileNew;

        FileMenu() {
            super("File");

            add(fileNew = new MenuItem("New"));
        }

        @Value
        @NonFinal
        public abstract class Listener implements ActionListener {
            {
                addActionListener(this);
            }

            @Override
            public final void actionPerformed(ActionEvent e) {
                invoke(fileNew, e, this::fileNew);
            }

            public abstract void fileNew();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class ModeMenu extends Menu {
        MenuItem                modeInteract;
        ModeMenu.InsertToolMenu insertToolMenu;
        MenuItem                modeRemove;

        ModeMenu() {
            super("Mode");

            add(modeInteract = new MenuItem("Interact"));
            add(insertToolMenu = new ModeMenu.InsertToolMenu());
            add(modeRemove = new MenuItem("Remove"));
        }

        @Value
        @NonFinal
        public abstract class Listener implements ActionListener {
            {
                addActionListener(this);
            }

            @Override
            public final void actionPerformed(ActionEvent e) {
                invoke(modeInteract, e, this::modeInteract);
                invoke(modeRemove, e, this::modeRemove);
            }

            public abstract void modeInteract();

            public abstract void modeRemove();
        }

        @Value
        @EqualsAndHashCode(callSuper = true)
        public static class InsertToolMenu extends PopupMenu {
            MenuItem toolWire;
            MenuItem toolSolder;

            InsertToolMenu() {
                super("Insert...");

                add(toolWire = new MenuItem("Wire"));
                add(toolSolder = new MenuItem("Solder"));
            }

            @Value
            @NonFinal
            public abstract class Listener implements ActionListener {
                {
                    addActionListener(this);
                }

                @Override
                public final void actionPerformed(ActionEvent e) {
                    invoke(toolWire, e, this::toolWire);
                    invoke(toolSolder, e, this::toolSolder);
                }

                public abstract void toolWire();

                public abstract void toolSolder();
            }
        }
    }
}
