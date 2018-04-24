package pt.ulisboa.tecnico.cmu.communication.response;

public class SubmitQuizResponse implements Response {

    private static final long serialVersionUID = 734457624276534179L;
    private String status;

    public SubmitQuizResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
        }

}
