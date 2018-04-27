package pt.ulisboa.tecnico.cmu.data;

import java.util.Date;

public class SessionID {

    private String sessionID;
    private Date generatedTime;

    public SessionID(String sessionID, Date generatedTime) {
        this.sessionID =  sessionID;
        this.generatedTime = generatedTime;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public Date getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(Date generatedTime) {
        this.generatedTime = generatedTime;
    }
}
