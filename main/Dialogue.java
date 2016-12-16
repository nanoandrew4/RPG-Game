/*
    Contains dialogue for NPCs.
*/

package main;

public class Dialogue {
    
    //merchant dialogue
    public static String[] getMerchantDialogue(String state) {
        //start of conversation
        if(state == null) {
            switch((int)(Math.random()*3)) {
                case 0:
                    return make("Hello! Feel free to take a look at my wares!");
                case 1:
                    return make("Welcome to my store! Take a look around.");
                case 2:
                    return make("Good day! Browse through my goods!");
                default:
                    return null;
            }
        }
        else {
            return null;
        }
    }
    
    public static String[] getCitizenDialogue(String state) {
        if(state == null) {
            switch((int)(Math.random()*5)) {
                case 0:
                    return make("Good day, weird stranger.",
                                "Get out of my face.",
                                "Ph'nglui mglw'nafh Cthulhu Rl'yeh wgah'nagl fhtagn.");
                case 1:
                    return make("Nice weather today.");
                case 2:
                    return make("I used to be an adventurer like you.",
                                "..................................",
                                "Then I took an arrow in the knee.");
                case 3:
                    return make("Sorry, could you come back another time?",
                                "Like, never?",
                                "Thanks.");
                case 4:
                    return make("The most merciful thing in the world, I think, "
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
                        + "the peace and safety of a new dark age.");
                default:
                    return null;
            }
        }
        else {
            return null;
        }
    }
    
    //condense strings into array
    private static String[] make(String... strings) {
        return strings;
    }
}
