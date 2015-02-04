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
 * This class is used to create a layer of abstraction between a SenfStream, and a ZipStream. 
 * This is needed because once a compressed stream has been read, it can not be reset.
 * That means that in order to re-read a compressed stream, you must recreate the stream.  With the
 * extra layer of abstraction, it becomes easy to determine if the stream must be recreated before
 * trying to re-read the stream.
*/
package senf;

public interface CompressedSenfStream extends SenfStream {}
