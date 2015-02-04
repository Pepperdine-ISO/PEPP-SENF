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

import streams.FileStream;
import streams.PDFStream;

//This class parses *.pdf SenfObjects into PDFStreams
public class PDFParser implements SenfParser
{
	//Parse the Object
	public SenfObject parse( SenfObject so )
	{
		try
		{
			return new PDFStream( (FileStream)so );
		}
		catch( Exception E ) {}

		return so;
	}

	//This returns the filetypes this parser can handle
	public String[] getExtensions()
	{
		String[] result = { "pdf", "fdf" };
		return result;
	}
}
