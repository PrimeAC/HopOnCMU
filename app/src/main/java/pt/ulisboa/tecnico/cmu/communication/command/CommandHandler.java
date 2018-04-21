package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

public interface CommandHandler {
	public Response handle(HelloCommand hc);
	public Response handle(TicketCommand tc);
	public Response handle(SignUpCommand suc);
	public Response handle(GetQuizCommand gqc);
  	public Response handle(GetRankingCommand grc);
}
