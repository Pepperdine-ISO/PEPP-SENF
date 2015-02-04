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

package seeds;

import senf.RingBuffer;
import senf.Seed;
import senf.SenfResult;
import java.util.regex.*;

public class DOBSeed implements Seed
{
	RingBuffer ring;
	private static final char EOFCHAR = (char)0x00;
	int sofar;
	int dobs;
	int dobns;
	int matches;
	long offset;
	boolean eof;
	private static final Pattern DOB_PATTERN = Pattern.compile( "[0-9]{2}([-\\. \\|\\\\\\/])[0-9]{2}\\1[0-9]{4}" ) ;

	private static final int LENIENT_MASK_DOB_SEPARATORS = 0x6de;
	private static final int LENIENT_MASK_DOB_NO_SEPARATORS = 0x1fe;
	private static final int LENIENT_MASK_DOB_SEPARATORS_TRUNC = 0xfff;
	private static final int LENIENT_MASK_DOB_NO_SEPARATORS_TRUNC = 0x3ff;

	public DOBSeed(){}

	private boolean dobValid( int day, int month, int year )
	{
		// Pepperdine Infosec - No match for DOB needed
		/*
		if( (month == 2) && (day<29) )
			return true;
		else if( (month==1 || month==3 || month==5 || month==7 || month==8 || month==10 || month==12 ) && (day<32) )
			return true;
		else if( (month==4 || month==6 || month==9 || month==11 ) && (day<31) )
			return true;
		else
			return false;
		*/
		return false;
	}

	public void init() {}

	public void reset()
	{
		ring = new RingBuffer();
		sofar = 0;
		dobs = 0;
		dobns = 0;
		matches = 0;
		eof = false;
		offset = 0;
	}

	public int match( int c, SenfResult results )
	{
		matches = 0;
		eof = ( c == -1 );
		sofar <<= 1;
		++offset;
		if( eof )
			ring.push( EOFCHAR );
		else
		{
			ring.push( (char)c );
			if( Character.isDigit( (char)c ) )
			{
				sofar |= 1;
				return 0;
			}
		}

		//remove stuff we don't care about
		dobs = sofar & LENIENT_MASK_DOB_SEPARATORS_TRUNC;
		dobns = sofar & LENIENT_MASK_DOB_NO_SEPARATORS_TRUNC;
		//check for DOBs
		if( dobs == LENIENT_MASK_DOB_SEPARATORS )
		{
			String posdob = ring.read( 11 );
			int month = Integer.parseInt( posdob.substring( 0, 2 ) );
			int day = Integer.parseInt( posdob.substring( 3, 5 ) );
			int year = Integer.parseInt( posdob.substring( 6, 10 ) );
			if( dobValid( day, month, year ) )
			{
				if( DOB_PATTERN.matcher( posdob ).find() )
				{
					++matches;
					results.addResult( offset - 11, 10 );
					sofar = 0;
					return matches;
				}
			}
		}
		if( dobns == LENIENT_MASK_DOB_NO_SEPARATORS )
		{
			String posdob = ring.read( 9 );
			int month = Integer.parseInt( posdob.substring( 0, 2 ) );
			int day = Integer.parseInt( posdob.substring( 2, 4 ) );
			int year = Integer.parseInt( posdob.substring( 4, 8 ) );
			if( dobValid(day, month, year) || dobValid(month, day, year) )
			{
				String temp = posdob.substring(0,2) + "/" + posdob.substring(2,4) + "/" + posdob.substring(4,8);
				if( DOB_PATTERN.matcher( temp ).find() )
				{
					++matches;
					results.addResult( offset - 9, 8 );
					sofar = 0;
					return matches;
				}
			}
		}

		return matches;
	}
}
