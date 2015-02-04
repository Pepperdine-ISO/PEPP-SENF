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
 * CCNSeed.java
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
public class CCNSeed implements Seed {
    RingBuffer ring;
    private static final char EOFCHAR = (char)0x00;
    int sofar;
    int ccns;
    int ccnns;
    int matches;
    long offset;
    boolean eof;
    private static final Pattern CCN_PATTERN = Pattern.compile( "[3-6][0-9]{3}([-\\. \\|])[0-9]{4}\\1[0-9]{4}\\1[0-9]{4}" );
    private static final Pattern CCN_NS_PATTERN = Pattern.compile( "[3-6][0-9]{15}" );

    private static final int LENIENT_MASK_CCN_SEPARATORS = 0xf7bde;
    private static final int LENIENT_MASK_CCN_NO_SEPARATORS = 0x1fffe;
    private static final int LENIENT_MASK_CCN_SEPARATORS_TRUNC = 0x1fffff;
    private static final int LENIENT_MASK_CCN_NO_SEPARATORS_TRUNC = 0x3ffff;

    public CCNSeed(){}

    private boolean luhnValid( String ccn ) {
        int sum = 0;
        int par = 0;
        int di = 0;
        for( int i = 0; i < ccn.length(); ++i ) {
            if( Character.isDigit( ccn.charAt( i ) ) ) {
                int digit = ccn.charAt( i ) - '0';
                if( ( di & 0x1 ) == par ) {
                    digit <<= 1;
                    if( digit > 9 ) digit -= 9;
                }
                ++di;
                sum += digit;
            }
        }
        return ( sum % 10 ) == 0;
    }
    
    public void init() {
        
    }
    
    public void reset()
    {
        ring = new RingBuffer();
        sofar = 0;
        ccns = 0;
        ccnns = 0;
        matches = 0;
        eof = false;
	offset = 0;
    }
    
    public int match(int c, SenfResult results)
    {
    	matches = 0;
        eof = ( c == -1 );
        sofar <<= 1;
	++offset;
        if( eof ) {
            ring.push( EOFCHAR );
        } else {
            ring.push( (char)c );
            if( Character.isDigit( (char)c ) ) {
                sofar |= 1;
	    	return 0;
	    }
        }
         // remove stuff we don't care about
        ccns = sofar & LENIENT_MASK_CCN_SEPARATORS_TRUNC;
        ccnns = sofar & LENIENT_MASK_CCN_NO_SEPARATORS_TRUNC;
        // Check for credit cards
        if( ccns == LENIENT_MASK_CCN_SEPARATORS ) {
            String posccn = ring.read( 20 );
            if( CCN_PATTERN.matcher( posccn ).find() ) {
                if( luhnValid( posccn ) ) {
                    ++matches;
		    results.addResult(offset - 20, 19);
                    sofar = 0;
		    return matches;
                }
            }
        }
        if( ccnns == LENIENT_MASK_CCN_NO_SEPARATORS ) {
            String posccn = ring.read( 17 );
            if( CCN_NS_PATTERN.matcher( posccn ).find() ) {
                if( luhnValid( posccn ) ) {
                    ++matches;
		    results.addResult(offset - 17, 16);
                    sofar = 0;
		    return matches;
                }
            }
        }
        return matches;
    }
}
