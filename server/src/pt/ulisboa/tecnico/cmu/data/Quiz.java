package pt.ulisboa.tecnico.cmu.data;

import java.util.List;

public class Quiz {

    private String monumentID;
    private List<Question> questions;

    public Quiz(String monumentID, List<Question> questions){
        this.monumentID = monumentID;
        this.questions = questions;

    }

    public String getMonumentID() {
        return monumentID;
    }

    public void setMonumentID(String monumentID) {
        this.monumentID = monumentID;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
