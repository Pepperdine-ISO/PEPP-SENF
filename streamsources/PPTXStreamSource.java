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
package streamsources;

//Import Senf Packages
import senf.*;
import streams.*;

//Import Java Packages
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/*
 * This class is used to scan *.pptx Files.  *.pptx files are similar to *.docx files
 * in that they are basicly *.zip files, except that *.pptx files have one *.xml file for
 * each slide, unlike *.docx files which have one *.xml file for the entire document.  This
 * means that we treat it as a streamsource, rather than a stream, because it is a conainer
 * for an unknown amount of *.xml fles.  These flies are named slide<x>.xml within the 
 * directory "ppt/slides", where the <x> is the number of the slide in the slide show.
*/
public class PPTXStreamSource implements SenfStreamSource
{
	File f;
	ZipFile z;
	ZipEntry ze;
	Enumeration<? extends ZipEntry > e;
	boolean advanced;

	public PPTXStreamSource( FileStream fs ) throws SenfStreamSourceException
	{
		try {
			f = fs.getFile();
			z = new ZipFile( f );
			e = z.entries();

		} catch( IOException ioe ) {
			throw new SenfStreamSourceException( "Error opening zip file!" );
		}

		advanced = false;
	}
	
	public boolean hasNext() throws SenfStreamSourceException
	{
		while( e.hasMoreElements() )
		{
			ze = e.nextElement();
			if( (!ze.isDirectory()) && (ze.getName().startsWith( "ppt/slides")) && (ze.getName().endsWith( "xml" )) )
			{
				advanced = true;
				return true;
			}
		}
		return false;
	}

	public SenfObject next() throws SenfStreamSourceException
	{
		if( !advanced )
			throw new SenfStreamSourceException( "Need to call hasNext() first in ZipStreamSource!" );

		advanced = false;

		try {
			return new ZipStream( z.getInputStream( ze ), f.getCanonicalPath(), ze.getName() );
		} catch( IOException ioe  ) {
			throw new SenfStreamSourceException( "Error getting input stream!" );
		}
	}

	public String getName() throws SenfStreamSourceException
	{
		return f.getName();
	}

	public URI getURI() throws SenfStreamSourceException
	{
		return f.toURI();
	}

    public boolean containsJunk()
    {
        return false;
    }
}
