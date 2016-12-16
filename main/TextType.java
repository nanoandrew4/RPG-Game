/*
    Text typing animation.
*/

package main;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TextType extends Transition {
    private final Text text; //pointer to text object
    private String[] strings; //destination text
    private static int charDuration; //milliseconds per char
    private int index; //current string
    
    //return millis per character
    public static int getCharDuration() {
        return charDuration;
    }
    
    //default duration using string length
    public TextType(Text text, String s) {
        this.text = text;
        text.setText("");
        strings = new String[1];
        index = 0;
        strings[0] = s;
        charDuration = 30;
        setCycleDuration(Duration.millis(s.length()*charDuration));
        setInterpolator(Interpolator.LINEAR);
    }
    
    //default duration using string length
    public TextType(Text text, String[] s) {
        this.text = text;
        text.setText("");
        index = 0;
        strings = s;
        charDuration = 30;
        setCycleDuration(Duration.millis(s[0].length()*charDuration));
        setInterpolator(Interpolator.LINEAR);
    }
    
    //set general duration of cycle
    public void setDuration(Duration d) {
        setCycleDuration(d);
    }
    
    //get duration per character
    public void setCharacterDuration(int millis) {
        charDuration = millis;
    }
    
    //set string
    public void setString(String s) {
        strings = new String[1];
        index = 0;
        strings[0] = s;
        setCycleDuration(Duration.millis(s.length()*charDuration));
    }
    
    //set strings
    public void setStrings(String[] s) {
        strings = s;
        index = 0;
        setCycleDuration(Duration.millis(s[0].length()*charDuration));
    }
    
    //advance to next text, return completion
    public boolean next() {
        if(index < strings.length - 1) {
            index++;
            setCycleDuration(Duration.millis(strings[index].length()*charDuration));
            return true;
        }
        else {
            return false;
        }
    }
    
    //instantly finish text
    public void finish() {
        text.setText(strings[index]);
        stop();
    }
    
    //get talk index
    public int getIndex() {
        return index;
    }
    
    //interpolate
    @Override
    protected void interpolate(double k) {
        text.setText(strings[index].substring(0, 
                (int)Math.floor(k * strings[index].length())));
    }
}
