/*
 *
 * Senf was created by the Information Security Office
 * at the Univeristy of Texas at Austin.
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 United States
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 *
 * Send comments to security@utexas.edu
 *
*/

//Package
package senf;

//Import Java Packages
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

/*
 * This class is the parsing engine.  It stores all the parsers in a HashMap
 * based on the extensions that the parser returns, and then returns the 
 * proper parser if one exists when asked to parse an object into a more
 * specific SenfObject.
*/
public class SenfParserManager
{
	private HashMap< String, SenfParser > parserMap;

	public SenfParserManager()
	{
		parserMap = new HashMap< String, SenfParser >();

		loadParsers();
	}

	private void addParser( String extension, SenfParser parser )
	{
		parserMap.put( extension.toLowerCase(), parser );
	}

	private void loadParsers()
	{
		File parserDIR = new File( "parsers" );

		if( !parserDIR.isDirectory() )
		{
			System.out.println( "Parser Directory does not exist, not loading Parsers." );
			return;
		}

		File[] parsers = parserDIR.listFiles
		(
			new FilenameFilter()
			{
				public boolean accept( File dir, String name )
				{
					if( name.endsWith( "class" ) || name.endsWith( "jar" ) )
					{
						return true;
					}

					return false;
				}
			}
		);

		int count = 0;

		for( int i = 0; i< parsers.length; i++ )
		{
			try
			{
				FileLoader parserloader = null;
				String filename = parsers[i].getName();
				int in;

				if( (in = filename.indexOf(".class")) > 0 )
				{
					parserloader = new FileLoader( "." );
				}
				else if( (in = filename.indexOf(".jar")) > 0 )
				{
					parserloader = new FileLoader( "parsers" + File.separator + parsers[i].getName() );
				}

				String classname = filename.substring( 0, in );
				SenfParser parser  = (SenfParser)parserloader.findClass( "parsers." + classname ).newInstance();
				String[] extensions = parser.getExtensions();
				
				for( int x=0; x<extensions.length; x++ )
					addParser( extensions[x], parser );
					
				count++;
			}
			catch( Exception e )
			{
				System.out.println( "Error Loading Parsers: " + e );
				e.printStackTrace();
			}
		}

		if( count > 0 )
			System.out.println( "Successfully loaded " + count + " parsers." );
		else
			System.out.println( "No Parsers loaded" );
	}

	public SenfObject parse( SenfObject ss )
	{
		String name = null;

		try {
			name = ss.getName();
		} catch( SenfObjectException soe ) {
			return ss;
		}

		int dotPos = name.lastIndexOf( '.' ) + 1;

		if( dotPos >= 0 && dotPos < name.length() )
		{
			String extension = name.substring( dotPos, name.length() ).toLowerCase();

			if( parserMap.containsKey( extension ) )
				return parserMap.get( extension ).parse( ss );
		}

		return ss;
	}

	public boolean hasParser( String extension )
	{
		if( parserMap.containsKey( extension ) )
			return true;
		else
			return false;
	}
}
