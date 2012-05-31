package spiderweb.visualizer;

public class StatisticEvent {

	
	//TODO: make the GraphStatisticalPane change dynamically
	private int event;
	private int query;
	private int msgId;
	
	public StatisticEvent(int event)
	{
		this.event = event;
		this.query = 0;
		this.msgId = 0;
		
	}
	
	public StatisticEvent(int event, int query)
	{
		this.event = event;
		this.query = query;
		this.msgId = 0;
	}
	
	public StatisticEvent(int event, int query, int msgId)
	{
		this.event = event;
		this.query = query;
		this.msgId = msgId;
	}
	
	public int getEvent()
	{
		return event;
	}
	
	public int getQuery()
	{
		return query;
	}
	
	public int getMsgId()
	{
		return msgId;
	}
	
}
