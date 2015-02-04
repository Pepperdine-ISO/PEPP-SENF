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
package streams;

//Import Senf Packages
import senf.CompressedSenfStream;
import senf.SenfOptions;

//Import Java Packages
import java.io.InputStream;
import java.net.URI;


/*
 * This class is used to create a layer of abstraction between a SenfStream, a Compressed SenfStream, 
 * and a ZipStream. This is needed because once a compressed stream has been read, it can not be reset.
 * That means that in order to re-read a compressed stream, you must recreate the stream.  With the 
 * extra layer of abstraction, it becomes easy to determine if the stream must be recreated before 
 * trying to re-read the stream.
*/
public class ZipStream implements CompressedSenfStream
{
	SenfInputStream sis;

	public ZipStream( InputStream is ){ this( is, "no_path", "no_name" ); }

	public ZipStream( InputStream is, String name ) { this( is, "", name ); }

	public ZipStream( InputStream is, String path, String name ) { sis = new SenfInputStream( is, path, name ); }

	public InputStream getInputStream() { return sis.getInputStream(); }

	public String getName() { return sis.getName(); }

	public long size() { return sis.size();	}

	public URI getURI() { return sis.getURI(); }

	public String getPath() { return sis.getPath(); }

	public boolean shouldScan( SenfOptions opts ) { return sis.shouldScan( opts ); }
}
