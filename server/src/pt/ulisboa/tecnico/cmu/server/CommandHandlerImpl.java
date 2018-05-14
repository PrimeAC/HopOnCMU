package pt.ulisboa.tecnico.cmu.server;

import pt.ulisboa.tecnico.cmu.communication.command.*;
import pt.ulisboa.tecnico.cmu.data.Quiz;
import pt.ulisboa.tecnico.cmu.data.User;
import pt.ulisboa.tecnico.cmu.data.Question;
import pt.ulisboa.tecnico.cmu.communication.response.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CommandHandlerImpl implements CommandHandler {

    private Logger LOGGER = Logger.getLogger(CommandHandlerImpl.class.getName());

	@Override
	public Response handle(HelloCommand hc) {
        LOGGER.info("Received: " + hc.getMessage());
		return new HelloResponse("Hi from Server!");
	}

	@Override
	public Response handle(TicketCommand tc) {
		LOGGER.info("Bilhete recebido " + tc.getTicketCode());
		if (Server.validTicket(tc.getTicketCode())) {
			for (User user : Server.getUsers()) {
				if (user.getTicketCode().equals(tc.getTicketCode())) {
					//ticket code already used so can login
					return new TicketResponse("OK", user.getUserID(), getMonuments(user));
				}
			}
			//ticket never used (NU), so need to create an account
			return new TicketResponse("NU", null, null);
		}
		return new TicketResponse("NOK", null, null);
	}

	@Override
	public Response handle(SignUpCommand suc) {
		LOGGER.info("Bilhete recebido " + suc.getTicketCode() + " user: " + suc.getUserID());
		for (User user : Server.getUsers()) {
			if (user.getUserID().equals(suc.getUserID())) {
				//ticket userID already used
				return new SignUpResponse("NOK", null, null);
			}
		}
		User user = new User(suc.getUserID(), suc.getTicketCode(), 0);
		Server.getUsers().add(user);
		return new SignUpResponse("OK", suc.getUserID(), getMonuments(user));
	}

	@Override
	public Response handle(GetQuizCommand gqc) {
        LOGGER.info("Quiz " + gqc.getMonumentName());
		for (Quiz quiz : Server.getQuizzes()) {
			if (quiz.getMonumentName().equals(gqc.getMonumentName())) {
				return new GetQuizResponse(quiz);
			}
		}
		return new GetQuizResponse(null);
	}

	@Override
	public Response handle(GetRankingCommand grc) {
		Map<String, Integer> unsortRanking = new HashMap<>();
		for (User user : Server.getUsers()) {
			unsortRanking.put(user.getUserID(), user.getScore());
		}
		return new GetRankingResponse(Server.sortByScore(unsortRanking));
	}

	@Override
	public Response handle(SubmitQuizCommand sqc) {
        LOGGER.info("Recebi as respostas ao quiz " + sqc.getAnswers().get(0) + sqc.getAnswers().get(1) + sqc.getAnswers().get(2) );
		List<Question> questions = Server.getQuiz(sqc.getQuizName());
		int cnt = 0;
		int score = 0;
		if(questions != null){
			for (Question question: questions) {
				if(sqc.getAnswers().get(cnt).equals(question.getSolution())){
					//need to increase the user score by one
					score++;
				}
				cnt++;
			}
            LOGGER.info("Score " + score);
			Server.updateUserScore(sqc.getUserID(), score, sqc.getQuizName());
			return new SubmitQuizResponse("OK");
		}
		return new SubmitQuizResponse("NOK");
	}

	private List<String> getMonuments(User user) {
		List<String> monumentsNames = new ArrayList<>();
		for (Quiz quiz : Server.getQuizzes()) {
		    if(quiz.getUserAnswers().containsKey(user)){
                monumentsNames.add(quiz.getMonumentName() + "|T");
            }
            else {
                monumentsNames.add(quiz.getMonumentName() + "|F");
            }
		}
		return monumentsNames;
	}
}
