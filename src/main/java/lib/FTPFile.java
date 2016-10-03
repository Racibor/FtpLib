package lib;

public class FTPFile {
	private String name;
	private long size;
	private String owner;
	private String group;
	private boolean dir;
	
	FTPFile(String name, boolean dir) {
		this.name = name;
		this.size = size;
		this.owner = owner;
		this.group = group;
		this.dir = dir;
	}
	
	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public String getOwner() {
		return owner;
	}

	public String getGroup() {
		return group;
	}
	
	public boolean isDir() {
		return dir;
	}
}
