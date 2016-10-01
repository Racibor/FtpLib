package lib;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ControlSocket {
	private int port;
	private InetAddress IP;
	private Socket controlSocket;
	private PrintWriter pw;
	private Scanner sc;
	
	ControlSocket(Socket socket) {
		controlSocket = socket;
		IP = socket.getInetAddress();
		port = socket.getPort();
		try {
		createStreams();
		} catch(IOException e) {
			System.out.println("couldn't create streams");
		}
		String welcome;
		while((welcome = sc.nextLine()).contains("-")) {
			System.out.println(welcome);
		}
		System.out.println(welcome);
		welcome = null;
	}
	
	private void createStreams() throws IOException {
		pw = new PrintWriter(new OutputStreamWriter(controlSocket.getOutputStream()), true);
		sc = new Scanner(controlSocket.getInputStream());
	}
	
	public void login(String username) {
		send("USER " + username);
		System.out.println(sc.nextLine());
	}
	
	public void login(String username, String password) {
		send("USER " + username);
		System.out.println(sc.nextLine());
		send("PASS " + password);
		System.out.println(sc.nextLine());
	}
	
	public DataSocket createActiveDataSocket(int port) {
		send(String.valueOf(port));
		return new DataActiveSocket(this.IP, port);
	}
	
	public String pwd() {
		send("PWD");
		return sc.nextLine();
	}
	
	public FTPFile[] ls() {
		String welcome;
		while((welcome = sc.nextLine()).contains("-")) {
			System.out.println(welcome);
		}
		System.out.println(welcome);
		welcome = null;
		return null;
	}
	
	public void send(String msg) {
		pw.println(msg);
	}
	
	public void close() throws IOException {
		if (controlSocket !=null) {
			pw.close();
			sc.close();
			controlSocket.close();
		}
	}
}
