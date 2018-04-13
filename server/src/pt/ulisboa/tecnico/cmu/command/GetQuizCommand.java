package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public class GetQuizCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;
    private String monumentID;

    public GetQuizCommand(String monumentID) {
        this.monumentID = monumentID;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getMonumentID() {
        return this.monumentID;
    }

}
