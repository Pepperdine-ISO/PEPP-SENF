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

/*
 * SenfFileFilter.java
 *
 * Created on July 7, 2008
 *
 */

/*
 *@author Alek Amrani
 */
package senf;

import java.io.File;

public class SenfFileFilter extends javax.swing.filechooser.FileFilter
{
	private String fileExtension;
	private String fileDescription;

	public SenfFileFilter( String extension, String description )
	{
		fileExtension = extension;
		fileDescription = description;
	}

	public boolean accept( File f )
	{
		if( f.isDirectory() )
			return true;

		String extension = getExtension( f );
		if( extension.equals( fileExtension ) )
			return true;
		return false;
	}

	public String getDescription()
	{
		return fileDescription;
	}

	private String getExtension( File f )
	{
		String name = f.getName();
		int i = name.lastIndexOf('.');

		if( i > 0 && i < name.length() - 1 )
			return name.substring( i+1 ).toLowerCase();
		return "";
	}
}
