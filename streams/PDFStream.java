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
import java.io.FilenameFilter;

import java.lang.reflect.*;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

/*
 * This class is used to obtain an InputStream from a PDF
 * style file.  This class relies on an external library,
 * "PDFBox" to function.  This library should be in the
 * "libs" folder in the Senf directory.  This library may
 * be named anything, as Senf uses reflection
 * (java.lang.reflect.*) to load the JAR file into the 
 * class path during runtime.
*/
public class PDFStream implements SenfStream
{
	private File f;

	public PDFStream( FileStream fs ) throws SenfStreamException
	{
		try
		{
			f = fs.getFile();
		}
		catch( Exception E )
		{
			throw new SenfStreamException( "Error creating PDF File Stream!" );
		}
	}

	public InputStream getInputStream() throws SenfStreamException
	{
		try
		{
			return new PDFInputStream( new FileInputStream( getFile() ) );
		}
		catch( Exception E )
		{
			throw new SenfStreamException( "Error getting reader in PDFStream!" );
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

	//This is a specialized InputStream for PDF Files.  This is the class that uses the external libraries
	private class PDFInputStream extends InputStream
	{
		private String text;
		private CharInputStream cis; 

		Method close;
		Object documentObj;
		Object[] closeArgs = {};

		public PDFInputStream( InputStream is ) throws IOException
		{
			try
			{	
				//This loads all the libraries (JAR Files) in the "libs" folder into the classpath in order to parse the PDF File.	
				File[] libs = new File( System.getProperty( "user.dir" ) + System.getProperty( "file.separator" ) + "libs" ).listFiles(
					//This limits it to only JAR Files
					new FilenameFilter()
					{
						public boolean accept( File dir, String name )
						{
							if( name.endsWith( "jar" ) )
							{
								return true;
							}
							return false;
						}
					}
				);

				//Creates an empty URL[] object for the FileLoader.  This is necessary because FileLoader extends URLClassLoader
				URL[] urls = {};
				FileLoader fl = new FileLoader( urls );

				//This loads the JAR Files from earlier into the Java ClassPath
				for( int i=0; i<libs.length; i++ )
				{
					fl.addJarFile( libs[i].getPath() );
				}

				//Create "Class" Objects for the classes needed to parse PDF files.  PDDocument, and PDFTextStripper
				Class documentClass = fl.loadClass( "org.pdfbox.pdmodel.PDDocument" );
				Class stripperClass = fl.loadClass( "org.pdfbox.util.PDFTextStripper" );
				//Empty "Class" Object that needs to be created in order to load the "close" method which takes no Arguments
				Class[] v = {};

				//Create default value Object instances of the "Class" objects loaded earlier.
				documentObj = documentClass.newInstance();
				Object stripperObj = stripperClass.newInstance();

				//Load the methods that will be executed from each of the "Class" Objects created earlier.
				Method getText = stripperClass.getMethod( "getText", documentClass );
				Method load = documentClass.getMethod( "load", Class.forName( "java.io.InputStream" ) );
				close = documentClass.getMethod( "close", v  );

				//Set the Arguments for each Method into an Object[]
				Object[] loadArgs = { is };
				Object[] getTextArgs = { load.invoke( documentObj, loadArgs ) };

				//Set the value of the string "text" to the value returned by the method "getText", cast as a String
				text = (String)getText.invoke( stripperObj, getTextArgs ); 
			}
			catch( InvocationTargetException ite ){}
			catch( Exception E )
			{
				System.out.println( E );
				throw new IOException();				
			}
			finally
			{
				try
				{
					//Close the PDF Document
					close.invoke( documentObj, closeArgs );
				}
				catch( Exception E )
				{
					System.out.println( "Error Closing PDFStream!" );
				}
			}

			//Create a CharInputStream to return to Senf
			char[] array = text.toCharArray();
			cis = new CharInputStream( array );
		}
	
		public int available() throws IOException
		{
			return cis.available();
		}
	
		public void close() throws IOException 
		{
			try
			{
				close.invoke( documentObj, closeArgs );
			}
			catch( Exception E )
			{
				System.out.println( "Error closing PDF Stream!" );
			}
		}
	
		public void mark( int readlimit )
		{
			cis.mark( readlimit );		
		}
	
		public boolean markSupported()
		{
			return cis.markSupported();
		}
	
		public int read() throws IOException
		{
			return cis.read();
		}
	
		public int read( byte[] b ) throws IOException
		{
			return cis.read( b );
		}
	
		public int read( byte[] b, int off, int len ) throws IOException
		{
			return cis.read( b, off, len );
		}
	
		public void reset() throws IOException
		{
			cis.reset();
		}
	
		public long skip( long N ) throws IOException
		{
			return cis.skip( N );
		}
	}
}
