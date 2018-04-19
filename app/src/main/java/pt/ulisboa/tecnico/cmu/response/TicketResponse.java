package pt.ulisboa.tecnico.cmu.response;

import java.util.List;

public class TicketResponse implements Response {

    private static final long serialVersionUID = 734457624276534179L;
    private List<String> message;

    public TicketResponse(List<String> message) {
        this.message = message;
    }

    public List<String> getMessage() {
        return this.message;
    }

}
