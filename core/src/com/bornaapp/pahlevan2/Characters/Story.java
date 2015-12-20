package com.bornaapp.pahlevan2.Characters;

import com.badlogic.gdx.utils.Array;
import com.bornaapp.games.borna2d.game.Engine;

public class Story {
    Character owner;
    public boolean repeatable = false;

    public Array<Array<String>> sections = new Array<Array<String>>();

    //<-------load from file

    public void AddDialog(Array<String> dialog) {
        if (dialog != null)
            sections.add(dialog);
        else
            Engine.log.error("dialog == null");
    }

    private int sectionProgress = 0;
    private int dialogProgress = 0;

    float cancelingTime = 3;
    float elapsedTime = 0;
    public boolean active = false;

//    Control ctrl = new Control();

    public Story(Character _owner) {
        owner = _owner;
//        ctrl.setHeight_inMeters(3);
//        ctrl.setWidth_inMeters(5);

//        ((Level)Engine.getCurrentLevel()).addUI(ctrl);
    }

    protected void updateStory(float delta) {
        if (elapsedTime > cancelingTime) {
            elapsedTime = 0;
            active = false;
        } else {
            elapsedTime += delta;
        }

//        if (active) {
//            ctrl.isVisible = true;
//            ctrl.setPosition_inMeters(owner.getPosition_inMeters());
//            ctrl.text = getSentence();
//        } else {
//            ctrl.isVisible = false;
//            dialogProgress = 0;
//        }
    }

    public String getSentence() {
        String sentence = "";
        if (active) {
            if (sectionProgress < sections.size) {
                Array<String> dialog = sections.get(sectionProgress);
                if (dialogProgress < dialog.size)
                    sentence = dialog.get(dialogProgress);
                else {
                    if (repeatable)
                        dialogProgress = 0;
                }

            } else {
                if (repeatable)
                    sectionProgress = 0;
            }
        }
        return sentence;
    }

    public void proceedDialog() {
        dialogProgress++;
        elapsedTime = 0;
    }

    public void goToNextSection() {
        sectionProgress++;
        dialogProgress = 0;
    }
}
