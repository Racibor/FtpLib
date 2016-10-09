package lib;

public enum TransferMode {
	BINARY("binary"), 
	ASCII("ascii");
	
	private String value;
	TransferMode(String value) {
		this.value = value;
	}
	
}
