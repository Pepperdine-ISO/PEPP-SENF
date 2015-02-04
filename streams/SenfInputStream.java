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
import java.io.InputStreamReader;
import java.io.InputStream;

import java.net.URI;

//This class is used to create a SenfStream from an InputStream
public class SenfInputStream implements SenfStream
{
	private InputStream is;
	private String path, name;

	public SenfInputStream( InputStream is )
	{
		this( is, "no_path", "no_name" );
	}

	public SenfInputStream( InputStream is, String name )
	{
		this( is, "", name );
	}

	public SenfInputStream( InputStream is, String path, String name )
	{
		this.is = is;
		this.path = path;
		this.name = name;
	}

	public InputStream getInputStream()
	{
		return is;
	}

	public String getName()
	{
		return name;
	}

	public long size()
	{
		try
		{
			return is.available();
		}
		catch( Exception E )
		{
			return -1;
		}
	}

	public URI getURI()
	{
		try {
			return new URI( path + System.getProperty( "file.separator" ) + name );
		} catch( java.net.URISyntaxException use ) { };
		return null;
	}

	public String getPath()
	{
		return path;
	}

	public boolean shouldScan( SenfOptions opts )
	{
		if( path != "no_path" && name != "no_name" )
		{
			try
			{
				if( is.available() < opts.MIN_MATCH_LENGTH * opts.minMatches )
					return false;
				if( opts.maxFileLen > 0 && is.available() > opts.maxFileLen )
					return false;
				if( !opts.senfACL.accept(path) && !opts.senfACL.accept(name) )
					return false;
			}
			catch( Exception E )
			{
				//error stuffs
				return false;
			}
			return true;
		}
		else
		{
			return true;
		}
	}
}
