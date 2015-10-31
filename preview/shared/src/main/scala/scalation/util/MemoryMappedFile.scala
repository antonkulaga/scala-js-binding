
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.2
 *  @date    Thu Sep 24 14:03:17 EDT 2015
 *  @see     LICENSE (MIT style license file).
 *
 *  @see www.programering.com/a/MDO2cjNwATI.html
 */

package scalation.util

import java.io.RandomAccessFile
import java.nio.{ByteBuffer, MappedByteBuffer}
import java.nio.channels.FileChannel

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `MemoryMappedFile` class provides support for memory-mapped files.
 *  This version works for `Array [Byte]`.
 *  @param fname  the file name
 *  @param sz     the size of the memory mapped file
 */
class MemoryMappedFile (fname: String, sz: Int = 1024)
{
    /** The random/direct access file
     */
    private val raf = new RandomAccessFile (MEM_MAPPED_DIR + fname, "rw");

    /** Mapping the file into memory
     */
    private val mraf = raf.getChannel ().map (FileChannel.MapMode.READ_WRITE, 0, sz);

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the size of the memory mapped file.
     */
    def size: Int = sz

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Get the bytes in the file starting at 'offset' and continuing for 'length'.
     *  @param offset  the offset in the file
     *  @param length  the number of bytes to get
     */
    def get (offset: Int = 0, length: Int = 1): Array [Byte] =
    {
        val buffer = Array.ofDim [Byte] (length)
        mraf.position (offset)
        mraf.get (buffer, 0, length)
        buffer
    } // get

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Put the bytes in the file starting at 'offset' and continuing for 'length'.
     *  @param buffer  the byte array buffer of content
     *  @param offset  the offset in the file
     *  @param length  the number of bytes to put
     */
    def put (buffer: Array [Byte], offset: Int = 0, length: Int = 1): ByteBuffer =
    {
        mraf.position (offset)
        mraf.put (buffer, 0, length)
    } // put

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Close the memory mapped file.
     */
    def close ()
    {
        raf.close ()
    } // close

} // MemoryMappedFile class


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 // MemoryMappedFileTest

