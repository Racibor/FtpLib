package lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DataPassiveSocket implements DataSocket {
	private Socket socket;
	
	DataPassiveSocket(InetAddress ip, int port) {
		try {
			socket = new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return socket.getOutputStream();
	}

	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return socket.getInputStream();
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
		socket.close();
		socket = null;
	}

}
