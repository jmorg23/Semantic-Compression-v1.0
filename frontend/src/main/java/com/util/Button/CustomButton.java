package com.util.Button;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.FontFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.imageio.ImageIO;

import com.util.Sounds.SoundPlayer.SoundEffects;





/**
 * This is a homeade button class to easily create buttons
 * with different texts, images, etc.
 * 
 * In order for this class to work you need to add the mouse listener to the
 * frame
 * and also the keylistener if you want it to work with keys also
 * You need to call its draw method within graphics paintcomponents
 * 
 * IMPORTANT: If using images make sure to set bouds with only x and y and set
 * bounds after images are added
 */

public class CustomButton {

    private BufferedImage image;
    private BufferedImage image2;
    private Runnable target;
    private Color textSelColor = Color.white;
    private Color textColor = Color.GRAY;
    private int x = 0, y = 0, width = 0, height = 0;
    private String text = "";
    private Font f = new Font("Arial", Font.PLAIN, 25);
    private int index;
    private DrawMethod drawOnSelect;
    private double sx = 1, sy = 1;

    private ActivateAction actAction;
    private boolean visible = true;

    public boolean isVisible(){
        return visible;

    }
    public void setVisible(boolean b){
        visible = b;
        actAction.setVisible(b);
    }

    /**
     * Must use before Button bounds are set
     */
    public void setActivateAction(ActivateAction act) {
        actAction = act;
    }

    public void changeTarget(Runnable r) {
        target = r;
    }

    public boolean isSelected() {

        return selectedButtons.contains((Integer) index) || otherSelectedButtons.contains((Integer) index);
    }

    public void setDrawOnSelect(DrawMethod d) {
        drawOnSelect = d;
    }

    public boolean isLocked() {
        return otherSelectedButtons.contains(buttons.indexOf(this));
    }

    private static ArrayList<CustomButton> buttons = new ArrayList<>();
    private static ArrayList<Integer> selectedButtons = new ArrayList<>();
    private static ArrayList<Integer> otherSelectedButtons = new ArrayList<>();

    private static ArrayList<ButtonCircle> circles = new ArrayList<>();

    static ActivateAction getActionFromIndex(int index) {
        return buttons.get(index).getMyAction();
    }

    public static void addCircle(ButtonCircle circle) {
        circles.add(circle);
    }

    public static void removeCircle(ButtonCircle circle) {
        circles.remove(circle);
    }

    public static void clearCircle(ButtonCircle circle) {
        circles.clear();
    }

    public static void activate(ActivateAction caller) {
        int buttonIndex = 0;
        for (CustomButton i : buttons) {
            if (i.getMyAction() == caller) {
                System.out.println("button index playing sound: " + buttonIndex);
                SoundEffects.SE_SELECT.playSoundFast();
                i.getTarget().run();
                break;
            }
            buttonIndex++;
        }
    }

    public static void checkToActivate(ActivateAction caller, ActivationCondition condition) {
        int index = 0;

        for (CustomButton but : buttons) {

            if (but.getMyAction() == caller) {
                for (ButtonCircle circle : circles) {
                    if (circle.hasIndex(index)) {
                        for (int i : circle.getActions()) {
                            buttons.get(i).getMyAction().checkToActivate(condition);
                        }
                    }
                    break;
                }
                break;
            }
            index++;
        }
    }

    public static void checkToActivate(ActivateAction caller, int condition) {
        int index = 0;
        for (CustomButton but : buttons) {

            if (but.getMyAction() == caller) {
                for (ButtonCircle circle : circles) {
                    if (circle.hasIndex(index)) {
                        ArrayList<Integer> circleActions = new ArrayList<>();
                        for (int i : circle.getActions()) {
                            circleActions.add(i);
                        }
                        for (int i : circleActions) {
                            try {
                                buttons.get(i).getMyAction().checkToActivate(condition);
                            } catch (Exception e) {

                            }
                        }
                        break;
                    }
                }
                break;
            }
            index++;
        }
    }

