package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

public class TicketCommand implements Command{

    private static final long serialVersionUID = -8807331723807741905L;
    private String ticketCode;

    public TicketCommand(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getTicketCode() {
        return this.ticketCode;
    }


}
