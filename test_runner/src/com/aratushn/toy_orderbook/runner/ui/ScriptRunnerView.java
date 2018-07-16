package com.aratushn.toy_orderbook.runner.ui;

import com.aratushn.toy_orderbook.runner.ScriptParser;
import com.aratushn.toy_orderbook.runner.ScriptRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScriptRunnerView extends JPanel {
    private final JList<ScriptParser.OrderAction> actions;
    private final Model model;
    private ScriptRunner runner;
    private final JButton nextBtn;

    public ScriptRunnerView(ActionListener beforeNext, ActionListener afterNext) {
        super(new BorderLayout());

        model = new Model();
        actions = new JList<>(model);
        actions.setCellRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        ScriptParser.OrderAction a = (ScriptParser.OrderAction) value;

                        String action = a.toString();
                        
                        return super.getListCellRendererComponent(list, action, index, isSelected, cellHasFocus);
                    }
                }
        );

        nextBtn = new JButton("Next Order");
        nextBtn.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        beforeNext.actionPerformed(e);

                        runner.next();

                        refreshState();

                        afterNext.actionPerformed(e);
                    }
                }
        );
        refreshState();

        this.add(nextBtn, BorderLayout.NORTH);
        this.add(new JScrollPane(actions), BorderLayout.CENTER);
    }

    private void refreshState() {
        model.fire();
        nextBtn.setEnabled(runner != null && runner.hasNext());
    }

    public void setRunner(ScriptRunner runner) {
        this.runner = runner;
        refreshState();
    }

    private class Model extends AbstractListModel<ScriptParser.OrderAction> {
        @Override
        public int getSize() {
            return runner != null ? runner.getInputs().size() : 0;
        }

        public void fire() {
            fireContentsChanged(this, 0, Integer.MAX_VALUE);
        }

        @Override
        public ScriptParser.OrderAction getElementAt(int index) {
            return runner.getInputs().get(index);
        }
    }
}
