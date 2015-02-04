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
import java.io.*;
import java.util.HashMap;
import java.text.ParseException;

//This class is used to load the options in SenfOptions
public class SenfOptionsLoader {

	public SenfOptions opts;

	public SenfOptionsLoader(SenfOptions o) {
		opts = o;
	}

	public void loadSeeds() throws Exception {
		File pluginsdir = new File("seeds");

		if(!pluginsdir.isDirectory()) {
			System.out.println("Seeds directory does not exist, not loading seeds.");
			return;
		}

		File[] plugins = pluginsdir.listFiles(
			new FilenameFilter() {

				public boolean accept(File dir, String name) {
					if(name.endsWith("class") || name.endsWith("jar")) {
						//TODO seed filter...
						return true;
					}
					return false;
				}
			});
		opts.scanners = new Seed[plugins.length];

		for(int i = 0; i < plugins.length; ++i) {
			try {
				FileLoader seedloader = null;
				String filename = plugins[i].getName();
				int in;

				if((in = filename.indexOf(".class")) > 0) {
					seedloader = new FileLoader(".");
				} else if((in = filename.indexOf(".jar")) > 0) {
					seedloader = new FileLoader("seeds" + File.separator + plugins[i].getName());
				}

				String classname = filename.substring(0, in);
				opts.scanners[i] = (Seed)seedloader.findClass("seeds." + classname).newInstance();
			} catch(Exception e) {
				System.out.println("Error loading plugin: " + e);
			}
		}

		if(opts.scanners.length > 0) {
			System.out.println("Successfully loaded " + opts.scanners.length + " seeds.");
		}
	}

	public boolean loadOptions(String[] args) {
		opts.senfACL = new SenfACL("senf.acl");
		HashMap optm = loadMap("senf.conf");
		String opt;

		if((opt = (String)optm.get("verbose")) != null && isTrue(opt)) {
			opts.showWork = true;
			opts.showError = true;
			opts.verbose = true;
		}
		if((opt = (String)optm.get("maxfilelength")) != null && !opt.equals("")) {
			try {
				opts.setMaxFileLength(opt);
			} catch(NumberFormatException nfe) {
				System.out.println("Invalid maxfilelength in senf.conf!");
				return false;
			}
		}
		if((opt = (String)optm.get("minmatches")) != null && !opt.equals("")) {
			try {
				opts.setMinMatches(opt);
			} catch(NumberFormatException nfe) {
				System.out.println("Invalid minmatches in senf.conf!");
				return false;
			}
		}
		if((opt = (String)optm.get("path")) != null && !opt.equals("")) {
			try {
				opts.setRootDirs(opt.split(File.pathSeparator));
			} catch(IOException io) {
				System.out.println("Problem trying to get search path in senf.conf!");
				return false;
			}
		}
		if((opt = (String)optm.get("lastmodified")) != null && !opt.equals("")) {
			try {
				opts.setLastModifiedDate(opt);
			} catch(ParseException pe) {
				System.out.println("Date format error in senf.conf; use yyyyMMdd!");
				return false;
			}
		}
		if((opt = (String)optm.get("logfile")) != null && !opt.equals("")) {
			opts.setLogFilename(opt);
		}
		if((opt = (String)optm.get("showerrors")) != null && isTrue(opt)) {
			opts.showError = true;
		}
		if((opt = (String)optm.get("quiet")) != null && isTrue(opt)) {
			opts.showWork = false;
			opts.showError = false;
			opts.verbose = false;
		}
		if((opt = (String)optm.get("hidegui")) != null && isTrue(opt)) {
			opts.showGUI = false;
		}
		if((opt = (String)optm.get("appendconfs")) != null && isTrue(opt)) {
			opts.appendConfs = true;
		}
		if((opt = (String)optm.get("appendlog")) != null && isTrue(opt)) {
			opts.appendLog = true;
		}
		if((opt = (String)optm.get("logtofile")) != null && isTrue(opt)) {
			opts.logToFile = true;
		}
		if((opt = (String)optm.get("junction")) != null && isTrue(opt)) {
			opts.junkshun = !opts.junkshun;
		}
		try {
			for(int i = 0; i < args.length; ++i) {
				if(args[i].equals("-h")) {
					printCLIUsage(System.out);
					opts.run = false;
					return false;
				} else if(args[i].equals("-v")) {
					opts.showWork = true;
					opts.showError = true;
					opts.verbose = true;
				} else if(args[i].equals("-f")) {
					try {
						opts.setMaxFileLength(args[ ++
					 i
						] );
        	            } catch( NumberFormatException nfe ) {
        	                System.out.println( "Invalid size");
						return false;
					} catch(ArrayIndexOutOfBoundsException aioobe) {
						System.out.println("Invalid size");
						return false;
					}
				} else if(args[i].equals("-m")) {
					try {
						opts.setMinMatches(args[ ++
					 i
						] );
        	            } catch( NumberFormatException nfe ) {
        	                System.out.println( "Invalid matches");
						return false;
					} catch(ArrayIndexOutOfBoundsException aioobe) {
						System.out.println("Invalid matches");
						return false;
					}
				} else if(args[i].equals("-p")) {
					try {
						opts.setRootDirs(args[ ++i

						].split( File.pathSeparator ) );
        	            } catch( IOException io ) {
        	                System.out.println( "Problem trying to get search path, exiting...");
						return false;
					} catch(ArrayIndexOutOfBoundsException aioobe) {
						System.out.println("Problem trying to get search path, exiting...");
						return false;
					}
				} else if(args[i].equals("-l")) {
					try {
						opts.setLastModifiedDate(args[ ++
					 i
						] );
        	            } catch( ParseException pe ) {
        	                System.out.println( "Date format error; use yyyyMMdd");
						return false;
					} catch(ArrayIndexOutOfBoundsException aioobe) {
						System.out.println("Date format error; use yyyyMMdd");
						return false;
					}
				} else if(args[i].equals("-o")) {
					try {
						opts.setLogFilename(args[ ++
					 i
						] );
        	            } catch( ArrayIndexOutOfBoundsException aioobe ) {
        	                System.out.println( "Logfile name was not given.");
						return false;
					}
				} else if(args[i].equals("-e")) {
					opts.showError = true;
				} else if(args[i].equals("-q")) {
					opts.showWork = false;
					opts.showError = false;
					opts.verbose = false;
				} else if(args[i].equals("-g")) {
					opts.showGUI = false;
				} else if(args[i].equals("-ac")) {
					opts.appendConfs = true;
				} else if(args[i].equals("-al")) {
					opts.appendLog = true;
				} else if(args[i].equals("-nl")) {
					opts.logToFile = false;
				} else if(args[i].equals("-j")) {
					opts.junkshun = !opts.junkshun;
				}

			}

			return true;
		} catch(Exception e) {
			System.out.println("Grave error, you broke it");
			System.out.println(e);
			e.printStackTrace();
		}

		return false;
	}

