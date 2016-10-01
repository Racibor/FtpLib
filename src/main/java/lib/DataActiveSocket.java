package lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DataActiveSocket implements DataSocket {
	private Socket dataSocket;

	DataActiveSocket(InetAddress IP, int port) {
		try {
			dataSocket = new Socket(IP, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return dataSocket.getOutputStream();
	}

	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return dataSocket.getInputStream();
	}

	public void close() throws IOException {
		dataSocket.close();
		dataSocket = null;
	}

}
