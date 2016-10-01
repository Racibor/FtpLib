package lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataSocket {
	public OutputStream getOutputStream() throws IOException;
	public InputStream getInputStream() throws IOException;
	public void close() throws IOException;
}
