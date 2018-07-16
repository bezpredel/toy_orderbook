package com.aratushn.toy_orderbook.runner;

import com.aratushn.toy_orderbook.runner.ui.UI;

import javax.swing.*;

public class UIMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new UI().setVisible(true)
        );
    }
}
