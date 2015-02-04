package senf;

import streamsources.*;
import streams.*;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import org.apache.tika.Tika;
import java.io.InputStreamReader;

public class SenfScanner implements Runnable {

	private final String ACL_FILE = "senf.acl";
	private final String TAB = "\t";
	private SenfACL acl;
	private SenfOptions opts;
	private SenfResult results;
	private SenfDoneThing sdt;
	private SenfMatchEvent sme;
	private OutputThing out;
	private OutputThing status;
	private int numMatches = 0;
	private Tika tika;

	public SenfScanner(SenfOptions o, SenfDoneThing done, SenfMatchEvent match, OutputThing ot, OutputThing so) throws Exception {
		acl = new SenfACL(ACL_FILE);
		sdt = done;
		sme = match;
		opts = o;
		opts.loadSeeds();
		results = new SenfResult(opts.minMatches);
		tika = new Tika();

		out = ot;
		status = so;
		opts.setupOutputLog();

		if(!opts.showGUI) {
			out = new OutputThing() {

				int tried = 0;

				private void log(String words) {
					String write = words;
					if(tried == 0) {
						try {
							if(opts.logToFile) {
								if(opts.encryptLog) {
									Encrypter enc = new Encrypter(opts.logPass);
									write = enc.encrypt(words);
								}
								FileWriter logFile = new FileWriter(new File(opts.logFilename), true);
								logFile.write(write + "\n");
								logFile.close();
							}
						} catch(Exception E) {
							System.out.println("Error writing to Log File. SENF will continue without a Log File.");
							tried = 1;
						}
					}
				}

				public void print(String words) {
					System.out.print(words);
					log(words);
				}

				public void println(String words) {
					System.out.println(words);
					log(words);
				}

				public void println() {
					System.out.println();
					log("");
				}
			};
		}
	}

	public void run() {
		opts.junk = 0;
		opts.senfACL = new SenfACL("senf.acl");

		try {
			out.println("Senf Version " + Senf.VERSION + " (Base Version: " + Senf.BASE_VERSION + ") Output Log");
			out.println(Calendar.getInstance().getTime().toString());
			out.println("User: " + System.getProperty("user.name") + " on " + java.net.InetAddress.getLocalHost().getHostName());
			out.println();

			if(opts.maxFileLen <= 0) {
				out.println("Checking objects of any size.");
			} else {
				out.println("Checking objects up to " + opts.maxFileLen + " bytes.");
			}

			if(opts.checkModified) {
				out.println("Checking only objects modified since " + opts.lmd + ".");
			} else {
				out.println("Ignoring modification date.");
			}

			out.println("Checking for " + opts.minMatches + " matches per object.");
		} catch(Exception E) {
			System.out.println("Senf Output Error.  Senf will not report anything!");
			E.printStackTrace();
		}

		if(opts.scanHD) {
			for(int i = 0; i < opts.rootSearchDirs.length; i++) {
				out.println("Starting search at " + opts.rootSearchDirs[i]);

				try {
					File f = opts.rootSearchDirs[i];

					if(f.isDirectory()) {
						scan(new FileStreamSource(opts.rootSearchDirs[i]));
					} else if((f.getName().matches(".*\\.zip$"))) {
						scan(new ZipStreamSource(new FileStream(opts.rootSearchDirs[i])));
					} else if(f.isFile()) {
						scan(new FileStream(opts.rootSearchDirs[i].getName()));
					}
				} catch(Exception E) {
					System.out.println("Senf encountered an error, and will now stop.");
					E.printStackTrace();
					return;
				}
			}
		}

		//clean up, clean up, everybody every where.  clean up, clean up, everybody do your share...
		out.println("\nDone.  " + numMatches + " matches found.");
		if(opts.showGUI) {
			sdt.doStuff();
		}

		try {
			if(opts.logToFile) {

				out.println("There was a total of " + opts.junk + " unreadable files.");
				if(opts.junk > 0) {
					out.println("The most common cause of this is Junctions in Microsoft Windows.  Please see the README for more info.");
				}
				if(opts.appendConfs) {
					out.println("\nACL Follows: ");

					BufferedReader br = new BufferedReader(new FileReader(opts.senfACL.getFilename()));
					String line;

					while((line = br.readLine()) != null) {
						out.println(line);
					}
				}

				out.print("---------------------------------------------------------------------------------");
			}
		} catch(Exception E) {
			System.out.println("Error Finalizing the Output Log.");
		}
	}

	private void scan(SenfStreamSource sss) throws SenfObjectException {
		if(opts.senfACL.accept(sss.getName())) {
			try {
				if(opts.showGUI) {
					status.print("Checking Source: " + sss.getName());
				}
			} catch(Exception E) {
				status.print("Error while reporting SENF's status.");
			}

			while(sss.hasNext()) {
				SenfObject so = sss.next();
				if(so instanceof SenfStream) {
					SenfStream ss = (SenfStream)so;
					scan(ss);
				} else if(so instanceof SenfStreamSource) {
					if((!(sss instanceof ZipStreamSource)) || ((sss instanceof ZipStreamSource) && !(so instanceof ZipStreamSource))) {
						scan((SenfStreamSource)so);
					}
				}
			}

			if(opts.junkshun && sss.containsJunk()) {
				if(((JunkStreamSource)sss).isJunk()) {
					out.println("There was an error reading the StreamSource: " + sss.getName());
					out.println(sss.getName() + " located at " + sss.getURI() + " is unreadable.");
					opts.junk++;
				}
			}
		}
	}

	private boolean scan(SenfStream ss) {
		try {
			if(ss.shouldScan(opts)) {
				BufferedReader br;
				try {
					br = new BufferedReader(tika.parse(ss.getInputStream()));
					// Sometimes tika blows up on the first read. Attempt read so this can be handled.
					br.mark(1);
					br.read();
					br.reset();
				} catch(IOException ioe) {
					// If tika can't parse, just read as plain text
					br = new BufferedReader(new InputStreamReader(ss.getInputStream()));
				}
				int matches = 0;
				boolean eof = false;

				results.reset(ss);

				for(int i = 0; i < opts.scanners.length; i++) {
					opts.scanners[i].reset();
				}

				while(!eof) {
					int c = br.read();
					eof = (c == -1);

					for(int i = 0; i < opts.scanners.length; i++) {
						matches += opts.scanners[i].match(c, results);
					}
					if(matches == opts.minMatches) {
						br.close();


						out.println("Scanning Stream: " + ss.getURI() + TAB + "size: " + ss.size() + " bytes" + TAB + "Found Match");


						if(opts.showGUI) {

							SenfResult srcp = new SenfResult(results);
							sme.onMatch(srcp);
						}
						numMatches++;
						return true;
					}
				}
				br.close();
				if(opts.verbose) {
					out.println("Scanning Stream: " + ss.getURI() + TAB + "size: " + ss.size() + " bytes");
				}

			}
		} catch(SenfStreamException sse) {
			System.out.println(sse);
		} catch(FileNotFoundException fnf) {
			System.out.println(fnf);
		} catch(IOException ioe) {
			System.out.println(ioe);
			ioe.printStackTrace();
		} catch(SenfObjectException soe) {
			System.out.println(soe);
		}
		return false;
	}
}
