package senf;

public class SenfResult
{
	private SenfStream ss;
	private String container;
	public result_entry[] matches;
	private int numresults;
	private int num_matches;

	public static class result_entry
	{
		public long offset;
		public int length;
		public result_entry(long offset, int length)
		{
			this.offset = offset;
			this.length = length;
		}
	}

	public SenfResult(int num_matches)
	{
		this.num_matches = num_matches;
	}

	public SenfResult(SenfResult result)
	{
		this.ss = result.ss;
		this.numresults = result.numresults;
		this.matches = result.matches;
		this.num_matches = result.num_matches;
	}

	public void reset( SenfStream ss )
	{
		this.ss = ss;
		this.container = null;
		numresults = 0;
		matches = new result_entry[num_matches];
	}

	public void setContainer(String name)
	{
		this.container = name;
	}

	public void addResult(long offset, int length)
	{
		matches[numresults++] = new result_entry(offset, length);
	}

	public result_entry[] getResults()
	{
		return matches;
	}

	public int getNumResults()
	{
		return numresults;
	}

	public String toString()
	{
		try
		{
			return ss.getURI().toString();
		}
		catch( Exception e )
		{}
			return "";
	}

	public String getName()
	{
		try
		{
			return ss.getName();
		}
		catch( Exception e )
		{
			System.out.println( "Error getting SenfStream Name!" );
		}
		return "";
	}

	public SenfStream getSS()
	{
		return ss;
	}
}
