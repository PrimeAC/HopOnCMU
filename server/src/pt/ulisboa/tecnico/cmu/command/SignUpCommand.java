package pt.ulisboa.tecnico.cmu.command;

import pt.ulisboa.tecnico.cmu.response.Response;

public class SignUpCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;
    private String ticketCode;
    private String userID;

    public SignUpCommand(String ticketCode, String userID) {
        this.ticketCode = ticketCode;
        this.userID = userID;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getTicketCode() {
        return this.ticketCode;
    }

    public String getUserID() {
        return this.userID;
    }
}
