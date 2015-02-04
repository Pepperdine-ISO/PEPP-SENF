package senf;

public class RingBuffer {
    // This needs to be a power of 2 (else mask won't work)
    private int size = 0x20;
    private int mask = size - 1;
    private int writePos = 0;
    private char[] buf = new char[ size ];
    public RingBuffer() {
        // Clear out the buffer
        for( int i = 0; i < size; ++i ) {
            buf[ i ] = 0x00;
        }
    }
    public void inc() {
        ++writePos;
        writePos &= mask;
    }
    public void push( char next ) {
        buf[ writePos ] = next;
        inc();
    }
    public String read( int length ) {
        char[] result = new char[ length ];
        int begin = writePos - length;
        if( begin >= 0 ) {
            System.arraycopy( buf, begin, result, 0, length );
        } else {
            int midLen = -begin;
            // Start of the string to end of ring
            System.arraycopy( buf, size - midLen, result, 0, midLen );
            // Start of ring to end of string
            System.arraycopy( buf, 0, result, midLen, length - midLen );
        }
        return new String( result );
    }
}
