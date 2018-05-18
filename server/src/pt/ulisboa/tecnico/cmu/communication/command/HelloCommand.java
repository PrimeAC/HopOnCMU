package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

public class HelloCommand implements Command {
	
	private static final long serialVersionUID = -8807331723807741905L;

	public HelloCommand(){
	}

	@Override
	public Response handle(CommandHandler chi) {
		return chi.handle(this);
	}
}
