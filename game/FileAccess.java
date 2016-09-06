package game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.sql.*;

public class FileAccess{

    private List<String> overworldFile = null;
    private Connection c = null;
    private Statement s = null;

    FileAccess(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public void loadDatabase(String name) {

        String URL = "jdbc:sqlite:data/" + name + ".db";

        try {
            c = DriverManager.getConnection(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getFromDatabase(String query){
        try {
            s = c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()){

            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void closeDatabase(){
        try {
            c.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}