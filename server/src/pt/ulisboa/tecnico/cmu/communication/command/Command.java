package pt.ulisboa.tecnico.cmu.communication.command;

import java.io.Serializable;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

public interface Command extends Serializable {
	Response handle(CommandHandler ch);
}
