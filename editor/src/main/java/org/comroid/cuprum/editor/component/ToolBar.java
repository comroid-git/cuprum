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
    EditMenu editMenu;
    ModeMenu modeMenu;

    public ToolBar() {
        add(fileMenu = new FileMenu());
        add(editMenu = new EditMenu());
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
    public static class EditMenu extends Menu {
        MenuItem rescan;

        EditMenu() {
            super("Edit");

            add(rescan = new MenuItem("Rescan"));
        }

        @Value
        @NonFinal
        public abstract class Listener implements ActionListener {
            {
                addActionListener(this);
            }

            @Override
            public final void actionPerformed(ActionEvent e) {
                invoke(rescan, e, this::editRescan);
            }

            public abstract void editRescan();
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
            MenuItem toolContactNO;
            MenuItem toolContactNC;

            InsertToolMenu() {
                super("Insert...");

                add(toolWire = new MenuItem("Wire"));
                add(toolSolder = new MenuItem("Solder"));
                add(toolContactNO = new MenuItem("NO Contact"));
                add(toolContactNC = new MenuItem("NC Contact"));
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
                    invoke(toolContactNC, e, this::toolContactNC);
                    invoke(toolContactNO, e, this::toolContactNO);
                }

                public abstract void toolWire();

                public abstract void toolSolder();

                public abstract void toolContactNC();

                public abstract void toolContactNO();
            }
        }
    }
}
