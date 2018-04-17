package pt.ulisboa.tecnico.cmu.response;

import pt.ulisboa.tecnico.cmu.data.Quiz;

public class GetQuizResponse implements Response {

    private static final long serialVersionUID = 734457624276534179L;
    private Quiz quiz;

    public GetQuizResponse(Quiz quiz) {
        this.quiz = quiz;
    }

    public Quiz getQuiz() {
        return this.quiz;
    }

}
