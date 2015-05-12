package system;

public class TspShared implements Shared {
	
	private int value;
	
	public TspShared(int value){
		this.value = value;
	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public boolean isOlderThan(Shared other) {
		return ((Integer) other.get() < (Integer) this.get());
	}

}
