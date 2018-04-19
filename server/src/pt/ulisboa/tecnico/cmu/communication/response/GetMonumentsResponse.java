package pt.ulisboa.tecnico.cmu.communication.response;

import java.util.List;

public class GetMonumentsResponse implements Response {

    private static final long serialVersionUID = 734457624276534179L;
    private List<String> monumentsNames;

    public GetMonumentsResponse(List<String> monumentsNames) {
        this.monumentsNames = monumentsNames;
    }

    public List<String> getMonumentsNames() {
        return this.monumentsNames;
    }

}
