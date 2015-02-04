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

public class Senf {

	public static String VERSION = "1.haku.338";
	public static String BASE_VERSION = "1.sasuke.111";
	private static SenfScanner s;
	private static SenfOptions opts;

	public static void main(String[] args) {
		SenfOptionsLoader load = new SenfOptionsLoader(new SenfOptions());
		load.loadOptions(args);
		opts = load.opts;
		opts.args = args;

		if(opts == null) {
			return;
		}

		try {
			if(opts.run) {
				if(opts.showGUI) {
					new SenfGUI(opts).showGUI();
				} else {
					Thread senfThread = new Thread(new SenfScanner(opts, null, null, null, null));
					senfThread.start();
				}
			}
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
