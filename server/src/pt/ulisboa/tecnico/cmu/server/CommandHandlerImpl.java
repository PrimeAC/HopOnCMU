package pt.ulisboa.tecnico.cmu.server;

import pt.ulisboa.tecnico.cmu.command.CommandHandler;
import pt.ulisboa.tecnico.cmu.command.HelloCommand;
import pt.ulisboa.tecnico.cmu.command.SignUpCommand;
import pt.ulisboa.tecnico.cmu.command.TicketCommand;
import pt.ulisboa.tecnico.cmu.data.User;
import pt.ulisboa.tecnico.cmu.response.HelloResponse;
import pt.ulisboa.tecnico.cmu.response.Response;
import pt.ulisboa.tecnico.cmu.response.SignUpResponse;
import pt.ulisboa.tecnico.cmu.response.TicketResponse;

import javax.jws.soap.SOAPBinding;

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
	public Response handle(SignUpCommand suc) {
		for (User user: Server.getUsers()) {
			if(user.getUserID().equals(suc.getUserID())){
				//ticket userID already used
				return new SignUpResponse("NOK");
			}
		}
		User user = new User(suc.getUserID(), suc.getTicketCode(), 0);
		Server.getUsers().add(user);
		return new TicketResponse("OK");
	}

}