	private static boolean isTrue(String opt) {
		opt = opt.toLowerCase();
		return opt.equals("true") || opt.equals("yes") || opt.equals("1") || opt.equals("sure");
	}

	private HashMap loadMap(String filename) {
		HashMap<String, String> optionsmap = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;

			while((line = br.readLine()) != null) {
				line = line.trim();
				if(line.charAt(0) == '#') {
					continue;
				}
				String[] option = line.split(":", 2);
				option[0] = option[0].trim();
				option[1] = option[1].trim();

				optionsmap.put(option[0], option[1]);
			}

			br.close();
		} catch(Exception e) {
			System.out.println("There was a problem loading values from '" + filename + "'!");
		}

		return optionsmap;
	}

	private static void printCLIUsage(PrintStream out) {
		out.println();
		out.println("Senf - the mustardy SEnsitive Number Finder. Version " + "<VERSION>" + " (Codebase " + "<BASE_VERSION>" + ")");
		out.println("Scans folders for files with possible social security or credit card numbers.");
		out.println("Usage: senf [OPTION]");
		out.println();
		out.println("  -ac\t\tAppend configuration file info to output log");
		out.println("  -al\t\tAppend session log to an already existing log");
		out.println("  -as\t\tAuto-start scanning (ignored when -g is specified)");
		out.println("  -e\t\tPrint error messages to screen");
		out.println("  -f\t\tSet the max file size to scan; end size (no spaces) with 'g' for gigs, 'm' for megs, 'k' for kilobytes, and nothing for bytes");
		out.println("  -g\t\tHide GUI");
		out.println("  -h\t\tDisplay this help and exit");
		out.println("  -l\t\tSet the last modified date to check, in yyyyMMdd format (files last modified before this date are skipped)");
		out.println("  -m\t\tSet minimum number of times to match a CCN/SSN pattern before reporting a file");
		out.println("  -nl\t\tDo not include a log file");
		out.println("  -o\t\tSet the name (and path) of the tab-delimited log file; default is senf_DATEnn; .txt will be appended");
		out.println("  -p\t\tSet the path to start scanning from");
		out.println("  -q\t\tQuiet mode");
		out.println("  -v\t\tVerbose mode");
		out.println();
		out.println("If a directory is not specified, senf starts in the current working directory");
		out.println("If file size not specified, senf checks all files");
		out.println("If minimum number of matches is not specified, senf assumes " + SenfOptions.DEFAULT_MIN_MATCHES);
		out.println();
		out.println("Written and maintained by Sean Reid, Jason Phelps, and Alek Amrani");
		out.println("InfoSec @ UT Austin");
		out.println();
		out.println("Report bugs to <abuse@utexas.edu>.");
	}
}
