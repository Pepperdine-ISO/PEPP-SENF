package senf;

/**
 *
 * @author Sean Reid
 */
public interface Seed {
    /*
     * Called when object loaded
     */
    public void init();
    
    /*
     * Called each time new file is scanned
     */
    public void reset();
    
    /*
     * Scanning goes here
     */
    public int match(int c, SenfResult results);
}
