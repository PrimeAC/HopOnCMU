package pt.ulisboa.tecnico.cmu.server;

import pt.ulisboa.tecnico.cmu.command.*;
import pt.ulisboa.tecnico.cmu.data.Quiz;
import pt.ulisboa.tecnico.cmu.data.User;
import pt.ulisboa.tecnico.cmu.response.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandlerImpl implements CommandHandler {

	@Override
	public Response handle(HelloCommand hc) {
		System.out.println("Received: " + hc.getMessage());
		return new HelloResponse("Hi from Server!");
	}

	@Override
	public Response handle(TicketCommand tc) {
		System.out.println("Este é o bilhete recebido " + tc.getTicketCode());
		if (Server.validTicket(tc.getTicketCode())) {
			for (User user : Server.getUsers()) {
				if (user.getTicketCode().equals(tc.getTicketCode())) {
					//ticket code already used so can login
					return new TicketResponse("OK");
				}
			}
			//ticket never used (NU), so need to create an account
			return new TicketResponse("NU");
		}
		return new TicketResponse("NOK");
	}

	@Override
	public Response handle(SignUpCommand suc) {
		for (User user : Server.getUsers()) {
			if (user.getUserID().equals(suc.getUserID())) {
				//ticket userID already used
				return new SignUpResponse("NOK");
			}
		}
		User user = new User(suc.getUserID(), suc.getTicketCode(), 0);
		Server.getUsers().add(user);
		return new TicketResponse("OK");
	}

	@Override
	public Response handle(GetQuizCommand gqc) {
		for (Quiz quiz : Server.getQuizzes()) {
			if (quiz.getMonumentName().equals(gqc.getMonumentName())) {
				return new GetQuizResponse(quiz);
			}
		}
		return new GetQuizResponse(null);
	}

	@Override
	public Response handle(GetMonumentsCommand gmc) {
		List<String> monumentsNames = new ArrayList<>();
		for (Quiz quiz : Server.getQuizzes()) {
			monumentsNames.add(quiz.getMonumentName());
		}
		return new GetMonumentsResponse(monumentsNames);
	}

	@Override
	public Response handle(GetRankingCommand grc) {
		Map<String, Integer> unsortRanking = new HashMap<>();
		for (User user : Server.getUsers()) {
			unsortRanking.put(user.getUserID(), user.getScore());
		}
		return new GetRankingResponse(Server.sortByScore(unsortRanking));
	}
}
