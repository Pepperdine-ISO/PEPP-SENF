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
package parsers;

//Import from Senf Packages
import senf.FileLoader;
import senf.SenfObject;
import senf.SenfObjectException;
import senf.SenfParser;

import streams.FileStream;

import streamsources.ZipStreamSource;

//This class parses compressed files into ZipStreamSources
public class ZipParser implements SenfParser
{
	//Parse the Object
	public SenfObject parse( SenfObject so )
	{
		try
		{	
			return new ZipStreamSource( (FileStream)so );
		}
		catch( Exception E ) {}

		return so;
	}

	//Return the extensions this parser handles
	public String[] getExtensions()
	{
		String[] result = { "zip", "jar" };
		return result;
	}
}
