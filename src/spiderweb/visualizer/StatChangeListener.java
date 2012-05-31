package spiderweb.visualizer;

import java.util.ArrayList;

import spiderweb.graph.LogEvent;



public interface StatChangeListener {

	
	public void processChange(StatisticEvent evt);
	public void checkDiameter(long time, boolean forward);
	public void setQueryList(ArrayList<LogEvent> list);
	
}
