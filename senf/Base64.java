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
 *@author Sean Reid
*/

package senf;

public class Base64
{
    
	private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	private static void binInt( int i )
	{
		int x = 0x80000000;
		while( x != 0 )
		{
			System.out.print(  ( ( x & i ) != 0 ) ? "1" : "0" );
			x >>>= 1;
		}
		System.out.println();
	}
	
	public static String encode( byte[] bytes )
	{
		StringBuffer sb = new StringBuffer( bytes.length / 3 * 4 );

		int i = 0, x = 0;

		for( ; i < bytes.length - 3; i += 3 ) {
			x = ( bytes[ i ] << 16 ) & 0x00ff0000;
			x |= ( i + 1 < bytes.length ) ? ( bytes[ i + 1 ] << 8 ) & 0x0000ff00 : 0;
			x |= ( i + 2 < bytes.length ) ? ( bytes[ i + 2 ] ) & 0x000000ff : 0;

			sb.append( chars.charAt( ( x >> 18 ) & 0x3F ) );
			sb.append( chars.charAt( ( x >> 12 ) & 0x3F ) );
			sb.append( chars.charAt( ( x >>  6 ) & 0x3F ) );
			sb.append( chars.charAt( x & 0x3F ) );
		}

		x = ( bytes[ i ] << 16 ) & 0x00ff0000;
		x |= ( i + 1 < bytes.length ) ? ( bytes[ i + 1 ] << 8 ) & 0x0000ff00 : 0;
		x |= ( i + 2 < bytes.length ) ? ( bytes[ i + 2 ] ) & 0x000000ff : 0;

		sb.append( chars.charAt( ( x >> 18 ) & 0x3F ) );
		sb.append( chars.charAt( ( x >> 12 ) & 0x3F ) );

		if( i == bytes.length - 1 ) {
			sb.append( "==" );
		} else {
			sb.append( chars.charAt( ( x >>  6 ) & 0x3F ) );

			if( i == bytes.length - 2 )
				sb.append( "=" );
			else
				sb.append( chars.charAt( x & 0x3F ) );
		}

		return sb.toString();
	}

	public static byte[] decode( String string )
	{
		int off = 0;
		int sl = string.length();

		if( sl - 1 >= 0 && string.charAt( sl - 1 ) == '=' ) {
			++off;
			if( sl - 2 >= 0 && string.charAt( sl - 2 ) == '=' )
				++off;
		}

		byte[] bytes = new byte[ string.length() / 4 * 3 - off ];

		byte[] sBytes = string.getBytes();

		int i = 0, x = 0, b = 0;

		for( ; i < sBytes.length - 4; i += 4) { 
			x = ( chars.indexOf( string.charAt( i ) ) & 0x3F ) << 18;
			x |= ( chars.indexOf( string.charAt( i + 1 ) ) & 0x3F ) << 12;
			x |= ( chars.indexOf( string.charAt( i + 2 ) ) & 0x3F ) << 6;
			x |= ( chars.indexOf( string.charAt( i + 3 ) ) & 0x3F );

			bytes[ b++ ] = (byte)( ( x >> 16 ) & 0xFF );
			bytes[ b++ ] = (byte)( ( x >>  8 ) & 0xFF );
			bytes[ b++ ] = (byte)( x & 0xFF );
		}

		x = ( chars.indexOf( string.charAt( i ) ) & 0x3F ) << 18;
		x |= ( chars.indexOf( string.charAt( i + 1 ) ) & 0x3F ) << 12;
		x |= ( string.charAt( i + 2 ) == '=' ) ? 0 : ( chars.indexOf( string.charAt( i + 2 ) ) & 0x3F ) << 6;
		x |= ( string.charAt( i + 3 ) == '=' ) ? 0 : ( chars.indexOf( string.charAt( i + 3 ) ) & 0x3F );

		bytes[ b++ ] = (byte)( ( x >> 16 ) & 0xFF );
		if( b < bytes.length ) {
			bytes[ b++ ] = (byte)( ( x >>  8 ) & 0xFF );
			if( b < bytes.length )
				bytes[ b++ ] = (byte)( x & 0xFF );
		}

		return bytes;
	}
}
