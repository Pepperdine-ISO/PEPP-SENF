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
 * SSNSeed.java
 *
 * Created on March 19, 2007, 3:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package seeds;

import senf.RingBuffer;
import senf.Seed;
import senf.SenfResult;
import java.util.regex.*;
/**
 *
 * @author Sean Reid
 */
public class SSNSeed implements Seed {
    RingBuffer ring;
    private static final char EOFCHAR = (char)0x00;
    int sofar;
    int ssns;
    int ssnns;
    int matches;
    long offset;
    boolean eof;
    int lastp; // position of the recent pepperdine control characters
    private static final Character peppchar = new Character( Character.toUpperCase( 'P' ) ); // UPPERCASE pepperdine control character
    private static final Pattern SSN_PATTERN = Pattern.compile( "[0-9]{3}([-\\. \\|])[0-9]{2}\\1[0-9]{4}" );

    private static final int LENIENT_MASK_SSN_SEPARATORS = 0xede;
    private static final int LENIENT_MASK_SSN_NO_SEPARATORS = 0x3fe;
    private static final int LENIENT_MASK_SSN_SEPARATORS_TRUNC = 0x1fff;
    private static final int LENIENT_MASK_SSN_NO_SEPARATORS_TRUNC = 0x7ff;
    // additional masks for detecting pepperdine control character
    private static final int CONTROL_MASK_SSN_SEPARATORS = 0x1000;
    private static final int CONTROL_MASK_SSN_NO_SEPARATORS = 0x400;

    public SSNSeed(){}
     
     private boolean ssnAreaValid( int area ) {
        // See http://www.ssa.gov/employer/ssnvhighgroup.htm if you dare
        if( area == 0 )
            return false;
        if (area == 100)
            return false;
        if (area >= 10 && area < 20)
            return false;
        if (area >= 40 && area < 50)
            return false;
        if( area < 734 )
            return true;
        if( area < 750 )
            return false;
        if( area < 773 )
            return true;
        return false;
    }
    
    public void init() {
        
    }
    
    public void reset()
    {
        ring = new RingBuffer();
        sofar = 0;
        ssns = 0;
        ssnns = 0;
        matches = 0;
        eof = false;
	offset = 0;
    }
    
    public int match(int c, SenfResult results)
    {
    	matches = 0;
        eof = ( c == -1 );
        sofar <<= 1;
        lastp <<= 1; // shift last pepperdine control character
	++offset;
        if( eof ) {
            ring.push( EOFCHAR );
        } else {
            ring.push( (char)c );
            if( Character.isDigit( (char)c ) ) {
                sofar |= 1;
	    	return 0;
	    } else { // check for the pepperdine control character
                if( peppchar.equals( Character.toUpperCase( (char)c ) ) ){
                    lastp |= 1; // store the new control character for next check
                }
            }
        }
         // remove stuff we don't care about
        ssns = sofar & LENIENT_MASK_SSN_SEPARATORS_TRUNC;
        ssnns = sofar & LENIENT_MASK_SSN_NO_SEPARATORS_TRUNC;
        
        // Check for socials
	if( ssns == LENIENT_MASK_SSN_SEPARATORS ) {
		String possn = ring.read( 12 );
            int area = Integer.parseInt( possn.substring( 0, 3 ) );
            if( ssnAreaValid( area ) && ( lastp & CONTROL_MASK_SSN_SEPARATORS ) == 0 )
            {
                if( SSN_PATTERN.matcher( possn ).find() ) {
                    if( !( possn.substring( 4, 6 ).equals( "00" ) || possn.substring( 7, 11 ).equals( "0000" ) ) )
                    {
                        ++matches;
		    	results.addResult(offset - 12, 11);
                        sofar = 0;
			return matches;
                    }
                }
            }
        }
        if( ssnns == LENIENT_MASK_SSN_NO_SEPARATORS ) {
            String possn = ring.read( 10 );
            int area = Integer.parseInt( possn.substring( 0, 3 ) );
            if( ssnAreaValid( area ) && ( lastp & CONTROL_MASK_SSN_NO_SEPARATORS ) == 0 )
            {
                if( !(possn.substring( 3, 5 ).equals( "00" ) || possn.substring( 5, 9 ).equals( "0000" ) ) )
                {
                    ++matches;
		    results.addResult(offset - 10, 9);
                    sofar = 0;
		    return matches;
                }
            }
        }
       return matches;
    }
}
