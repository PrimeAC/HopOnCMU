package pt.ulisboa.tecnico.cmu.communication.command;

import java.security.PublicKey;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

public class SignUpCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;
    private String ticketCode;
    private String userID;
    private PublicKey clientPubKey;


    public SignUpCommand(String ticketCode, String userID, PublicKey clientPubKey) {
        this.ticketCode = ticketCode;
        this.userID = userID;
        this.clientPubKey = clientPubKey;
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

    public PublicKey getClientPubKey() {
        return clientPubKey;
    }
}
