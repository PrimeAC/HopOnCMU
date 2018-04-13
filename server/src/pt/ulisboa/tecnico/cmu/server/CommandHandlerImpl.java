package pt.ulisboa.tecnico.cmu.server;

import pt.ulisboa.tecnico.cmu.command.CommandHandler;
import pt.ulisboa.tecnico.cmu.command.GetMonumentsCommand;
import pt.ulisboa.tecnico.cmu.command.HelloCommand;
import pt.ulisboa.tecnico.cmu.command.TicketCommand;
import pt.ulisboa.tecnico.cmu.data.User;
import pt.ulisboa.tecnico.cmu.response.GetMonumentsResponse;
import pt.ulisboa.tecnico.cmu.response.HelloResponse;
import pt.ulisboa.tecnico.cmu.response.Response;
import pt.ulisboa.tecnico.cmu.response.TicketResponse;

import java.util.ArrayList;
import java.util.List;

public class CommandHandlerImpl implements CommandHandler {

	@Override
	public Response handle(HelloCommand hc) {
		System.out.println("Received: " + hc.getMessage());
		return new HelloResponse("Hi from Server!");
	}

	@Override
	public Response handle(TicketCommand tc) {
		for (User user: Server.getUsers()) {
			if(user.getTicketCode().equals(tc.getTicketCode())){
				//ticket code already used
				return new TicketResponse("OK");
			}
		}
		return new TicketResponse("NOK");
	}

	@Override
	public Response handle(GetMonumentsCommand gmc) {
		List<String> monumentsNames = new ArrayList<>();
		for (Quiz quiz: Server.getQuizzes()) {
			monumentsNames.add(quiz.getMonumentName());
		}
		return new GetMonumentsResponse(monumentsNames);
	}

}
