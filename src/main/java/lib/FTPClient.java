package lib;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class FTPClient {
	private ControlSocket controlSocket;
	private DataSocket dataSocket;
	private Scanner dataReader;
	private PrintWriter dataWriter;
	private FileWriter fileWriter;
	private BufferedOutputStream out;
	private FileInputStream in;
	private BufferedReader fileReader;
	private InetAddress IP;
	private int port;
	private String username;
	private String password;
	private TransferMode transferMode;
	private ArrayList<FTPFile> files;

	FTPClient(String ip, int port) throws UnknownHostException, IOException {
		this.IP = InetAddress.getByName(ip);
		this.port = port;
		transferMode = TransferMode.ASCII;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setIP(String ip) throws UnknownHostException {
		this.IP = InetAddress.getByName(ip);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setBinaryTransferMode() {
		transferMode = TransferMode.BINARY;
	}

	public void setASCIITransferMode() {
		transferMode = TransferMode.ASCII;
	}

	public void passiveMode() {
		controlSocket.setPassiveConnection();
	}

	public void activeConnection() {
		controlSocket.setActiveConnection();
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void connect() throws IOException {
		controlSocket = new ControlSocket(new Socket(this.IP, this.port));
		login();
	}

	private void login() throws IOException {
		if (password == null) {
			controlSocket.login(username);
		} else {
			controlSocket.login(username, password);
		}
	}

	// Returning list of Files, and saving to and array of FTPFiles
	public ArrayList<FTPFile> ls() {
		String helper;
		files = new ArrayList<FTPFile>();
		try {
			controlSocket.setDataPort(this.IP, PortGenerator.generatePort());
		} catch (IOException e) {
			getLogger().log("error while reading from Socket buffer");
		}
		controlSocket.send("MLSD");
		try {
			dataSocket = controlSocket.createActiveDataSocket();
			dataReader = new Scanner(dataSocket.getInputStream());
			while (dataReader.hasNextLine()) {
				helper = dataReader.nextLine();
				if (helper.substring(5, 8).equals("dir")) {
					files.add(new FTPFile(helper.substring(helper.indexOf(" ", 18) + 1), true));
					getLogger().log("added: " + helper);
				} else {
					files.add(new FTPFile(helper.substring(helper.indexOf(" ", 18) + 1), false));
					getLogger().log("added: " + helper);
				}
			}
			dataReader.close();
			dataSocket.close();
			dataReader = null;
			dataSocket = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return files;
	}

	public boolean upload(String path) {
		// setting up a new port for data connection
		try {
			String helper;
			controlSocket.setDataPort(this.IP, PortGenerator.generatePort());
			// sending "STOR" command
			controlSocket.send("STOR " + path);
			if (controlSocket.validate("150")) {
				// creating data connection and opening streams for socket and
				// file
				dataSocket = controlSocket.createActiveDataSocket();
				if (TransferMode.valueOf("ASCII").equals(transferMode)) {

					dataWriter = new PrintWriter(dataSocket.getOutputStream());
					fileReader = new BufferedReader(new FileReader(path));
					while ((helper = fileReader.readLine()) != null) {
						dataWriter.print(helper + " \r\n");
					}
					dataWriter.flush();
					System.out.println("here");
				} else {
					byte[] buffer = new byte[1024];
					int count;
					in = new FileInputStream(path);
					out = new BufferedOutputStream(dataSocket.getOutputStream());
					while ((count = in.read(buffer)) >= 0) {
						out.write(buffer, 0, count);
					}
				}
				dataSocket.close();
				controlSocket.validate("226");
				helper = null;
			}
		} catch (SocketException e) {
			getLogger().log("Problem with connection");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			getLogger().log("Problem opening Streams");
			e.printStackTrace();
			return false;
		} finally {
			/*
			 * try { if (fileReader != null) { fileReader.close(); } dataSocket
			 * = null; dataWriter = null; fileReader = null; } catch
			 * (IOException e) { getLogger().log("probelm closing streams"); }
			 */
		}
		return true;
	}

	public String pwd() throws IOException {
		return controlSocket.pwd();
	}

	public LoggerImpl getLogger() {
		return controlSocket.getLogger();
	}

}
