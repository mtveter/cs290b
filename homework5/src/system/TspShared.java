package system;

public class TspShared implements Shared {
	
	private double value;
	
	public TspShared(double value){
		this.value = value;
	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public boolean isOlderThan(Shared other) {
		return ((double) other.get() < (double) this.get());
	}

}
