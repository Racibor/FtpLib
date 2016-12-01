package lib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

public class FTPClient {
	private ControlSocket controlSocket;
	private DataSocket dataSocket;

	private Scanner dataReader;
	private PrintWriter dataWriter;

	private BufferedWriter fileWriter;
	private BufferedReader fileReader;

	private BufferedOutputStream out;
	private BufferedInputStream in;

	private InetAddress IP;
	private int port;
	private String username;
	private String password;
	private TransferMode transferMode;
	private ArrayList<FTPFile> files;
	private Logger logger;

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

	public void activeMode() {
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
			controlSocket.setDataPort(this.IP);
			controlSocket.send("MLSD");
			dataSocket = controlSocket.createDataSocket();
			dataReader = new Scanner(dataSocket.getInputStream());
			while (dataReader.hasNextLine()) {
				helper = dataReader.nextLine();
				if (helper.substring(5, 8).equals("dir")) {
					files.add(new FTPFile(helper.substring(helper.indexOf(" ", 18) + 1), true));
				} else {
					files.add(new FTPFile(helper.substring(helper.indexOf(" ", 18) + 1), false));
				}
			}
			dataReader.close();
			dataSocket.close();
			dataReader = null;
			dataSocket = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return files;
	}

	public boolean upload(String path) {
		// setting up a new port for data connection
		try {
			String helper;
			controlSocket.setDataPort(this.IP);
			// sending "STOR" command
			controlSocket.send("STOR " + path);
			if (controlSocket.validate("150")) {
				// creating data connection and opening streams for socket and
				// file
				dataSocket = controlSocket.createDataSocket();
				if (TransferMode.valueOf("ASCII").equals(transferMode)) {
					dataWriter = new PrintWriter(dataSocket.getOutputStream());
					fileReader = new BufferedReader(new FileReader(path));
					while ((helper = fileReader.readLine()) != null) {
						dataWriter.print(helper + " \r\n");
					}
					dataWriter.flush();
					dataWriter.close();
					fileReader.close();
				} else {
					byte[] buffer = new byte[1024];
					int count;
					in = new BufferedInputStream(new FileInputStream(path));
					out = new BufferedOutputStream(dataSocket.getOutputStream());
					while ((count = in.read(buffer)) >= 0) {
						out.write(buffer, 0, count);
					}
					out.flush();
					out.close();
					in.close();
				}
				dataSocket.close();
				controlSocket.validate("226");
				helper = null;
			}
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			in = null;
			out = null;
			dataSocket = null;
			dataWriter = null;
			fileReader = null;
		}
		return true;
	}
	
	public boolean download(String path) {
		controlSocket.setDataPort();
		
		controlSocket.send("" + path);
			if(controlSocket.validate(code)) {
				dataSocket = controlSocket.createDataSocket();
			}
	}

	public boolean download(String file, File dir) {
		return download(file, dir.getPath());
	}

	public boolean download(String file, String directory) {
		try {
			controlSocket.setDataPort(this.IP);
			controlSocket.send("RETR " + file);
			if (controlSocket.validate("150")) {
				dataSocket = controlSocket.createDataSocket();
				if (TransferMode.valueOf("ASCII").equals(transferMode)) {
					dataReader = new Scanner(dataSocket.getInputStream());
					fileWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
					while (dataReader.hasNextLine()) {
						fileWriter.write(dataReader.nextLine() + "\r\n");
					}
					fileWriter.flush();
					fileWriter.close();
					dataReader.close();
				} else {
					int count;
					byte[] buffer = new byte[1024];
					in = new BufferedInputStream(dataSocket.getInputStream());
					out = new BufferedOutputStream(new FileOutputStream(directory));

					while ((count = in.read(buffer)) != -1) {
						out.write(buffer, 0, count);
					}
					out.flush();
					out.close();
					in.close();
					buffer = null;
					count = -1;
				}
				dataSocket.close();
				// controlSocket.validate(code);
				return true;
			}
		} catch (IOException e) {
			return false;
		} finally {
			fileWriter = null;
			dataReader = null;
			in = null;
			out = null;
			dataSocket = null;
		}
		return false;
	}

	public String pwd() throws IOException {
		return controlSocket.pwd();
	}
}
