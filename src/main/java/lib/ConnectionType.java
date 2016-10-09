package lib;

public enum ConnectionType {
	ACTIVE("active"), 
	PASSIVE("active");
	
	private String value;
	
	ConnectionType(String value) {
		this.value = value;
	}
}
