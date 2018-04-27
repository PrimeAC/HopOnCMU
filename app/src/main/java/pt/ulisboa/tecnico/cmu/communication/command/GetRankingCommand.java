package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

public class GetRankingCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;
    private String sessionID;
    private String userID;

    public GetRankingCommand(String sessionID , String userID) {
        this.sessionID = sessionID;
        this.userID = userID;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
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
