package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ControlSocket {
	private int port;
	private InetAddress IP;
	private Socket controlSocket;
	private int dataPort;
	private PrintWriter pw;
	private BufferedReader sc;
	private ConnectionType connectionType;

	//TODO returning welcome message 
	ControlSocket(Socket socket) {
		connectionType = ConnectionType.ACTIVE;
		controlSocket = socket;
		IP = socket.getInetAddress();
		port = socket.getPort();
		try {
			createStreams();
		} catch (IOException e) {
			
		}
	}

	private void createStreams() throws IOException {
		pw = new PrintWriter(new OutputStreamWriter(controlSocket.getOutputStream()), true);
		sc = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
	}
	
	private String[] getWelcomeMessage() {
		String[] welcome = null;
		int count=0;
		try {
			while((welcome[count] = read())!=null) {
				count++;
			}
			return welcome;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public void setPassiveConnection() {
		connectionType = ConnectionType.PASSIVE;
	}

	public void setActiveConnection() {
		connectionType = ConnectionType.ACTIVE;
	}

	public boolean login(String username) throws IOException {
		send("USER " + username);
		validate("230");
		return true;
	}

	public boolean login(String username, String password) throws IOException {
		send("USER " + username);
		if (validate("331")) {
			send("PASS " + password);
		} else {
			return false;
		}
		return validate("230");
	}

	public DataSocket createDataSocket() {
		if (ConnectionType.valueOf("ACTIVE").equals(connectionType)) {
			return new DataActiveSocket(this.IP, (14 * 256) + dataPort);
		} else {
			return new DataPassiveSocket(this.IP, dataPort);
		}
	}

	public int getDataPort() {
		return dataPort;
	}

	// make passive mode
	public boolean setDataPort(InetAddress ip) throws IOException {
		if (ConnectionType.valueOf("ACTIVE").equals(connectionType)) {
			this.dataPort = PortGenerator.generatePort();
			byte[] hostBytes = controlSocket.getInetAddress().getAddress();
			send("PORT " + hostBytes[0] + "," + hostBytes[1] + "," + hostBytes[2] + "," + hostBytes[3] + ",14,"
					+ dataPort);
			return validate("200");
		} else {
			String helper;
			int portHelper;
			send("PASV");
			helper = read();
			if (helper.substring(0, 3).equals("227")) {
				helper = helper.substring(helper.indexOf(",") + 1, helper.length() - 1);
				helper = helper.substring(helper.indexOf(",") + 1);
				helper = helper.substring(helper.indexOf(",") + 1);
				helper = helper.substring(helper.indexOf(",") + 1);
				portHelper = Integer.parseInt(helper.substring(0, helper.indexOf(",")));
				System.out.println(portHelper);
				this.dataPort = (portHelper * 256) + Integer.parseInt(helper.substring(helper.lastIndexOf(",") + 1));
				return true;
			} else {
				return false;
			}
		}
	}

	public String pwd() throws IOException {
		send("PWD");
		return sc.readLine();
	}

	public void send(String msg) {
		pw.println(msg);
	}

	public String read() throws IOException {
		return sc.readLine();
	}

	public void close() throws IOException {
		if (controlSocket != null) {
			pw.close();
			sc.close();
			controlSocket.close();
		} else {
		}
	}

	public boolean validate(String code) {
		String helper;
		try {
			if ((helper = read()).substring(0, 3).equals(code)) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
}
