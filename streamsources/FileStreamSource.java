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
import streams.FileStream;

//Import Java Packages
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URI;


//This class is used as the default "container" for streams
public class FileStreamSource implements JunkStreamSource
{
	private class ActRec
	{
		public File file;
		public int i;

		public ActRec( File file )
		{
			i = 0;
			this.file = file;
		}
	}

	private File root, curFile;
	private File[] fl;
	private int curFileIndex;
	private boolean advanced;
    private boolean junk;


	public FileStreamSource( String path ) throws SenfStreamSourceException
	{
		this( new File( path ) );
		
	}

	public FileStreamSource( File file ) throws SenfStreamSourceException
	{
		root = file;
		curFile = null;
		curFileIndex = -1;
		advanced = false;

		try {
			if( root.isDirectory() )
				root = root.getCanonicalFile();
			else if( root.isFile() )
				root = root.getParentFile().getCanonicalFile();
			else
				throw new IOException( "Invalid file/folder" );

			fl = root.listFiles();
			junk = (fl == null);
		} catch( FileNotFoundException fnfe ) {
			throw new SenfStreamSourceException( "Root dir not found!" );
		} catch( IOException ioe ) {
			throw new SenfStreamSourceException( "Error getting root dir" );
		}
	}

	public boolean hasNext() throws SenfStreamSourceException
	{
		if( advanced )
			return true;

		if(junk)
			return false;

		try {
            while( ++curFileIndex < fl.length )
            {
                curFile = fl[ curFileIndex ];

                if( ( curFile.isDirectory() && curFile.getAbsolutePath().equals( curFile.getCanonicalPath() ) ) || curFile.isFile() )
                {
                    advanced = true;
                    return true;
                }
            }
            return false;
		} catch( FileNotFoundException fnfe ) {
			throw new SenfStreamSourceException( "File not found in FileStreamSource" );
		} catch( IOException ioe ) {
			throw new SenfStreamSourceException( "Error reading file in FileStreamSource" );
		}
	}

	public SenfObject next() throws SenfStreamSourceException
	{
		if( !advanced )
			throw new SenfStreamSourceException( "Need to call hasNext() first in FileStreamSource!" );

		advanced = false;

		try {
			if( curFile.isFile() )
				return new FileStream( curFile );
			else
				return new FileStreamSource( curFile );
		} catch( SenfStreamException sse ) {
			throw new SenfStreamSourceException( "Error opening file" );
		}
	}

	public String getName() throws SenfStreamSourceException
	{
		try {
			return root.getCanonicalPath();
		} catch( IOException ioe ) {
			throw new SenfStreamSourceException( "Couldn't get name!" );
		}
	}

	public URI getURI() 
	{
		return root.toURI();
	}

    public boolean isJunk()
    {
        return junk;
    }

    public boolean containsJunk()
    {
        return true;
    }
}
