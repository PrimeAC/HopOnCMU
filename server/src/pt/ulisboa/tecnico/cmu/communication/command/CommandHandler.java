package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.communication.sealed.SealedCommand;

public interface CommandHandler {
	public Response handle(HelloCommand hc);
	public Response handle(TicketCommand tc);
	public Response handle(SignUpCommand suc);
	public Response handle(GetQuizCommand gqc);
  	public Response handle(GetRankingCommand grc);
  	public Response handle(SubmitQuizCommand sqc);
	public Response handle(SealedCommand sm);

}
