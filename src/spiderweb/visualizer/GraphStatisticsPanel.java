package spiderweb.visualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;

import spiderweb.graph.LogEvent;
import spiderweb.graph.P2PNetworkGraph;
import spiderweb.graph.P2PVertex;
import spiderweb.graph.PeerVertex;

public class GraphStatisticsPanel extends JPanel implements StatChangeListener{

	/**eclipse generated Serial UID*/
	private static final long serialVersionUID = 8407256043023483988L;
	
	private int numberPeersOnline;
	private double diameter;
	private double cSize;
	private JFreeChart chart;
	private JLabel numberPeersOnlineLabel;
	private JLabel queryLabel;
	private JLabel diameterLabel;
	private JLabel cSizePerPeer;
	private P2PNetworkGraph currentGraph;
	private ArrayList<QueryFreq> queryFreq;
	private boolean directed;
	
	
	private ArrayList<DiameterTimeList> diameterTimeList;
	
	
	private JTabbedPane jtabbedPane;
	private JScrollPane scrollPane;
	private ChartPanel graphPane;
	private XYDataset dataset2; 
	
	private JTable qTable;
	private JPanel p1;
	
	public GraphStatisticsPanel(P2PNetworkGraph g) {
		
		numberPeersOnline = 0;
		directed = true;
		currentGraph = g;
		queryFreq = new ArrayList<QueryFreq>();
		diameterTimeList = new ArrayList<DiameterTimeList>();
		diameterTimeList.add(new DiameterTimeList(0,0)); //initial value
		
		
		this.setLayout(new BorderLayout());
		
		p1 = new JPanel(new GridLayout(2,1));
		
		
		jtabbedPane = new JTabbedPane();
		jtabbedPane.addTab("Tab1", p1);
		jtabbedPane.addTab("Tab2", graphPane);
		jtabbedPane.addTab("Table", scrollPane);
		
		
		
		numberPeersOnlineLabel = new JLabel("Number of Peers online: "+numberPeersOnline);
		numberPeersOnlineLabel.setToolTipText("Number of Peers online.");
		
		
		queryLabel = new JLabel("no Queries have been made");
		queryLabel.setToolTipText("Queries made");

		
		diameterLabel = new JLabel("Diameter of the graph: "+diameter);
		diameterLabel.setToolTipText("Diameter of the graph");
		
		cSize = 1;
		cSizePerPeer = new JLabel("Average Component Size Per Peer: " + cSize);
		
		

	    
		
	    
		
		p1.add(diameterLabel);
		p1.add(numberPeersOnlineLabel);
		p1.add(cSizePerPeer);
		add(jtabbedPane);
		
	}
	