    public static void select(ActivateAction caller) {
        int ind = 0;

        for (CustomButton i : buttons) {
            if (i.getMyAction() == caller) {
                for (ButtonCircle j : circles) {
                    if (j.hasIndex(ind)) {
                        j.setIndex(ind);

                        return;

                    }

                }
            }
            ind++;
        }
        selectedButtons.add(ind);

    }

    public static void deselect(ActivateAction caller) {
        int ind = 0;

        for (CustomButton i : buttons) {
            if (i.getMyAction() == caller) {
                for (ButtonCircle j : circles) {
                    if (j.getIndex() == ind) {
                        j.setIndex(-1);
                        return;

                    }

                }
            }
            ind++;
        }
        selectedButtons.remove((Integer) ind);

    }

    public void swapImages() {
        BufferedImage temp = image;
        image = image2;
        image2 = temp;
    }

    public Runnable getTarget() {
        return target;
    }

    public void activate() {
        target.run();
    }

    static void nextInCircle(ActivateAction caller) {
        int ind = 0;
        for (CustomButton i : buttons) {
            if (i.getMyAction() == caller) {
                for (ButtonCircle j : circles) {
                    if (j.hasIndex(ind)) {
                        j.nextIndex();
                    }
                }
                break;
            }
            ind++;
        }
    }

    static void beforeInCircle(ActivateAction caller) {
        int ind = 0;
        for (CustomButton i : buttons) {
            if (i.getMyAction() == caller) {
                for (ButtonCircle j : circles) {
                    if (j.hasIndex(ind)) {
                        j.beforeIndex();
                        break;
                    }
                }
                break;
            }
            ind++;
        }
    }

    public ActivateAction getMyAction() {
        return actAction;
    }

    public static void addSelectedButton(ActivateAction caller) {
        int ind = 0;
        for (CustomButton i : buttons) {
            if (i.getMyAction() == caller) {
                if (!selectedButtons.contains(ind)) {
                    selectedButtons.add(ind);

                    SoundEffects.SE_CLICK.playSoundFast();
                    for (ButtonCircle c : circles) {
                        if (c.hasIndex((Integer) ind)) {
                            c.setIndex(ind);
                            break;
                        }
                    }

                }
            }
            ind++;

        }
    }

    public static void remSelectedButton(ActivateAction caller) {
        int ind = 0;
        for (CustomButton i : buttons) {
            if (i.getMyAction() == caller) {
                if (selectedButtons.contains(ind)) {
                    selectedButtons.remove((Integer) ind);
                }

                break;
            }
            ind++;
        }
    }

    /**
     * Adds a font to the button
     * 
     * @param url  pathway to the font
     * @param size size of the text
     * @throws FontFormatException in case the font format is incorrect
     * @throws IOException         in case it cant find the url
     */
    public void setFont(String url, float size) throws FontFormatException, IOException {
        InputStream fontFile = getClass().getResourceAsStream(url);
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
        f = customFont.deriveFont(size);

        if (image == null)
            actAction.setBounds(getTextBounds(f, text, x, y, true));

    }

    /**
     * Adds a font to the button
     * 
     * @param url  pathway to the font
     * @param size size of the text
     * @throws FontFormatException in case the font format is incorrect
     * @throws IOException         in case it cant find the url
     */
    public void setFont(Font f) {
        this.f = f;
        if (image == null)
            actAction.setBounds(getTextBounds(f, text, x, y, true));

    }

    /**
     * Text on the button
     * 
     * @param t text
     * 
     */
    public void addText(String t) {
        text = t;
        if (image == null)
            actAction.setBounds(getTextBounds(f, text, x, y, true));

    }

    /**
     * Adds an image for the button
     * 
     * @param url url to png image
     * @throws IOException If file url does not exist
     */
    public void addImage(String url) throws IOException {
        image = ImageIO.read(getClass().getResourceAsStream(url));
    }

