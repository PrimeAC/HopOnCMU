package pt.ulisboa.tecnico.cmu.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;

import pt.ulisboa.tecnico.cmu.command.Command;
import pt.ulisboa.tecnico.cmu.data.Quiz;
import pt.ulisboa.tecnico.cmu.response.Response;
import pt.ulisboa.tecnico.cmu.data.Question;
import pt.ulisboa.tecnico.cmu.data.User;

public class Server {

	//Monuments:
	//M1 - Torre de Belém
	//M2 - Mosteiro dos Jerónimos
	//M3 - Padrão dos Descobrimentos
	//M4 - Castelo de São Jorge

	private static Question m1_1;
	private static Question m1_2;
	private static Question m1_3;
	private static Question m2_1;
	private static Question m2_2;
	private static Question m2_3;
	private static Question m3_1;
	private static Question m3_2;
	private static Question m3_3;
	private static Question m4_1;
	private static Question m4_2;
	private static Question m4_3;

	private static final int PORT = 9090;

	//keeps a list of users
	private static List<User> users = new ArrayList<>();

	//keeps a list of quizzes
	private static List<Quiz> quizzes = new ArrayList<>();

	//keeps a list of valid codes
	private static List<String> tickets = new ArrayList<>();


	public static void main(String[] args) throws Exception {
		initializeM1();
		initializeM2();
		initializeM3();
		initializeM4();
		initializeQuizzes();
		initializeTickets();


		CommandHandlerImpl chi = new CommandHandlerImpl();
		ServerSocket socket = new ServerSocket(PORT);
		Socket client = null;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Server now closed.");
				try { socket.close(); }
				catch (Exception e) { }
			}
		});

		System.out.println("Server is accepting connections at " + PORT);

		while (true) {
			try {
				client = socket.accept();

				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
				Command cmd =  (Command) ois.readObject();
				Response rsp = cmd.handle(chi);

				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				oos.writeObject(rsp);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (client != null) {
					try { client.close(); }
					catch (Exception e) {}
				}
			}
		}
	}

	private static void initializeM1() {
		m1_1 = new Question("Em que ano começou a construção da Torre de Belém?",
				"1514", "1515", "1513", "1512", "1515");
		m1_2 = new Question("Em que ano foi inaugurada a Torre de Belém?",
				"1514", "1515", "1513", "1519", "1519");
		m1_3 = new Question("Que outro nome se dá à Torre de Belém?",
				"Torre de São Vicente", "Torre dos Jerónimos", "Torre Francisco de Arruda", "Torre Cultural de Belém", "Torre de São Vicente");
	}

	private static void initializeM2() {
		m2_1 = new Question("Em que ano começou a construção do Mosteiro dos Jerónimos?",
				"1510", "1505", "1512", "1502","1502");
		m2_2 = new Question("Em que ano foi inaugurado o Mosteiro dos Jerónimos?",
				"1514", "1515", "1513", "1519", "1513");
		m2_3 = new Question("Que outro nome se dá ao Mosteiro dos Jerónimos?",
				"Torre de São Vicente", "Torre dos Jerónimos", "Mosteiro de Santa Maria de Belém", "Mosteiro de Belém", "Mosteiro de Santa Maria de Belém");
	}

	private static void initializeM3() {
		m3_1 = new Question("Em que ano foi inaugurado o Padrão dos Descobrimentos original?",
				"1933", "1940", "1936", "1945", "1940");
		m3_2 = new Question("Em que ano foi inaugurado o Padrão dos Descobrimentos?",
				"1953", "1955", "1960", "1950", "1960");
		m3_3 = new Question("Que outro nome se dá ao Padrão dos Descobrimentos?",
				"Monumento aos Navegantes", "Torre dos Descobrimentos", "Padrão de Santa Maria de Belém", "Monumento de Belém", "Monumento aos Navegantes");
	}

	private static void initializeM4() {
		m4_1 = new Question("Em que século foi definido o nome do Castelo de São Jorge?",
				"XIV", "XV", "XVI", "XVII", "XIV");
		m4_2 = new Question("Qual o rei que definiu o nome do castelo?",
				"D.João I", "D.Duarte I", "D.João II", "D.Fernando I", "D.João I");
		m4_3 = new Question("Em que ano foi declarado Monumento Nacional?",
				"1900", "1905", "1910", "1915", "1910");
	}

	private static void initializeQuizzes(){
		List<Question> questions = new ArrayList<>();
		questions.add(m1_1);
		questions.add(m1_2);
		questions.add(m1_3);
		Quiz quiz1 = new Quiz("M1", questions);
		questions.clear();
		questions.add(m2_1);
		questions.add(m2_2);
		questions.add(m2_3);
		Quiz quiz2 = new Quiz("M2", questions);
		questions.clear();
		questions.add(m3_1);
		questions.add(m3_2);
		questions.add(m3_3);
		Quiz quiz3 = new Quiz("M3", questions);
		questions.clear();
		questions.add(m4_1);
		questions.add(m4_2);
		questions.add(m4_3);
		Quiz quiz4 = new Quiz("M4", questions);
		quizzes.add(quiz1);
		quizzes.add(quiz2);
		quizzes.add(quiz3);
		quizzes.add(quiz4);
	}

	public static boolean validTicket(String ticketCode) {
		return tickets.contains(ticketCode);
	}

	private static void initializeTickets(){
		tickets.add("123456");
		tickets.add("654321");
		tickets.add("123123");
		tickets.add("321321");
		tickets.add("111111");
		tickets.add("222222");
		tickets.add("333333");
		tickets.add("444444");
		tickets.add("555555");
		tickets.add("666666");
	}

	public static List<User> getUsers(){
		return users;
	}

	public static Quiz getQuiz(String monumentID){
		for (Quiz quiz: quizzes) {
			if(quiz.getMonumentID().equals(monumentID)){
				return quiz;
			}
		}
		return null;
	}

}

