package senf;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

public class SenfACL implements FilenameFilter
{
	private enum SenfACLResult { NO_MATCH, MATCH_ALLOW, MATCH_DENY }

	private static abstract class SenfACLNode {

		protected boolean allow;
		
		SenfACLNode( boolean allow )
		{
			this.allow = allow;
		}

		protected abstract boolean match( String name );

		public SenfACLResult allow( String name )
		{
			if( match( name ) )
				return ( allow ? SenfACLResult.MATCH_ALLOW : SenfACLResult.MATCH_DENY );

			return SenfACLResult.NO_MATCH;
		}
	}

	private static class SenfACLRegexNode extends SenfACLNode
	{
		private Pattern p;

		public SenfACLRegexNode( String regex, boolean allow )
		{
			super( allow );
			p = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
		}

		protected boolean match( String name )
		{
			return p.matcher( name ).find();
		}
	}

	private static class SenfACLTextNode extends SenfACLNode
	{
		private String s;

		public SenfACLTextNode( String s, boolean allow )
		{
			super( allow );
			this.s = s;
		}

		protected boolean match( String text )
		{
			return s.equals( text );
		}
	}

	private LinkedList< SenfACLNode > senfACL;
	private String filename;

	public SenfACL( String senfACLFile )
	{
		filename = senfACLFile;
		senfACL = loadFromFile( senfACLFile );
	}

	private LinkedList< SenfACLNode > loadFromFile( String filename )
	{
		LinkedList< SenfACLNode > sa = new LinkedList< SenfACLNode >();

		try {
			BufferedReader br = new BufferedReader( new FileReader( filename ) );

			String line;

			while( ( line = br.readLine() ) != null ) {
				SenfACLNode node = parseLine( line );

				if( node != null )
					sa.addLast( node );
				else
					System.out.println( "Error parsing senf ACL line: " + line );
			}

		} catch ( java.io.FileNotFoundException fnfe ) {
			System.out.println( "ACL not found!" );
		} catch ( java.io.IOException ioe ) {
			System.out.println( "Error reading ACL file!" );
		}
		
		return sa;
	}

	public SenfACLNode parseLine( String line )
	{
		String[] parts = line.trim().split( "\\s+", 3 );

		if( parts.length != 3 )
			return null;
		
		parts[ 0 ] = parts[ 0 ].trim().toUpperCase();
		parts[ 1 ] = parts[ 1 ].trim().toUpperCase();
		parts[ 2 ] = parts[ 2 ].trim().replaceAll( "(^|[^\\\\])\\\\([^\\\\]|$)", "$1\\\\\\\\$2" );

		boolean allow = false;

		if( parts[ 0 ].equals( "ALLOW" ) ) {
			allow = true;
		} else if( parts[ 0 ].equals( "DENY" ) ) {
			allow = false;
		} else {
			return null;
		}

		if( parts[ 1 ].equals( "BEGINSWITH" ) ) {
			return new SenfACLRegexNode( "^" + parts[ 2 ], allow );
		} else if( parts[ 1 ].equals( "ENDSWITH" ) ) {
			return new SenfACLRegexNode( parts[ 2 ] + "$", allow );
		} else if( parts[ 1 ].equals( "CONTAINS" ) ) {
			return new SenfACLRegexNode( parts[ 2 ], allow );
		} else if( parts[ 1 ].equals( "EXACTLY" ) ) {
			return new SenfACLRegexNode( "^" + parts[ 2 ] + "$", allow );
		} else if( parts[ 1 ].equals( "REGEX" ) ) {
			return new SenfACLRegexNode( parts[ 2 ], allow );
		}
	
		return null;
	}

	public String getFilename() 
	{
		return filename;
	}

	public boolean accept( File dir, String name )
	{
		Iterator< SenfACLNode > i = senfACL.iterator();

		while( i.hasNext() ) {
			switch( i.next().allow( name ) )
			{
				case NO_MATCH:
					break;
				case MATCH_ALLOW:
					return true;
				case MATCH_DENY:
					return false;
			}
		}

		// Implicit ALLOW ALL, BABY
		return true;
	}

	public boolean accept( String name )
	{
		return accept( null, name );
	}
}