    public void addImage(BufferedImage i) {
        image = i;
    }

    public void addImage(BufferedImage i, double sx, double sy) {
        image = i;
        this.sx = sx;
        this.sy = sy;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
        actAction.setBounds(this.x, this.y, width, height);
    }

    public void change(int x, int y) {
        this.x += x;
        this.y += y;

        actAction.setBounds(this.x, this.y, width, height);

    }

    /**
     * Sets the text on the button to a color
     *
     * @param c color of text before selected
     */
    public void setTextColor(Color c) {
        textColor = c;
    }

    /**
     * Sets the text on the button to a color
     *
     * @param c color of text when selected
     */
    public void setTextSelColor(Color c) {
        textSelColor = c;
    }

    /**
     * Sets the location of the button, if there is an image attached it will set
     * the width and height equal to the images width and height
     * 
     * @param x x pos
     * @param y y pos
     * 
     */
    public void setBounds(int x, int y) {
        this.x = x;
        this.y = y;
        if (image != null) {
            width = (int) (image.getWidth() * sx);
            height = (int) (image.getHeight() * sy);
            actAction.setBounds(this.x, this.y, width, height);

        } else {

            actAction.setBounds(getTextBounds(f, text, x, y, true));

        }

    }

    /**
     * Gets the bounds of the text, this is used to center the text in the button
     * 
     * @param font The font of the text
     * @param text The text to be drawn
     * @param x    x pos
     * @param y    y pos
     * @return Rectangle of the text bounds
     */
    public Rectangle getTextBounds(Font font, String text, int x, int y, boolean center) {
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        Rectangle2D bounds2D = font.getStringBounds(text, frc);

        width = (int) Math.ceil(bounds2D.getWidth());
        height = (int) Math.ceil(bounds2D.getHeight());
        int ascent = (int) -bounds2D.getY();

        return center ? new Rectangle(x - (width / 2), (y - ascent), width, height)
                : new Rectangle(x, (y - ascent), width, height);
    }

