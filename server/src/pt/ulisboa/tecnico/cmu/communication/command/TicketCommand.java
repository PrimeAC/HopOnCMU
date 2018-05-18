package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

import java.security.PublicKey;

public class TicketCommand implements Command{

    private static final long serialVersionUID = -8807331723807741905L;
    private String ticketCode;
    private PublicKey clientPubKey;


    public TicketCommand(String ticketCode, PublicKey clientPubKey) {
        this.ticketCode = ticketCode;
        this.clientPubKey = clientPubKey;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getTicketCode() {
        return this.ticketCode;
    }

    public PublicKey getClientPubKey() {
        return clientPubKey;
    }


}
