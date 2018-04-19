package pt.ulisboa.tecnico.cmu.communication.response;

import java.util.Map;

public class GetRankingResponse implements Response {

    private static final long serialVersionUID = 734457624276534179L;
    private Map<String, Integer> ranking;

    public GetRankingResponse(Map<String, Integer> ranking) {
        this.ranking = ranking;
    }

    public Map<String, Integer> getRanking() {
        return this.ranking;
        }

}