    /**
     * Sets the location and size of the button
     * 
     * @param x      x pos
     * @param y      y pos
     * @param width  button width
     * @param height button height
     * 
     */
    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        actAction.setBounds(this.x, this.y, width, height);
    }

    public void addButtonPressedImage(String url) throws IOException {
        image2 = ImageIO.read(getClass().getResourceAsStream(url));
    }

    public void addButtonPressedImage(BufferedImage i) {
        image2 = i;
    }
    /// SoundPlayer selClick;

    /**
     * Constructer that takes a runnable that will be ran when the button is
     * activated
     * The activate action is set to default
     * 
     * @param r The Target Runnable
     * @param a The Action for the button to be pressed or selected
     */
    public CustomButton(Runnable r, ActivateAction a) {
        target = r;

        // selClick = SoundPlayer.playSound(Sounds.SELCLICK);
        actAction = a;
        // if (customFont == null)
        // try {
        // customFont = Font.createFont(Font.TRUETYPE_FONT,
        // getClass().getResourceAsStream("/fonts/aAbstractGroovy.ttf"));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

    }

    public Rectangle getBounds() {
        return new Rectangle(x - (width / 2), y - (height / 2), width, height);
    }

    /**
     * Constructer that takes a runnable that will be ran when the button is
     * activated
     * The activate action is set to default
     * 
     * @param r The target runnable
     */
    public CustomButton(Runnable r) {
        this(r, new ActivateAction());
    }

    public CustomButton() {
        this(null, new ActivateAction());
    }

    static void unlockAllCircles() {
        for (int i = 0; i < circles.size(); i++) {
            circles.get(i).unlockCircle();

        }
    }

    static void indexChange(int lastIndex, ButtonCircle circle) {

        // ArrayList<Integer> con = circle.getActions();
        // for (Integer i : con) {
        // System.out.print(i + " ");
        // }
        if (circle.hasIndex((Integer) lastIndex)) {
            selectedButtons.remove((Integer) lastIndex);
            buttons.get(lastIndex).getMyAction().selected = false;

        }

        for (ButtonCircle bc : circles) {
            if (!selectedButtons.contains(bc.getIndex())) {
                selectedButtons.add(bc.getIndex());
                SoundEffects.SE_CLICK.playSoundFast();

                // System.out.println(bc.getIndex());
                if (bc.getIndex() != -1)
                    buttons.get(bc.getIndex()).getMyAction().selected = true;

            }

        }
    }

    public int myIndex() {
        int ind = 0;
        for (CustomButton i : buttons) {
            if (i.equals(this)) {
                return ind;
            }
            ind++;
        }
        return -1;
    }

    public void lockSel() {
        otherSelectedButtons.add(index);
        actAction.locked = true;
    }

    public void unlockSel() {
        if (otherSelectedButtons.contains((Integer) index))
            otherSelectedButtons.remove((Integer) index);
        actAction.locked = false;

    }

    public static void unlockAll() {

        int len = otherSelectedButtons.size();
        int[] temp = new int[len];

        int n = 0;
        for (int i : otherSelectedButtons) {
            temp[n] = i;
            n++;
        }

        for (int i = 0; i < temp.length; i++) {
            buttons.get(temp[i]).unlockSel();
        }

    }

    /**
     * This method is neccaseary to draw the actual buttons on screen, it checks to
     * draw the correct image/text
     * 
     * @param g2 for drawing 2d graphics
     */
    public void draw(Graphics2D g2) {
        actAction.setVisible(visible);
        if(visible){
        Font lFont = g2.getFont();
        Color lColor = g2.getColor();

        if (f == null) {
            // f = customFont.deriveFont(25f);
           // g2.setFont(Enviornment.mainFont);

        } else {

            g2.setFont(f);
        }

        if (image == null) {
            if (selectedButtons.contains(index) || otherSelectedButtons.contains(index)) {
                if (drawOnSelect != null) {
                    drawOnSelect.draw(g2);
                }
                g2.setColor(textSelColor);

            } else {
                g2.setColor(textColor);
            }

            g2.drawString(text, x - (width / 2), y);

        } else {
            AffineTransform specs = new AffineTransform();
            specs.translate(x - image.getWidth() / 2, y - image.getHeight() / 2);
            specs.scale(sx, sy);
            if ((selectedButtons.contains(index) || otherSelectedButtons.contains(index)) && image2 != null) {
                if (drawOnSelect != null) {
                    drawOnSelect.draw(g2);
                }

                g2.drawImage(image2, specs, null);

            } else if((selectedButtons.contains(index) || otherSelectedButtons.contains(index))&&(drawOnSelect != null)){
                
                    drawOnSelect.draw(g2);
                
            }else{
                g2.drawImage(image, specs, null);
            }
            g2.drawString(text, x, y);

        }
        g2.setColor(lColor);
        g2.setFont(lFont);
    }

    }

    /**
     * Adds a button to the entire CustomButton class to be used mainly for index
     * for key selecting
     * 
     * @param b the button being added
     */
    public static void addButton(CustomButton b) {
        if (!buttons.contains(b)) {
            buttons.add(b);
            b.index = buttons.size() - 1;
            if (b.getMyAction().usesTimer()) {
                b.getMyAction().start();
            }
        }

    }

    /**
     * Removes a button from the entire CustomButton class to be used mainly for
     * index for key selecting
     * usually being done if the button is not being drawn
     * 
     * @param b the button being removed
     */
    public static void remButton(CustomButton b) {
        if (buttons.contains(b)) {
            if (selectedButtons.contains((Integer) b.index)) {
                selectedButtons.remove((Integer) b.index);
            }
            b.getMyAction().stop();
            buttons.remove(b);

        }
    }

    public static void freshStart() {
        buttons.clear();
        circles.clear();
        selectedButtons.clear();
        otherSelectedButtons.clear();
    }

}
