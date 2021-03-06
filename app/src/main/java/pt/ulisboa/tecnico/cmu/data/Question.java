package pt.ulisboa.tecnico.cmu.data;


import java.io.Serializable;

public class Question implements Serializable{

    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String solution;

    public Question(String question, String option1, String option2, String option3, String option4, String solution){
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.solution = solution;
    }

    public void setNullSolution(){
        this.solution = null;
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

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
