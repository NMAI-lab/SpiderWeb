package spiderweb;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

public class P2PVertexStrokeTransformer implements
		Transformer<P2PVertex, Stroke> {

	@Override
	public Stroke transform(P2PVertex v) {
		if(v.getQueryState()==P2PVertex.GETQUERY)
			return new BasicStroke(2.5f); //make the stroke twice as wide if the vertex is getting a query
		else
			return new BasicStroke(1.0f); // if it's to a document, make it narrow
	}

}
