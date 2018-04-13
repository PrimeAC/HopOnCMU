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

	private static Quiz quiz1;
	private static Quiz quiz2;
	private static Quiz quiz3;
	private static Quiz quiz4;

	private static final int PORT = 9090;

	//keeps a map with: userID
	private static List<User> users = new ArrayList<User>();

	//keeps a map with: monumentID:quiz
	private static Map<String, Quiz> quizzes = new HashMap<String, Quiz>();

	//keeps a map with: ticketCode:userID
	private static Map<String, String> tickets = new HashMap<String, String>();

	/*
	//for test only
	private static Map<String, List<String>> userAnswers = new HashMap<String, List<String>>();
	private static List<String> aux = new ArrayList<String>();*/

	public static void main(String[] args) throws Exception {
		initializeM1();
		initializeM2();
		initializeM3();
		initializeM4();
		initializeQuizzes();


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
		quiz1 = new Quiz("M1", m1_1, m1_2, m1_3);
		quiz2 = new Quiz("M2", m2_1, m2_2, m2_3);
		quiz3 = new Quiz("M3", m3_1, m3_2, m3_3);
		quiz4 = new Quiz("M4", m4_1, m4_2, m4_3);
		quizzes.put(quiz1.getMonumentID(), quiz1);
		quizzes.put(quiz2.getMonumentID(), quiz2);
		quizzes.put(quiz3.getMonumentID(), quiz3);
		quizzes.put(quiz4.getMonumentID(), quiz4);

	}

	private static boolean usedTicket(String ticketCode) {
		return tickets.containsKey(ticketCode);
	}

	private static void createNewUser(String ticketCode, String userID){
		tickets.put(ticketCode, userID);
	}

	public static List<User> getUsers(){
		return users;
	}

	public static Quiz getQuiz(String monumentID){
		for (Map.Entry<String, Quiz> quiz: quizzes.entrySet()) {
			if(quiz.getKey().equals(monumentID)){
				return quiz.getValue();
			}
		}
		return null;
	}






















/*

	//for test only

	private static void initializeAux() {
		aux.add("1515");
		aux.add("1519");
		aux.add("Torre dos Jerónimos");
		aux.add("bruno");
	}

	private static void initTest(){
		userAnswers.put("M1", aux);
	}

	private static void initUsers(){
		List<String[]> b = new ArrayList<String[]>();
		b.add(new String[] {""});
		b.add(new String[] {"0"});
		users.put("bruno", b);
	}

	private static void print(){
		for (Map.Entry<String, List<String[]>> entry : users.entrySet()) {
			String key = entry.getKey();
			for (String[] value : entry.getValue()) {
				for (String aux : value) {
					System.out.println("VALUE: " + aux);
				}
			}
		}
	}

	*/

}