	/*
	 * Should only be called once before the visualisation begins. Builds the List of queryhits per
	 * query, with queryIds and the Time of the query. Also builds a Java JTable placed into a 
	 * scrollPane for more info.
	 * */
	public void setQueryList(ArrayList<LogEvent> queryList)
	{
		
		String[] columnNames = {"Time(ms)",
				"Query ID",
				"QueryHits",
				"Peers Reached"};
		
		//multiplier is used to counteract the rollover of queryId's
		//by multiplying 1000 times the multiplier and adding it to the queryId (queryIds rollover at 999)
		int multiplier = 0;
		
		//Iterate through the list of Queries/Queryhits in order to get the amount of queryhits per
		//individual query
		for(LogEvent evt: queryList)
		{
			int queryId = evt.getParam(3);
			if(evt.getType().equals("queryreachespeer"))
			{
				queryId = evt.getParam(2);
			}
			
			//if the queryId is about to rollover, increase the multiplier by 1
			
			
			//if we have an instance of a query, add it to the list of Queries
			if(evt.getType().equals("query"))
			{
				
				
					//first instance of each query 
					queryFreq.add(new QueryFreq( evt.getTime(),queryId+(1000*multiplier), 0, 0));
					if(queryId == 999)
					{
						multiplier++;
					}
			}
			else
			{
				int index =0;
				boolean found = false;
				//Current Event is a queryhit, so update the frequency of the appropriate query
				for(int i = multiplier; i >=0 ;i--)
				{
					if(queryFreq.contains(new QueryFreq(0, queryId+(1000*multiplier),0, 0))){
						index = queryFreq.indexOf(new QueryFreq(0, queryId+(1000*multiplier), 0, 0));
						found = true;
						break;
					}
				}
				if(found){
					QueryFreq temp = queryFreq.get(index);
					if(evt.getType().equals("queryhit"))
					{
						//System.out.println("queryhit - index: " + index + " - queryId: " + queryId + " - multiplier: " + multiplier );
						temp.setFrenquency(temp.getFrequency()+1);
					}
					else{
						temp.setPeersReached(temp.getPeersReached()+1);
					}
					
				}

			}
			
			
			
			
		}
		

		XYDataset dataset = createDataset(queryFreq);
		chart = createChart(dataset);
		graphPane = new ChartPanel(chart);
		jtabbedPane.setComponentAt(jtabbedPane.indexOfTab("Tab2"), graphPane);
		try {
			ChartUtilities.saveChartAsPNG(new File("C:\\eclipse\\workspace\\SpiderWeb\\graph.png"), chart, 800, 800);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		Object[][] data = new Object[queryFreq.size()][4];
		int i=0;
		for(QueryFreq evt : queryFreq) {
			data[i] = evt.toArray();
			i++;
		}
		
		qTable = new JTable(data, columnNames);
		scrollPane = new JScrollPane(qTable);
		scrollPane.setSize(getWidth(),getHeight());
		scrollPane.setWheelScrollingEnabled(true);
		qTable.setFillsViewportHeight(true);
		qTable.setEnabled(false);
		jtabbedPane.setComponentAt(jtabbedPane.indexOfTab("Table"), scrollPane);
		
	}
	
	/*
	 * Currently doesn't do much except keep track of the number of peers online.
	 * */
	public void processChange(StatisticEvent evt)
	{
		int event = evt.getEvent();
		switch(event) {
		case 1:
			numberPeersOnline++;
			numberPeersOnlineLabel.setText("Number of Peers online: "+numberPeersOnline);
			
			
			break;
		case 2:
			numberPeersOnline--;
			numberPeersOnlineLabel.setText("Number of Peers online: "+numberPeersOnline);
			
			
			break;
		default:
			break;
		}
		
	}
	
	/*
	 * After every connect of disconnect event, this method is called to calculate the diameter of
	 * the graph. If the event is being played forward, it also changes the plot
	 * */
	public void checkDiameter(long time, boolean forward)
	{
		if(directed){
			ArrayList<Double> a = new ArrayList<Double>();
			double diameter = 0;
			Distance<PeerVertex> d = new UnweightedShortestPath(currentGraph);
			ArrayList<PeerVertex> PeerVertices = new ArrayList<PeerVertex>();
			for (P2PVertex V: currentGraph.getVertices())
			{
				if(V instanceof PeerVertex)
				{
					PeerVertices.add((PeerVertex)V);
				}
			}
			for(PeerVertex v: PeerVertices)
			{
				double componentSize = 1;
				for(PeerVertex w: PeerVertices)
				{
					if(v.equals(w) == false)
					{
						Number dist = d.getDistance(v, w);
						if(dist != null)
						{
							componentSize++;
							diameter = Math.max(diameter, dist.doubleValue());
						}
					}
				}
				a.add(componentSize);
			}
			
			double total = 0;
			for(Double p: a)
			{
				total += p;
			}
			cSize = total/numberPeersOnline;
			cSizePerPeer.setText("Average Component Size Per Peer: "+cSize);
			
		
			diameterLabel.setText("Diameter of the graph: "+diameter);
			if(forward) {
				addDiameterData(diameterTimeList,time, diameter);
				XYStepRenderer step = new XYStepRenderer();
				step.setPaint(Color.BLUE);
				dataset2 = createDataset2(diameterTimeList);
				addStepPlot(dataset2, step);
				

				
				
				
			}
		}
		else{
			double diameter = 0;
			Distance<PeerVertex> d = new UnweightedShortestPath(currentGraph);
			ArrayList<PeerVertex> PeerVertices = new ArrayList<PeerVertex>();
			for (P2PVertex V: currentGraph.getVertices())
			{
				if(V instanceof PeerVertex)
				{
					PeerVertices.add((PeerVertex)V);
				}
			}
			
			for(int i = 0; i != PeerVertices.size(); i++){
				int indexOffset = 0;
				for(int j = indexOffset; j != PeerVertices.size();j++){
					if(PeerVertices.get(i).equals(PeerVertices.get(j)) == false){
						Number dist = d.getDistance(PeerVertices.get(i), PeerVertices.get(j));
						if(dist != null){
							diameter = Math.max(diameter,  dist.doubleValue());
						}
						indexOffset++;
					}
				}
			}
			diameterLabel.setText("Diameter of the graph: "+diameter);
			if(forward) {
				addDiameterData(diameterTimeList,time, diameter);
				XYStepRenderer step = new XYStepRenderer();
				step.setPaint(Color.BLUE);
				dataset2 = createDataset2(diameterTimeList);
				addStepPlot(dataset2, step);
			}
		}
		try {
			ChartUtilities.saveChartAsPNG(new File("C:\\eclipse\\workspace\\SpiderWeb\\graph.png"), chart, 800, 800);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Takes in as an input the list of QueryFreq, meaning a list with the amount of queryhits a
	 * query has received, when that query was issued and the Id of the query. This is list is then
	 * used to generate a XYDataset used to plot a scatterplot of the QueryHit Freq over time.
	 * */
	private static XYDataset createDataset(ArrayList<QueryFreq> queryFreq) {
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Queryhits");
		
		for(QueryFreq q: queryFreq)
		{
			series1.add((double)q.getTime()/1000, q.getFrequency());
		}
		dataset.addSeries(series1);
		return dataset;
		
	}
	
	
	/*
	 * Simple method which adds to the list the Time of the change in diameter and the new diameter
	 * */
	private void addDiameterData(ArrayList<DiameterTimeList> list, long time, double diameter) {
		
		list.add(new DiameterTimeList(time, diameter));
		
	}
	
	/*
	 * Taking in as an input the list of diameters and there changing times, generates an XYdataset
	 * that will be used to plot the step graph of the diameter over time.
	 */
	private static XYDataset createDataset2(ArrayList<DiameterTimeList> list) {
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Diameter");
		
		for(DiameterTimeList q: list)
		{
			series1.add((double)q.getTime()/1000, q.getDiameter());
		}
		dataset.addSeries(series1);
		return dataset;
		
	}
	
	/*
	 * Since the diameter of the network graph changes throughout the simulation, the step plot of
	 * the diameter needs to be redrawn whenever the diameter changes.
	 * */
	private void addStepPlot(XYDataset dataset, XYStepRenderer Step) {
		
		JFreeChart chart = graphPane.getChart();
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDataset(1,dataset);
		plot.setRenderer(1,Step);
		
	}
	
	
	/*
	 * Creates a scatterplot of the Query frequency over time using the provided dataset
	 * */
	private static JFreeChart createChart(XYDataset dataset) {
		
		JFreeChart chart = ChartFactory.createScatterPlot("Queryhit Frequency Graph"
				,"Time(s)"
				,"QueryHits"
				,dataset
				,PlotOrientation.VERTICAL,
				true,
				true,
				false
		);
		
		chart.setBackgroundPaint(Color.WHITE);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setShape(new Rectangle(-1,-1,2,2));  //makes the squares smaller
		renderer.setShapesVisible(true);
		renderer.setShapesFilled(true);
		
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		return chart;
		
	}

}
