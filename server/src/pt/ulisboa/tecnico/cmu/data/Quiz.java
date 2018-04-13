package pt.ulisboa.tecnico.cmu.data;

import java.util.List;

public class Quiz {

    private String monumentID;
    private String monumentName;
    private List<Question> questions;

    public Quiz(String monumentID, String monumentName, List<Question> questions){
        this.monumentID = monumentID;
        this.monumentName = monumentName;

        this.questions = questions;

    }

    public String getMonumentID() {
        return monumentID;
    }

    public void setMonumentID(String monumentID) {
        this.monumentID = monumentID;
    }

    public String getMonumentName() {
        return monumentName;
    }

    public void setMonumentName(String monumentName) {
        this.monumentName = monumentName;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
