package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

public class GetQuizCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;
    private String monumentName;
    private String sessionID;
    private String userID;

    public GetQuizCommand(String monumentName, String sessionID, String userID) {
        this.monumentName = monumentName;
        this.sessionID = sessionID;
        this.userID = userID;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getMonumentName() {
        return this.monumentName;
    }

    public void setMonumentName(String monumentName) {
        this.monumentName = monumentName;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
