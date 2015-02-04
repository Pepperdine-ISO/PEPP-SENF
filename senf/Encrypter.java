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
 * SenfFileFilter.java
 *
 * Created on July 7, 2008
 *
 */

/*
 *@author Alek Amrani
*/

package senf;

import java.io.*;
import javax.crypto.*; 
import javax.crypto.spec.*;
import java.security.spec.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

public class Encrypter {
        private Cipher encryptionCipher;
        private Cipher decryptionCipher;
        byte[] salt = { (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32, (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03 };
        int iterationCount = 19;

    	//creates an crypto object that uses PBE based DES encryption.... and yeah, DES is horrid, but its what we've got right now
        Encrypter( String password ) {
            try {
                //key... like the one I don't to the office... which means I get locked out post 5
                KeySpec keySpec = new PBEKeySpec( password.toCharArray() );
                SecretKey key = SecretKeyFactory.getInstance( "PBEWithMD5AndDES" ).generateSecret( keySpec );

		encryptionCipher = Cipher.getInstance( key.getAlgorithm() );
                decryptionCipher = Cipher.getInstance( key.getAlgorithm() );
                AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

                encryptionCipher.init( Cipher.ENCRYPT_MODE, key, paramSpec );
                decryptionCipher.init( Cipher.DECRYPT_MODE, key, paramSpec );
            }
            catch (Exception e)
            {
            	System.out.println( "Encryption Object Error: " + e );
            }
        }

	//encrypts string
        public String encrypt( String toEncrypt )
        {
            try
            {
            	//encrypt
                byte[] bytes = toEncrypt.getBytes( "ASCII" );
                byte[] encoded = encryptionCipher.doFinal( bytes );

                return Base64.encode( encoded );
            }
            catch( Exception e )
            {
            	System.out.println( "Encryption Error: " + e );
            }
            return null;
        }

	//decrypts string
        public String decrypt( String toDecrypt )
        {
            try
            {
                // see the above comment for rant, replacing Encoder with Decoder... kthxbye
			//note: "above comment" no longer exists
                byte[] decoded = Base64.decode( toDecrypt );

                // decrypt
                byte[] bytes = decryptionCipher.doFinal( decoded );
                return new String( bytes, "ASCII" );
            }
            catch( Exception e )
            {
            	System.out.println( "Decryption Error: " + e );
		e.printStackTrace();
            }
            return null;
        }

	//encrypts txt file
        public void encryptTextFile( File file )
        {
                String toEncrypt = getContents( file );

    	        wipe( file );
    		setContents( file, encrypt( toEncrypt ) );
        }

	//decrypts txt file
        public void decryptTextFile( File file )
        {
    	        String toDecrypt = getContents( file );

    	        wipe( file );
    	        setContents( file, decrypt( toDecrypt ) );
        }

	//Reads the contents of the passed file into a string
        public String getContents( File file )
        {
        	StringBuilder result = new StringBuilder();

    	    try
    	    {
    	    	BufferedReader input =  new BufferedReader( new FileReader( file ) );
    	        String nextLine = new String();

    	        while( ( nextLine = input.readLine() ) != null )
    	        {
    	          result.append( nextLine );
    	          result.append( System.getProperty( "line.separator" ) );
    	        }

    	        input.close();
    	    }
    	    catch( Exception e )
    	    {
    	    	System.out.println( "Encryption/Decryption File Error: " + e );
    	    }

    	    return result.toString();
        }

	//writes the passed string to the passed file
        private void setContents( File file, String content )
        {
    	    try
    	    {
    	    	BufferedWriter output =  new BufferedWriter( new FileWriter( file ) );

    	        output.write( content );
    	        output.close();
    	    }
    	    catch( Exception e )
    	    {
    	    	System.out.println( "Encryption/Decryption File Error: " + e );
    	    }
        }

	/*So one of java's downfalls is apparent here...
	 *The fact that it runs on (most) systems means that
	 *It has a hard time with Native IO, which is necessary
	 *To truly delete a file.  This method *tries* to force
	 *Java to use Native IO, *BUT*, there are no garuntees.
	 *If Java can't use Native IO for some reason, it will
	 *Revert back to its evil ways, and the old file will
	 *Still be available forensicly... Basically, you really
	 *Should use something besides senf to encrypt and
	 *Delete your stuff.  That said, its still here :-)
	*/
    	private void wipe( File file )
    	{
    		try
    		{
    			FileChannel eraser = new RandomAccessFile( file, "rw" ).getChannel();
    			int numBytes = ( int )eraser.size();
    			byte[] fill = new byte[numBytes];
    			int i;

    			for( i=0; i<numBytes; i++ )
    			{
    				fill[i] = 0;
    			}

    			ByteBuffer toWrite = ByteBuffer.allocateDirect(numBytes);
    			toWrite.wrap(fill);
    			eraser.write( toWrite );
    			eraser.close();
    		}
    		catch( Exception e )
    		{
    			System.out.println( "Wiping Error: " + e );
    		}
    	}
    }

