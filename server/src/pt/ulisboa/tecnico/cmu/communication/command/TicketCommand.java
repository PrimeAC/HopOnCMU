package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

import javax.crypto.SecretKey;

public class TicketCommand implements Command{

    private static final long serialVersionUID = -8807331723807741905L;
    private String ticketCode;
    private SecretKey randomAESKey;


    public TicketCommand(String ticketCode, SecretKey randomAESKey) {
        this.ticketCode = ticketCode;
        this.randomAESKey = randomAESKey;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getTicketCode() {
        return this.ticketCode;
    }

    public SecretKey getRandomAESKey() {
        return randomAESKey;
    }

}
