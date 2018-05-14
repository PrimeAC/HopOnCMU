package pt.ulisboa.tecnico.cmu.server;

import pt.ulisboa.tecnico.cmu.communication.command.*;
import pt.ulisboa.tecnico.cmu.data.Quiz;
import pt.ulisboa.tecnico.cmu.data.SessionID;
import pt.ulisboa.tecnico.cmu.data.User;
import pt.ulisboa.tecnico.cmu.data.Question;
import pt.ulisboa.tecnico.cmu.communication.response.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Date;

import java.util.logging.Logger;


public class CommandHandlerImpl implements CommandHandler {

  private Logger LOGGER = Logger.getLogger(CommandHandlerImpl.class.getName());

	@Override
	public Response handle(HelloCommand hc) {
    LOGGER.info("Received: " + hc.getMessage());
		return new HelloResponse("Hi from Server!");
	}

	@Override
	public Response handle(TicketCommand tc) {
		LOGGER.info("Bilhete recebido " + tc.getTicketCode());
		if (Server.validTicket(tc.getTicketCode())) {
			for (User user : Server.getUsers()) {
				if (user.getTicketCode().equals(tc.getTicketCode())) {
					//ticket code already used so can login
                    String sessionID;
                    while (true){
                        sessionID = generateSessionID(user.getUserID());
                        if (sessionID != null){
                            break;
                        }
                    }
					return new TicketResponse("OK", user.getUserID(), getMonuments(user.getUserID()), sessionID);
				}
			}
			//ticket never used (NU), so need to create an account
			return new TicketResponse("NU", null, null,  null);
		}
		return new TicketResponse("NOK", null, null, null);
	}

	@Override
	public Response handle(SignUpCommand suc) {
		LOGGER.info("Bilhete recebido " + suc.getTicketCode() + " user: " + suc.getUserID());
		for (User user : Server.getUsers()) {
			if (user.getUserID().equals(suc.getUserID())) {
				//ticket userID already used
				return new SignUpResponse("NOK", null, null, null);
			}
		}
		User user = new User(suc.getUserID(), suc.getTicketCode(), 0);
		Server.getUsers().add(user);
        String sessionID;
		while (true){
            sessionID = generateSessionID(user.getUserID());
            if (sessionID != null){
                break;
            }
        }
		return new SignUpResponse("OK", user.getUserID(), getMonuments(user.getUserID()), sessionID);
	}

	@Override
	public Response handle(GetQuizCommand gqc) {
		LOGGER.info("Quiz " + gqc.getMonumentName());
		if (validateSessionID(gqc.getSessionID(), gqc.getUserID())) {
            for (Quiz quiz : Server.getQuizzes()) {
                if (quiz.getMonumentName().equals(gqc.getMonumentName())) {
                    return new GetQuizResponse(quiz);
                }
            }
        }
        return new GetQuizResponse(null);
    }

	@Override
	public Response handle(GetRankingCommand grc) {
	    System.out.println("recebi um get ranking " + grc.getSessionID() + " //// " + grc.getUserID());
        if (validateSessionID(grc.getSessionID(), grc.getUserID())) {
            System.out.println("entrei no get ranking");
            Map<String, Integer> unsortRanking = new HashMap<>();
            for (User user : Server.getUsers()) {
                unsortRanking.put(user.getUserID(), user.getScore());
            }
            return new GetRankingResponse(Server.sortByScore(unsortRanking));
        }
        return new GetRankingResponse(null);
	}

	@Override
	public Response handle(SubmitQuizCommand sqc) {
		    LOGGER.info("Recebi as respostas ao quiz " + sqc.getAnswers().get(0) + sqc.getAnswers().get(1) + sqc.getAnswers().get(2) );
        if (validateSessionID(sqc.getSessionID(), sqc.getUserID())) {
            List<Question> questions = Server.getQuiz(sqc.getQuizName());
            int cnt = 0;
            int score = 0;
            if(questions != null){
                for (Question question: questions) {
                    if(sqc.getAnswers().get(cnt).equals(question.getSolution())){
                        //need to increase the user score by one
                        score++;
                    }
                    cnt++;
                }
                System.out.println("Score " + score);
                Server.updateUserScore(sqc.getUserID(), score, sqc.getQuizName());
                return new SubmitQuizResponse("OK");
            }
        }
		return new SubmitQuizResponse("NOK");
	}

	private List<String> getMonuments(String userID) {
		List<String> monumentsNames = new ArrayList<>();
		for (Quiz quiz : Server.getQuizzes()) {
		    if(quiz.getUserAnswers().containsKey(userID)){
                monumentsNames.add(quiz.getMonumentName() + "|T");
            }
            else {
                monumentsNames.add(quiz.getMonumentName() + "|F");
            }
		}
		return monumentsNames;
	}

	private String generateSessionID(String userID) {
	    try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //generate a random number
            String sessionID = Integer.toString(secureRandom.nextInt());
            System.out.println("SESSION ID: " + sessionID);
            if (notUSedSessionID(sessionID, userID)){
                Server.updateSessionID(userID, new SessionID(sessionID, new Date()));
                return sessionID;
            }
        } catch (NoSuchAlgorithmException nae){
	        nae.getMessage();
        }
        return null;
    }

    private boolean notUSedSessionID(String sessionID, String userID) {
        if (Server.getSessionID().containsKey(userID)){
            SessionID session = Server.getSessionID().get(userID);
            return !session.getSessionID().equals(sessionID);
        }
        return true;
    }

    private boolean validateSessionID(String sessionID, String userID) {
	    if (Server.getSessionID().containsKey(userID)){
	        System.out.println("tem o user");
            SessionID session = Server.getSessionID().get(userID);
            if (session.getSessionID().equals(sessionID)){
                System.out.println("mesmo session id");
                Date last = session.getGeneratedTime();
                Date now = new Date();
                long diff = Math.abs(now.getTime() - last.getTime());
                System.out.println(diff);
                // 300000 miliseconds equals 5 minutes of a session
                if (diff < 300000) {
                    session.setGeneratedTime(now);
                    return true;
                }
                else {
                    Server.removeSession(userID);
                    return false;
                }
            }
        }
        return false;
    }

}
