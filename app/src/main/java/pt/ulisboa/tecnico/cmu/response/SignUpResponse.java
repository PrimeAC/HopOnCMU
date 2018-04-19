package pt.ulisboa.tecnico.cmu.response;

import java.util.List;

public class SignUpResponse implements Response {

    private static final long serialVersionUID = 734457624276534179L;
    private List<String> message;

    public SignUpResponse(List<String> message) {
        this.message = message;
    }

    public List<String> getMessage() {
        return this.message;
    }

}
