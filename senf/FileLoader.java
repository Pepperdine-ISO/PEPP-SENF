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

package senf;

import java.io.IOException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

//This class is used to add files to the Java Class Path
public class FileLoader extends URLClassLoader
{
	public FileLoader( URL[] urls )
	{
		super( urls );
	}

	public FileLoader( String path ) throws MalformedURLException
	{
		super( new URL[] { new File( path ).toURI().toURL() } );
	}

	public Class findClass( String name )
	{
		try
		{
			return super.findClass( name );
		}
		catch( ClassNotFoundException ce )
		{
			return null;
		}
	}

	public void addJarFile( String path ) throws MalformedURLException
	{
		String urlPath = "jar:file://" + path + "!/";
		addURL( new URL( urlPath ) );
	}

	public void addFile( String path ) throws MalformedURLException
	{
		String urlPath = "file://" + path + "!/";
		addURL( new URL( urlPath ) );
	}
}
