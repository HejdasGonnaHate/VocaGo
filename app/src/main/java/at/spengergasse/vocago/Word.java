package at.spengergasse.vocago;

import java.io.Serializable;

public class Word implements Serializable {
    private String wordNative;
    private String wordForeign;
    private int knowledge;

    public Word(){
        wordNative = "";
        wordForeign = "";
        knowledge = 0;
    }

    public Word(String wordNative, String wordForeign, int knowledge){
        setWordNative(wordNative);
        setWordForeign(wordForeign);
        setKnowledge(knowledge);
    }

    public String getWordNative(){
        return wordNative;
    }

    public String getWordForeign(){
        return wordForeign;
    }

    public int getKnowledge(){
        return knowledge;
    }

    public void setWordNative(String wordNative){
       this.wordNative = wordNative;
    }

    public void setWordForeign(String wordForeign){
        this.wordForeign = wordForeign;
    }

    public void setKnowledge(int knowledge){
        if(knowledge >= 0 && knowledge <= 3){
            this.knowledge = knowledge;
        }
    }

}
