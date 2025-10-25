package com.util.Button;

import java.util.ArrayList;

/**
 * This is used to put certain CustomButtons into a circle that when keys are
 * pressed the next button is selected or for custom use.
 * 
 * The index of the button will always be selected
 */
public class ButtonCircle {
    private ArrayList<Integer> actions = new ArrayList<>();

    private int index = -1;

    /**
     * Index in actions arraylist
     * 
     * @return int index
     */

    public boolean circleLocked = false;

    public ArrayList<Integer> getActions() {
        return actions;
    }

    public void lockCircle() {
        for (int i : actions) {
            CustomButton.getActionFromIndex(i).canBeSelected = false;
        }
        circleLocked = true;
    }

    public void unlockCircle() {
        for (int i : actions) {
            CustomButton.getActionFromIndex(i).canBeSelected = true;
        }
        circleLocked = false;
    }

    public int getIndex() {
        return index;
    }

    public boolean hasIndex(Integer i) {
        return actions.contains((Integer) i);
    }

    public void setIndex(Integer i) {
        int c = index;
        index = i;

        CustomButton.indexChange(c, this);

    }

    /**
     * Sets index to one higher, zero if it cant go any more
     */
    public void beforeIndex() {
        if (circleLocked)
            return;
        index++;
        if (index >= actions.size()) {
            index = 0;
            CustomButton.indexChange(actions.size() - 1, this);

        } else
            CustomButton.indexChange(index - 1, this);

    }

    /**
     * Constructor that takes each index of the custum buttons to be in the circle
     * 
     * @param acts
     */
    public ButtonCircle(int[] acts) {
        for (int i : acts) {
            actions.add(i);
        }
    }

    /**
     * Constructor that takes an index of the custum buttons to be in the circle
     * 
     * @param acts
     */
    public ButtonCircle(int acts) {

        actions.add(acts);

    }

    /**
     * Sets index to one less, arraysize-1 if it cant go any lower
     */
    public void nextIndex() {

        index--;
        if (index < 0) {
            index = actions.size() - 1;
            CustomButton.indexChange(0, this);

        } else
            CustomButton.indexChange(index + 1, this);

    }

    /**
     * Adds an action to the button circle
     * 
     * @param i index passed in
     */
    public void addIndex(int i) {
        if (!actions.contains(i))
            actions.add(i);
    }

    /**
     * Removes an action to the button circle
     * 
     * @param i index passed in
     */
    public void remIndex(int i) {
        if (actions.contains(i))
            actions.remove((Integer) i);
    }

    /**
     * Gets the next acion in the array list, loops back to index 0 at highest index
     * 
     * @return returns the activation action next in line
     */
    public int getNextAction() {
        if (index - 1 >= actions.size()) {
            index = 0;
            return actions.get(index);

        } else
            return actions.get(index + 1);
    }

    /**
     * Gets the current action
     * 
     * @return returns the action at index
     */
    public int getCurAction() {
        if (index >= actions.size())
            index = 0;

        return actions.get(index);

    }

}
