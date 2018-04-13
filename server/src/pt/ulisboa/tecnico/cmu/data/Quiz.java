package pt.ulisboa.tecnico.cmu.data;

public class Quiz {

    private String monumentID;
    private Question q1;
    private Question q2;
    private Question q3;

    public Quiz(String monumentID, Question q1, Question q2, Question q3){
        this.monumentID = monumentID;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;

    }

    public String getMonumentID() {
        return monumentID;
    }

    public void setMonumentID(String monumentID) {
        this.monumentID = monumentID;
    }

    public Question getQ1() {
        return q1;
    }

    public void setQ1(Question q1) {
        this.q1 = q1;
    }

    public Question getQ2() {
        return q2;
    }

    public void setQ2(Question q2) {
        this.q2 = q2;
    }

    public Question getQ3() {
        return q3;
    }

    public void setQ3(Question q3) {
        this.q3 = q3;
    }

}
