package com.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JTextArea;

public class ChatBubble extends JTextArea {

    private RoundRectangle2D bounds;
    private int x, y, width, height;
    private final int maxWidth = 300;

    public ChatBubble(String text, int starty) {
        y = starty;
        x = 1425;
        bounds = new RoundRectangle2D.Double(x, y, maxWidth, 50, 25, 25);
        setBounds(x, y, 10, 10);
        setFont(new Font("Segoe UI", Font.PLAIN, 35));

        setOpaque(false); // removes default background
        setBackground(new Color(0, 0, 0, 0)); // fully transparent
        setForeground(Color.WHITE); // text color still visible
        setBorder(null); // optional: remove border
        setCaretColor(Color.WHITE); // so you can see the caret
        setEditable(false);
        setWrapStyleWord(true);
        setLineWrap(true);
        setText(text);

        resizeToFit();
        Panel.nexty += height + 80;
        System.out.println(getBounds());

    }

    private void resizeToFit() {
        FontMetrics fm = getFontMetrics(getFont());
        String text = getText().isEmpty() ? " " : getText();

        int textWidth = fm.stringWidth(text);

        width = Math.min(textWidth + 10, maxWidth);

        setBounds(x, y, width, Short.MAX_VALUE);
        height = getPreferredSize().height;

        setBounds(x, y, width, height);

        setPreferredSize(new Dimension(width, height));

        revalidate();
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics2D g2) {

        bounds = new RoundRectangle2D.Double(x - 10, y + Panel.changey, width + 10, height, 25, 25);
        setBounds(getX(), y + Panel.changey, getWidth(), getHeight());
        g2.fill(bounds);

        g2.draw(bounds);

    }

}
