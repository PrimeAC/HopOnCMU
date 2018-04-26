package pt.ulisboa.tecnico.cmu.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz implements Serializable{

    private String monumentID;
    private String monumentName;
    private List<Question> questions;
    private Map<User, Integer> userAnswers;

    public Quiz(String monumentID, String monumentName, List<Question> questions){
        this.monumentID = monumentID;
        this.monumentName = monumentName;
        this.questions = questions;
        this.userAnswers = new HashMap<>();
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

    public Map<User, Integer> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(Map<User, Integer> userAnswers) {
        this.userAnswers = userAnswers;
    }

}
