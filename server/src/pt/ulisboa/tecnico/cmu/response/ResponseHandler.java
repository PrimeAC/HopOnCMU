package pt.ulisboa.tecnico.cmu.response;

import pt.ulisboa.tecnico.cmu.command.GetMonumentsCommand;
import pt.ulisboa.tecnico.cmu.command.SignUpCommand;

public interface ResponseHandler {
	public void handle(HelloResponse hr);
	public void handle(TicketResponse tr);
  public void handle(GetQuizResponse gqr);
	public void handle(SignUpResponse sur);
  public void handle(GetMonumentsResponse gmr);
  	public void handle(GetRankingResponse grr);
}
