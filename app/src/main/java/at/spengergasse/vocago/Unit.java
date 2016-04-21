package at.spengergasse.vocago;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Unit implements Serializable{
    private String name;
    private ArrayList<Word> woerter;
    private Random rnd = new Random();

    public Unit(String name){
        setName(name);
        woerter = new ArrayList<Word>();
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
       this.name = name;
    }

    public boolean addWord(Word w){
        boolean wordOK = false;
        for(char c : w.getWordForeign().toCharArray()){
            if(c != ' '){
                wordOK = true;
            }
        }
        boolean wordOK2 = false;
        for(char c : w.getWordNative().toCharArray()){
            if(c != ' '){
                wordOK2 = true;
            }
        }
        if(wordOK&&wordOK2){
            woerter.add(w);
            return true;
        }
        else return false;
    }
    public void removeWord(int index){
        woerter.remove(index);
    }

    public Word getRandomWord(){
        boolean foundWord = false;
        Word word = new Word();
        while (!foundWord){
            int index = rnd.nextInt(woerter.size()); //Random Index
            word = woerter.get(index); //Wort mit dem index holen
            int randomValue = rnd.nextInt(10); //Random Wert zwischen 0 und 9 (inklusive)
            int wordknowledge = word.getKnowledge(); //Knowledge des Wortes (0,1,2 oder 3)
            switch(wordknowledge){
                case 0: foundWord = true; //100% Chance bei schlechtem Wissen
                case 1: if(randomValue < 8)foundWord = true; //80% Chance
                case 2: if(randomValue < 5)foundWord = true; //50% Chance
                case 3: if(randomValue < 2)foundWord = true; //20% Chance
            }
        }
        return word;
    }

    public ArrayList<Word> getWordArray(){
        return woerter;
    }
}
