package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public class GetRankingCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;


    public GetRankingCommand() {

    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

}
