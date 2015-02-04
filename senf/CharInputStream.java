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
package senf;

//Import Java Packages
import java.io.IOException;
import java.io.InputStream;

//This class is basically a wrapper for an array of Chars into an InputStream
public class CharInputStream extends InputStream
{
	private char[] text;
	private int pos;

	public CharInputStream( char[] t )
	{
		text = t;
		pos = 0;
	}

	public int available() throws IOException
	{
		return text.length;
	}

	public void close() throws IOException {}

	public void mark( int readlimit )  
	{
		pos = readlimit;
	}

	public boolean markSupported()   
	{
		return true;
	}

	public int read() throws IOException
	{
		pos++;
		if( pos >= text.length)
		{
			return -1;
		}
		return (byte)text[ pos-1 ];
	}

	public int read( byte[] b ) throws IOException
	{
		return read( b, 0, b.length);
	}

	public int read( byte[] b, int off, int len ) throws IOException
	{
     		if (b == null) 
		{
                    	throw new NullPointerException();
                }
		else if (off < 0 || len < 0 || len > b.length - off) 
		{
                    	throw new IndexOutOfBoundsException();
                } 
		else if (len == 0) 
		{
                    	return 0;
                }
        
                int c = read();

                if (c == -1) 
		{
                    	return -1;
                }

                b[off] = (byte)c;     
                int i;

                try 
		{
                    	for (i=1; i < len ; i++) 
			{
                        	c = read();
                        	if (c == -1) 
				{
                            		break;
                        	}

                        	b[off + i] = (byte)c;
                    	}
			
			return i;
                } 
		catch (IOException ee) {}

                throw new IOException();
	}
}
