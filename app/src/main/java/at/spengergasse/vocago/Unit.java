package at.spengergasse.vocago;

import java.io.Serializable;

public class Unit implements Serializable{
    private String name;
    //TODO hashmap? andere Liste?

    public Unit(String name){
        setName(name);

    }

    public String getName(){
        return name;
    }

    public boolean setName(String name){
        if(name.length()>17){
            return false;
        }
        else{
            this.name = name;
            return true;
        }
    }
}
