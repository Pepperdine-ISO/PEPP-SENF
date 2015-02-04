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
import senf.SenfObject;
import senf.SenfObjectException;
import senf.SenfParser;

import streamsources.PPTXStreamSource;

import streams.FileStream;

//This class parses *.pptx SenfObjects into PPTXStreamSources
public class PPTXParser implements SenfParser
{
	//Parse the Object
	public SenfObject parse( SenfObject so )
	{
		try 
		{
			return new PPTXStreamSource( ( FileStream )so );
		} catch( SenfObjectException soe ) {}

		return so;
	}

	//Return extensions this Parser can handle
	public String[] getExtensions()
	{
		String[] result = { "pptx" };
		return result;
	}
}
