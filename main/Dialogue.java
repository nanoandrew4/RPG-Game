/*
    Contains dialogue for NPCs.
    How the constructor works:
    -If a string starting with a '#' is encountered, all strings following it 
        until the next '#' will be part of a set stored in a HashMap under the
        numbers following the original '#'.
    -If a '$' is encountered during the construction of a set, it is prompting
        a response, usually either yes or no.
    -A '%' signifies action for the model, with the characters following it
        representing what action to take.
        -"%trade": start trading
*/

package main;

import java.util.ArrayList;
import java.util.HashMap;

public class Dialogue {
    //merchant dialogue
    public static Dialogue getMerchantDialogue() {
        switch((int)(Math.random()*4)) {
            case 0:
                return new Dialogue(
                        "#0", "Hello! Feel free to take a look at my wares!", 
                        "$", 
                        "#00", "Alright! Go ahead.", 
                        "%trade", "Thanks for the business.",
                        "#01", "Fine then, you cheapskate."
                );
            case 1:
                return new Dialogue(
                        "#0", "Welcome to my store! Take a look around.",
                        "$",
                        "#00", "Do you really have any money on you...?", 
                        "%trade", "Thanks for the business.",
                        "#01", "Then get out of here."
                );
            case 2:
                return new Dialogue(
                        "#0", "Good day! Browse through my goods!",
                        "$",
                        "%trade", "Thanks for the business.", "Come again next time.",
                        "#01", "Well, that's okay too I guess."
                );
            case 3:
                return new Dialogue(
                        "#0", "It's quiz time, you loser!", "Is 3 times 3 equal to 9?",
                        "$",
                        "#00", "Correct!", "Is 2 plus 3 equal to 6?",
                        "$",
                        "#01", "Wrong!", "You suck at math.",
                        "#000", "Wrong.", "It's 5.",
                        "#001", "Correct! On to the next question!", "Is 0 a natural number?",
                        "$",
                        "#0010", "No. It's not.",
                        "#0011", "Correct. The final question:", "How would you rate this quiz out of 10?",
                        "$",
                        "#00110", "Congratulations! You can look at my store now!",
                        "%trade", "Thanks!",
                        "#00111", "Wrong. You failed the quiz."
                );
            default:
                return null;
        }
    }
    
    //citizen dialogue
    public static Dialogue getCitizenDialogue() {
        switch((int)(Math.random()*6)) {
            case 0:
                return new Dialogue(
                        "#0", "Good day, weird stranger.", "Get out of my face.",
                        "Ph'nglui mglw'nafh Cthulhu Rl'yeh wgah'nagl fhtagn."
                );
            case 1:
                return new Dialogue(
                        "#0", "Nice weather today."
                );
            case 2:
                return new Dialogue(
                        "#0", "I used to be an adventurer like you.",
                        "..................................",
                        "Then I took an arrow to the knee."
                );
            case 3:
                return new Dialogue(
                        "#0", "Sorry, could you come back another time?",
                        "Like, never?",
                        "Thanks."
                );
            case 4:
                return new Dialogue(
                        "#0", "The most merciful thing in the world, I think, "
                    + "is the inability of the human mind to correlate "
                    + "all its contents. We live on a placid island of "
                    + "ignorance in the midst of black seas of infinity, "
                    + "and it was not meant that we should voyage far.",
                        "The sciences, each straining in its own direction, have "
                    + "hitherto harmed us little; but some day the piecing "
                    + "together of dissociated knowledge will open up such "
                    + "terrifying vistas of reality, and of our frightful "
                    + "position therein, that we shall either go mad from "
                    + "the revelation or flee from the deadly light into "
                    + "the peace and safety of a new dark age."
                );
            case 5:
                return new Dialogue(
                        "#0", "Hey, could you spare some change?",
                        "$",
                        "#00", "Thanks, bud.", 
                        "%c-10",
                        "#01", "Maybe next time, then."
                );
            default:
                return null;
        }
    }
    
    //standard vars
    HashMap<String, String[]> lines;
    String state;
    
    //constructor
    private Dialogue(String... s) {
        lines = new HashMap();
        state = "0";
        
        //compile lines
        for(int i = 0; i < s.length; i++) {
            if(s[i].charAt(0) == '#') {
                ArrayList<String> stringList = new ArrayList();
                for(int j = i+1; j < s.length && s[j].charAt(0) != '#'; j++) {
                    stringList.add(s[j]);
                }
                lines.put(s[i].substring(1), stringList.toArray(new String[0]));
            }
        }
    }
    
    //get lines of current state
    public String[] getText() {
        return lines.getOrDefault(state, condense("I AM ERROR"));
    }
    
    //advance state
    public boolean nextState(int i) {
        state += String.valueOf(i).charAt(0);
        return lines.get(state) == null;
    }
    
    public boolean nextState(char s) {
        state += s;
        return lines.get(state) == null;
    }
    
    private String[] condense(String... s) {
        return s;
    }
}
