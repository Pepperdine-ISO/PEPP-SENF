package streamsources;

import senf.*;
import streams.*;

import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import java.io.File;
import java.io.IOException;
import java.net.URI;


public class ZipStreamSource implements SenfStreamSource
{
	File f;
	ZipFile z;
	ZipEntry ze;
	Enumeration<? extends ZipEntry > e;
	boolean advanced;

	//TODO: Depreciate
	public ZipStreamSource( FileStream fs ) throws SenfStreamSourceException
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
	public ZipStreamSource( File file ) throws SenfStreamSourceException
	{
		try
		{
			f = file;
			z = new ZipFile( file );
			e = z.entries();
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
			throw new SenfStreamSourceException( "Error opening zip file!" );
		}

		advanced = false;
	}
	
	public boolean hasNext() throws SenfStreamSourceException
	{
		while( e.hasMoreElements() )
		{
			ze = e.nextElement();
			if( !ze.isDirectory() )
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
