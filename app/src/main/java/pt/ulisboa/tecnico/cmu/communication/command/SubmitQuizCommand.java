package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

import java.util.List;

public class SubmitQuizCommand implements Command{

    private static final long serialVersionUID = -8807331723807741905L;
    private List<String> answers;
    private String quizName;
    private String userID;

    public SubmitQuizCommand(String quizName ,List<String> answers, String userID) {
        this.quizName = quizName;
        this.answers = answers;
        this.userID = userID;
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
}
