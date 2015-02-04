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

import streamsources.XLSXStreamSource;

import streams.FileStream;

//This Class parses *.xlsx SenfObjets into XLSXStreamSources
public class XLSXParser implements SenfParser
{
	//Parse the Object
	public SenfObject parse( SenfObject so )
	{
		try 
		{
			System.out.println( "parser ugh");
			return new XLSXStreamSource( ( FileStream )so );
		} catch( SenfObjectException soe ) {}

		return so;
	}

	//Return the extensions this parser handles
	public String[] getExtensions()
	{
		String[] result = { "xlsx" };
		return result;
	}
}
