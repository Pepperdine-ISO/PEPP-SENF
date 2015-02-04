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
package streams;

//Import Senf Packages
import senf.*;

//Import Java Packages
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.FileInputStream;

import java.net.URI;

/*
 * This class is the "default" Stream class for "files".
 * Most files will be a FileStream at one point before 
 * being run through the SenfParserManager, which will
 * then, if necessary, parse them into a more specific
 * Stream or StreamSource.
*/
public class FileStream implements SenfStream
{
	private File f;

	public FileStream( String path ) throws SenfStreamException
	{
		this( new File( path ) );
	}

	public FileStream( File file ) throws SenfStreamException
	{
		f = file;	
	}

	public InputStream getInputStream() throws SenfStreamException
	{
		try 
		{
			return new FileInputStream( f );
		} 
		catch( FileNotFoundException fnfe ) 
		{
			throw new SenfStreamException( "Error getting reader in FileStream!" );
		}
		catch( IOException ioe )
		{
			throw new SenfStreamException( "Error getting reader in FileStream!" );
		}
	}



	public File getFile()
	{
		return f;
	}

	public String getName()
	{
		return f.getName();
	}

	public long size()
	{
		return f.length();
	}

	public URI getURI()
	{
		return f.toURI();
	}

	public boolean shouldScan( SenfOptions opts ) throws SenfStreamException
	{
		if( !(f.canRead()) )
			return false;
		if( f.length() < opts.minMatches * SenfOptions.MIN_MATCH_LENGTH )
			return false;
		if( opts.maxFileLen > 0 && f.length() > opts.maxFileLen )
			return false;
		if( opts.checkModified && f.lastModified() < opts.lastModified )
			return false;

		try
		{
			if( !opts.senfACL.accept( f.getCanonicalPath() ) )
				return false;
			if( !f.canRead() )
				return false;
		}
		catch( Exception E )
		{
			throw new SenfStreamException( "Error Checking File!" );
		}
		return true;
	}
}
