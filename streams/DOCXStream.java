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

import streams.FileStream;

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

import java.util.zip.*;

import java.net.URI;

/*
 * This class is used to obtain an InputStream from *.docx files. *.DOCX 
 * files are nifty in the fact that they are pretty much just *.zip files 
 * that contain some folders and *.xml files made by Microsoft.  So we
 * can treat them just like *.zip files in order to read their contents.
 * Furthermore, the contents of *.docx files are contained in the file 
 * named "document.xml" within the directory "word".  Cool, huh?
*/
public class DOCXStream implements CompressedSenfStream
{
	private File f;

	public DOCXStream( FileStream fs ) throws SenfStreamException
	{
		try
		{
			f = fs.getFile();
		}
		catch( Exception E )
		{
			throw new SenfStreamException( "Error creating DOCX File Stream!" );
		}
	}

	//Gets an InputStream from the file "document.xml" within the directory "word"
	public InputStream getInputStream() throws SenfStreamException
	{
		try
		{
			ZipFile zf = new ZipFile( f );
			ZipEntry ze = zf.getEntry( "word/document.xml" );
			return zf.getInputStream( ze );
		}
		catch( Exception E )
		{
			throw new SenfStreamException( "Error getting reader in DOCXStream!" );
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
