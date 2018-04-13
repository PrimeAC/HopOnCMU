package pt.ulisboa.tecnico.cmu.response;

import pt.ulisboa.tecnico.cmu.command.GetMonumentsCommand;

public interface ResponseHandler {
	public void handle(HelloResponse hr);
	public void handle(GetMonumentsResponse gmr);
}
