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

package senf;

import java.io.File;
import java.io.Reader;
import java.io.InputStream;

public interface SenfStream extends SenfObject
{
	public InputStream getInputStream() throws SenfStreamException;
	public long size();
	public boolean shouldScan( SenfOptions opts ) throws SenfStreamException;
}
