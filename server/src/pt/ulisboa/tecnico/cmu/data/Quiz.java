package pt.ulisboa.tecnico.cmu.data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz implements Serializable{

    private String monumentID;
    private String monumentName;
    private List<Question> questions;
    private Map<String, Integer> userScore;  //keeps a map of userID:score for the quiz
    private Map<String, Date> userTime;  //keeps a map of userID:time spent for the quiz

    public Quiz(String monumentID, String monumentName, List<Question> questions){
        this.monumentID = monumentID;
        this.monumentName = monumentName;
        this.questions = questions;
        this.userScore = new HashMap<>();
        this.userTime = new HashMap<>();
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

    public Map<String, Integer> getUserScore() {
        return userScore;
    }

    public void setUserScore(Map<String, Integer> userScore) {
        this.userScore = userScore;
    }

    public Map<String, Date> getUserTime() {
        return userTime;
    }

    public void setUserTime(Map<String, Date> userTime) {
        this.userTime = userTime;
    }
}
