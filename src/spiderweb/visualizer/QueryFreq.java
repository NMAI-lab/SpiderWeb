package spiderweb.visualizer;

public class QueryFreq {

	private int queryId;
	private int frequency;
	private long time;
	private int peersReached;
	
	public QueryFreq(long time, int queryId, int frequency, int peersReached)
	{
		this.queryId = queryId;
		this.frequency = frequency;
		this.time = time;
		this.peersReached = peersReached;
	}
	
	public int getQueryId()
	{
		return queryId;
	}
	
	public int getFrequency()
	{
		return frequency;
	}
	public long getTime()
	{
		return time;
	}
	
	public int getPeersReached()
	{
		return peersReached;
	}
	
	public void setPeersReached(int peersReached)
	{
		this.peersReached = peersReached;
	}
	
	public void setFrenquency(int frequency)
	{
		this.frequency = frequency;
	}
	
	@Override
	public boolean equals(Object q)
	{
		if(!(q instanceof QueryFreq))
		{
			return false;
		}
		else if(this.equal((QueryFreq)q)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean equal(QueryFreq q)
	{
		if(this.getQueryId() == q.getQueryId())
		{
			return true;
		}
		else{
			return false;
		}
	}
	
	public Object[] toArray() {
		Object[] array = { new Long(time), new Integer(queryId), new Integer(frequency), new Integer(peersReached)};
		return array;
	}
}
