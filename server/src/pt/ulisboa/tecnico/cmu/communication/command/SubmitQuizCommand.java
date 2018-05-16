package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

import java.util.List;

public class SubmitQuizCommand implements Command{

    private static final long serialVersionUID = -8807331723807741905L;
    private String quizName;
    private List<String> answers;
    private String userID;
    private String sessionID;

    public SubmitQuizCommand(String quizName ,List<String> answers, String userID, String sessionID) {
        this.quizName = quizName;
        this.answers = answers;
        this.userID = userID;
        this.sessionID = sessionID;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public List<String> getAnswers() {
        return this.answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
