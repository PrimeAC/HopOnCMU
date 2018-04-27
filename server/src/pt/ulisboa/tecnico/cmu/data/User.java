package pt.ulisboa.tecnico.cmu.data;

public class User {

    private String userID;
    private String ticketCode;
    private int score;

    public User(String userID, String ticketCode, int score){
        this.userID = userID;
        this.ticketCode = ticketCode;
        this.score = score;

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
