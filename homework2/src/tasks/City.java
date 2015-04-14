package tasks;

public class City {
	
	private Integer id;
	private double x;
	private double y;
	
	public City(Integer id, double x, double y){
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	public Integer getId(){
		return id;
	}
}