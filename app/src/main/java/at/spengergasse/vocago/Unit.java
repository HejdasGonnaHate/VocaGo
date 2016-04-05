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

    public void setName(String name){
       this.name = name;
    }
}
