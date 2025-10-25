package com.util.Button;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.Timer;


public class ActivateAction extends MouseAdapter implements KeyListener, ActionListener {

    public boolean selected, locked = false;
    public boolean canBeSelected = true;

    private ArrayList<ActivationCondition> actCondition = new ArrayList<>();
    private ArrayList<SelectiveCondition> selCondition = new ArrayList<>();
    private ArrayList<Integer> selUpKeyCodes = new ArrayList<>();
    private ArrayList<Integer> selDownKeyCodes = new ArrayList<>();

    private ArrayList<Integer> actKeyCodes = new ArrayList<>();
    private boolean actOnClick = true, selOnHover = true, onlyActivateOnSel = true;
    private Rectangle buttonBounds;

    private static AffineTransform graphicalTransform = new AffineTransform();

    private boolean usetimer = false;
    private boolean visible = true;
    private boolean selectedByHover = false;
    public boolean isVisible(){
        return visible;
    }
    public void setVisible(boolean b){
        visible = b;
    }
    public void setSelectOnHover(boolean b) {
        selOnHover = b;
    }

    public boolean usesTimer() {
        return usetimer;
    }

    public Rectangle getMybounds() {
        return buttonBounds;
    }

    public static void setGraphicalTransform(AffineTransform af) {
        graphicalTransform = af;
    }

    public void onlyActivateOnSelect(boolean b) {
        onlyActivateOnSel = b;
    }

    /**
     * This constructer has no parameters and it adds up and down arrows to the
     * selected button codes and it add the enter key for activation codes
     * 
     */
    public ActivateAction() {
        selUpKeyCodes.add(KeyEvent.VK_UP);
        selDownKeyCodes.add(KeyEvent.VK_DOWN);
        actKeyCodes.add(KeyEvent.VK_ENTER);
        actKeyCodes.add(KeyEvent.VK_SPACE);
        
        t = new Timer(1000, this);
     
    }

    public void setBounds(int x, int y, int w, int h) {
        
        buttonBounds = new Rectangle(x - (w / 2), y - (h / 2), w, h);
    }

    public void setBounds(Rectangle r) {
        
        buttonBounds = r;
    }
    public void actOnClick(boolean b) {
        actOnClick = b;
    }

    public void selOnHover(boolean b) {
        selOnHover = b;
    }

    public ActivateAction(ActivationCondition con) {
        actCondition.add(con);
        t = new Timer(33, this);
        usetimer = true;
        selOnHover = false;

    }

    private Timer t;

    public void start() {
        t.start();
    }

    public ActivateAction(SelectiveCondition selCon, ActivationCondition con) {
        actCondition.add(con);
        selCondition.add(selCon);
        t = new Timer(33, this);
        usetimer = true;
        selOnHover = false;

    }
    public ActivateAction(SelectiveCondition selCon) {
        selCondition.add(selCon);
        t = new Timer(33, this);
        usetimer = true;
       // selOnHover = false;

    }

    public void addActKeyCode(int code) {
        actKeyCodes.add(code);
    }

    public void addSelUpKeyCode(int code) {
        selUpKeyCodes.add(code);
    }

    public void addSelDownKeyCode(int code) {
        selDownKeyCodes.add(code);
    }

    /**
     * This method is called to set a keycode as a primary source of activating the
     * button, It will earase all other codes
     * 
     * @param code KeyCode that will be used to activate the button.
     *             =
     */
    public void setActKeyCode(int code) {
        actKeyCodes.clear();
        actKeyCodes.add(code);
    }

    public void stop() {
        t.stop();
    }

    /**
     * This method is called to set a keycode as a primary source of selecting the
     * button, It will earase all other codes
     * 
     * @param code KeyCode that will be used to select the button.
     */
    public void setSelUpKeyCode(int code) {
        selUpKeyCodes.clear();
        selUpKeyCodes.add(code);
    }

    public void setSelDownKeyCode(int code) {
        selDownKeyCodes.clear();
        selDownKeyCodes.add(code);
    }

    public boolean isSelected() {
        return selected;
    }


    public void checkToActivate(ActivationCondition ac){
        if(actCondition.contains(ac)&&(selected)){
            CustomButton.activate(this);
        }
    }
    public void checkToActivate(int ac){
        if(actKeyCodes.contains(ac)&&(selected)){
            CustomButton.activate(this);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        if(!visible){
            return;
        }
        if (selOnHover) {
            int ox = (int) ((e.getX() - graphicalTransform.getTranslateX()) / graphicalTransform.getScaleX());
            int oy = (int) ((e.getY() - graphicalTransform.getTranslateY()) / graphicalTransform.getScaleY());

            if (buttonBounds != null)
                if (buttonBounds.contains(ox, oy)) {

                    if(!selected&&canBeSelected){
                    CustomButton.select(this);
                    selected = true;
                    selectedByHover = true;
                    }

                } else {

                    if(canBeSelected)
                    CustomButton.deselect(this);
                    selectedByHover = false;
                    selected = false;

                }

        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!visible){
            return;
        }
        int ox = (int) ((e.getX() - graphicalTransform.getTranslateX()) / graphicalTransform.getScaleX());
        int oy = (int) ((e.getY() - graphicalTransform.getTranslateY()) / graphicalTransform.getScaleY());
        if (actOnClick)
            if (buttonBounds.contains(ox, oy)&&selected) {
                CustomButton.activate(this);
            }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!visible){
            return;
        }
        if (selUpKeyCodes.contains(e.getKeyCode())) {
            CustomButton.nextInCircle(this);

        }
        if (selDownKeyCodes.contains(e.getKeyCode())) {
            CustomButton.beforeInCircle(this);


        }
        if (actKeyCodes.contains(e.getKeyCode())) {


            CustomButton.checkToActivate(this, e.getKeyCode());
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!visible){
            return;
        }
        // 60064970 2848 545 7651

        if(!selectedByHover)
        for (SelectiveCondition s : selCondition) {
            if (s.isSelected()) {
                if(!selected&&canBeSelected){
                CustomButton.select(this);
                selected = true;
                }
            } else {
                if(selected&&canBeSelected){
                    CustomButton.deselect(this);

                }
                selected = false;
            }
        }
        for (ActivationCondition a : actCondition) {

            if (a.shouldActivate()) {
                if ((onlyActivateOnSel && selected)) {
                    CustomButton.activate(this);
                }
            }
        }

    }

}
