package com.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import com.networking.Client;

public class UserInputField extends JTextArea {


    private Panel panel;

    public UserInputField(Panel pan, Client client) {
        super();
        this.panel = pan;
        setBounds(650, 930, 1000, 80);
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        setForeground(Color.GRAY);


        setCaretColor(Color.WHITE);
        setEditable(false);
        setWrapStyleWord(true);
        setLineWrap(true);
        setFont(new Font("Segoe UI", Font.PLAIN, 35));

        setText("Type your message here...");
        setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0)));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                setText("");
                setEditable(true);

                setForeground(Color.WHITE);

            }
        });
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = getText().trim();
                    panel.sendMessage(message, client);
                    setText("");
                    evt.consume();
                    setEditable(false);
                    setForeground(Color.GRAY);
                    setText("Type your message here...");

                }
            }
        });

    }

   
}