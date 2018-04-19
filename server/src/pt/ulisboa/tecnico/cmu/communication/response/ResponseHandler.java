package pt.ulisboa.tecnico.cmu.communication.response;

public interface ResponseHandler {
	public void handle(HelloResponse hr);
	public void handle(TicketResponse tr);
	public void handle(SignUpResponse sur);
	public void handle(GetQuizResponse gqr);
  	public void handle(GetMonumentsResponse gmr);
  	public void handle(GetRankingResponse grr);
}
