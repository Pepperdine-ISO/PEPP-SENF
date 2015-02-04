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

import streams.DOCXStream;
import streams.FileStream;

//This Class parses *.docx SenfObjects into DOCXStreams
public class DOCXParser implements SenfParser
{
	//Parse and Object
	public SenfObject parse( SenfObject so )
	{
		try 
		{
			return new DOCXStream( ( FileStream )so );
		} 
		catch( SenfObjectException soe ) {}

		return so;
	}

	//This returns the extensions that this parser handles
	public String[] getExtensions()
	{
		String[] result = { "docx" };
		return result;
	}
}
