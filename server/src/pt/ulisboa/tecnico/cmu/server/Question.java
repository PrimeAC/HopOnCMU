package pt.ulisboa.tecnico.cmu.server;


public class Question {

    private String monumentID;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String solution;

    public Question(String monumentID, String question, String option1, String option2, String option3, String option4, String solution){
        this.monumentID = monumentID;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.solution = solution;
    }

    public String getMonumentID(){
        return this.monumentID;
    }

    public String getQuestion(){
        return this.question;
    }

    public String getOption1(){
        return this.option1;
    }


    public String getOption2(){
        return this.option2;
    }

    public String getOption3(){
        return this.option3;
    }

    public String getOption4(){
        return this.option4;
    }


    public String getSolution(){
        return this.solution;
    }




}
