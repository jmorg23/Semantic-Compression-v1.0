package com.ui;

import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

public class Response  {
    
    public String message;
    public JLabel label;
    
    private int y;
    private int x = 550;

    private int index = 0;

    public Response(String message, int ypos, boolean inc){
        this.message = message;
        this.y = ypos;
        // message = con;
        // this.message = con;
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(parser.parse(message));
        label = new JLabel();
        label.setVerticalAlignment(SwingConstants.TOP);
        // label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        label.setForeground(java.awt.Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 30));

        this.message = "<html>" + html + "</html>";
        label.setText(message);
        label.setBounds(x, y, 1000, 2000);
        
        int height = getRenderedLabelHeight(label, 1000);
        Panel.nexty+=height+80;
        if(!inc){

        label.setText("");
        }else{
            index = message.length();
        }




    }
    public static int getRenderedLabelHeight(JLabel label, int maxWidth) {
        String text = label.getText();
        Font font = label.getFont();

        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();

        View view = kit.getViewFactory().create(
                doc.getDefaultRootElement()
        );

        try {
            kit.read(new java.io.StringReader(text), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        view = kit.getViewFactory().create(doc.getDefaultRootElement());
        view.setSize(maxWidth, 0);
        float preferredHeight = view.getPreferredSpan(View.Y_AXIS);

        return (int) Math.ceil(preferredHeight);
    }

    public void draw(Graphics2D g2){
        label.setBounds(label.getX(), y+Panel.changey, label.getWidth(), label.getHeight());
        if(index < message.length()){
            label.setText(message.substring(0,index++));

        }

        // g2.setColor(new Color(50, 50, 50, 200));
        // g2.fillRoundRect(x, y, 600, 100, 25, 25);
        // g2.setColor(Color.WHITE);
        // g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // g2.drawString(message, x + 20, y + 30);


    }
}
