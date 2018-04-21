package pt.ulisboa.tecnico.cmu.communication.response;

import java.util.List;

public class SignUpResponse implements Response {

    private static final long serialVersionUID = 734457624276534179L;
    private String status;
    private String userID;
    private List<String> monumentsNames;


    public SignUpResponse(String status, String userID, List<String> monumentsNames) {
        this.status = status;
        this.userID = userID;
        this.monumentsNames = monumentsNames;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<String> getMonumentsNames() {
        return monumentsNames;
    }

    public void setMonumentsNames(List<String> monumentsNames) {
        this.monumentsNames = monumentsNames;
    }
}
