package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

public class GetQuizCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;
    private String monumentName;

    public GetQuizCommand(String monumentName) {
        this.monumentName = monumentName;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getMonumentName() {
        return this.monumentName;
    }

}
