
/*
 * File:         EdgePredicate.java
 * Project:		 Spiderweb Network Graph Visualizer
 * Created:      23/05/2012 
 * Last Changed: Date: 23/05/2012 
 * Author:       Matthew Smith
 * 				 Denis Dionne
 * 
 * This code was produced at Carleton University 2012
 */
package spiderweb.visualizer.predicates;

import org.apache.commons.collections15.Predicate;

import spiderweb.graph.P2PConnection;
import spiderweb.graph.P2PVertex;
import spiderweb.graph.ReferencedNetworkGraph;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;

/**
 * The Edge Predicate checks to make sure the p2p connections are present in both the dynamic and full graph
 * and will return whether or not the edge should be drawn.
 * 
 * @author <A HREF="mailto:smith_matthew@live.com">Matthew Smith</A>
 * @author Denis Dionne
 * @version Date: 23/05/2012 
 */
public class EdgePredicate implements Predicate<Context<Graph<P2PVertex, P2PConnection>, P2PConnection>> {

	protected ReferencedNetworkGraph graph;
	protected Class<? extends P2PConnection> exclude;
	
	/**
	 * The Options Predicate will listen to anything that handles PredicateChangeEvents and show specified 
	 * vertices depending on what options are passed into it and providing the vertices are in both the reference
	 * graph and the dynamic graph.
	 * @param graph The referenced graph which this predicate is operating on
	 */
	public EdgePredicate(ReferencedNetworkGraph graph) {
		this.graph = graph;
		exclude = null;
	}
	
	/**
	 * Set the Type of vertex to exclude from drawing.
	 * @param exclude The type of vertex class to exclude from drawing; required to extend <code>P2PVertex.class</code>
	 */
	public void setExcludeClass(Class<? extends P2PConnection> exclude) {
		this.exclude = exclude;
	}
	
	
	@Override
	public boolean evaluate(Context<Graph<P2PVertex, P2PConnection>, P2PConnection> context) {
		
		if(!graph.getDynamicGraph().containsEdge(context.element)) { 
			return false; //the dynamic graph doesn't contain the vertex to show
		}
		return true;
	}

}
