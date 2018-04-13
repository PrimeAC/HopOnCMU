package pt.ulisboa.tecnico.cmu.data;

import java.util.List;

public class User {

    private String userID;
    private int score;
    private String ticketCode;

    public User(String ticketCode){
        this.ticketCode = ticketCode;
    }

    public User(String userID, int score, String ticketCode){
        this.userID = userID;
        this.score = score;
        this.ticketCode = ticketCode;

    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
