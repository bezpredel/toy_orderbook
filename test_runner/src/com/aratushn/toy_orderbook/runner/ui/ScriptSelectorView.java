package com.aratushn.toy_orderbook.runner.ui;

import com.aratushn.toy_orderbook.runner.ScriptParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.function.Consumer;

public class ScriptSelectorView extends JPanel {

    private final JComboBox<String> scripts;

    public ScriptSelectorView(Consumer<String> onSelection) {
        super(new BorderLayout());

        scripts = new JComboBox<>(
                new Vector<>(ScriptParser.KNOWN_SCRIPT_NAMES)
        );

        JButton btn = new JButton("Load");
        btn.addActionListener( a -> onSelection.accept((String) scripts.getSelectedItem()));

        add(scripts, BorderLayout.CENTER);
        add(btn, BorderLayout.EAST);
    }

    public String getSelectedScript() {
        return (String) scripts.getSelectedItem();
    }


}
