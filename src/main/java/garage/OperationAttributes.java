package garage;

public class OperationAttributes {
	private String key1;
	private KeyAttribute key2;
	
	public OperationAttributes() {
		this.setKey1("this is my key1");
		this.key2 = new KeyAttribute();
	}

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public KeyAttribute getKey2() {
		return key2;
	}

	public void setKey2(KeyAttribute key2) {
		this.key2 = key2;
	}
	
}
