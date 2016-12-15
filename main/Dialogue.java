/*
    Contains dialogue for NPCs.
*/

package main;

public class Dialogue {
    
    public static String[] getMerchantDialogue(String state) {
        String[] s;
        
        if(state == null) {
            switch((int)(Math.random()*3)) {
                case 0:
                    s = new String[1];
                    s[0] = "Hello! Feel free to take a look at my wares!";
                    break;
                case 1:
                    s = new String[1];
                    s[0] = "Welcome to my store! Take a look around.";
                    break;
                case 2:
                    s = new String[3];
                    s[0] = "Good day! Browse through my goods!";
                    break;
                default:
                    s = new String[0];
            }
        }
        else {
            s = new String[0];
        }
        
        return s;
    }
    
    public static String[] getCitizenDialogue(String state) {
        String[] s;
        
        if(state == null) {
            switch((int)(Math.random()*4)) {
                case 0:
                    s = new String[3];
                    s[0] = "Good day, weird stranger.";
                    s[1] = "Get out of my face.";
                    s[2] = "Ph'nglui mglw'nafh Cthulhu Rl'yeh wgah'nagl fhtagn.";
                    break;
                case 1:
                    s = new String[1];
                    s[0] = "Nice weather today.";
                    break;
                case 2:
                    s = new String[3];
                    s[0] = "I used to be an adventurer like you.";
                    s[1] = "...";
                    s[2] = "Then I took an arrow in the knee.";
                    break;
                case 3:
                    s = new String[3];
                    s[0] = "Sorry, could you come back another time?";
                    s[1] = "Like, never?";
                    s[2] = "Thanks.";
                    break;
                default:
                    s = new String[0];
            }
        }
        else {
            s = new String[0];
        }
        
        return s;
    }
}
