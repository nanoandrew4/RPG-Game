package game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileAccess{

    private List<String> overworldFile = null;

    FileAccess(){

    }

    public void loadFile(String path){
        try {
            overworldFile = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No such file exists");
            // error trap
        }
    }

    public Object getFromFile(String identifier, String type){ // need to cast to get appropiate object
        for(int x = 0; x < overworldFile.size(); x++){
            if(overworldFile.get(x).contains(identifier)) {
                return parseType(overworldFile.get(x).substring(identifier.length()+1), type);
            }
        }
        return null;
    }

    private Object parseType(Object o, String type){
        if(type.equals("int"))
            return Integer.valueOf((String)o);
        else if(type.equals("boolean"))
            return Boolean.valueOf((String)o);
        else if(type.equals("double"))
            return Double.valueOf((String)o);

        return o; // return the string if none applicable
    }
}