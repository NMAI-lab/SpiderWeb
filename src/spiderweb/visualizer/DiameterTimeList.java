package spiderweb.visualizer;

public class DiameterTimeList {
	
	private long time;
	private double diameter;
	
	
	public DiameterTimeList(long time, double diameter) {
		this.time = time;
		this.diameter = diameter;
	}
	
	public long getTime() {
		return time;
	}
	
	public double getDiameter() {
		return diameter;
	}
	
}
