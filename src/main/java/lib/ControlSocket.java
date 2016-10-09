package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ControlSocket {
	private int port;
	private InetAddress IP;
	private LoggerImpl logger;
	private Socket controlSocket;
	private int dataPort;
	private PrintWriter pw;
	private BufferedReader sc;
	private ConnectionType connectionType;
	
	ControlSocket(Socket socket) {
		connectionType = ConnectionType.ACTIVE;
		logger = new LoggerImpl();
		controlSocket = socket;
		IP = socket.getInetAddress();
		port = socket.getPort();
		try {
		createStreams();
		String welcome;
			while((welcome = sc.readLine()).contains("-")) {
				logger.log(welcome);
			}
		logger.log(welcome);
		welcome = null;
		} catch(IOException e) {
			logger.log("couldn't create streams");
		}
	}
	
	private void createStreams() throws IOException {
		pw = new PrintWriter(new OutputStreamWriter(controlSocket.getOutputStream()), true);
		sc = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
	}
	
	public void setPassiveConnection() {
		connectionType = ConnectionType.PASSIVE;
	}
	
	public void setActiveConnection() {
		connectionType = ConnectionType.ACTIVE;
	}
	
	public boolean login(String username) throws IOException {
		send("USER " + username);
		logger.log(sc.readLine());
		return true;
	}
	
	public boolean login(String username, String password) throws IOException {
		send("USER " + username);
		if(validate("331")) {
		send("PASS " + password);
		} else {
			logger.log("invalid password");
			return false;
		}
		return validate("230");
	}
	
	public DataSocket createActiveDataSocket() {
		return new DataActiveSocket(this.IP, (14*256)+dataPort);
	}
	
	public int getDataPort() {
		return dataPort;
	}
	
	//make passive mode
	public void setDataPort(InetAddress ip, int dataPort) throws IOException {
		if(connectionType.valueOf("ACTIVE").equals(connectionType)) {
		this.dataPort = dataPort;
		byte[] hostBytes = controlSocket.getInetAddress().getAddress();
		send("PORT " + hostBytes[0] + "," + hostBytes[1] + "," + hostBytes[2] + "," + hostBytes[3] + ",14," + dataPort);
		validate("200");
		} else {
			
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
		if (controlSocket !=null) {
			pw.close();
			sc.close();
			controlSocket.close();
		}
	}
	
	public LoggerImpl getLogger() {
		return logger;
	}
	public boolean validate(String code) {
		String helper;
		try {
			if((helper = read()).substring(0,3).equals(code)) {
				logger.log("validation succesfull");
				logger.log(helper);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.log("couldn't validate respond");
			return false;
		}
		logger.log("error validating");
		logger.log(helper);
		return false;
	}
}
