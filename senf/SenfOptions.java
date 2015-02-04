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
 * SenfOptions.java
 *
 * Created on February 26, 2007, 3:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package senf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.io.FilenameFilter;
import java.util.zip.*;
import java.util.Enumeration;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Sean Reid
 */
public class SenfOptions {

	// Options specified at runtime (pre-scan)
	public static final String DEFAULT_LOG_PREFIX = "senf_";
	public static final String DEFAULT_LOG_EXTENSION = ".txt";
	public static final int DEFAULT_MIN_MATCHES = 15;
	public static final int DEFAULT_MAX_FILE_LENGTH = -1;
	public static final String SCAN_ALL_ROOTS = "System";
	public static final String CCN_SEED = "CCNSeed.class";
	public static final String SSN_SEED = "SSNSeed.class";
	public static final int MIN_MATCH_LENGTH = 9;
	public long maxFileLen = DEFAULT_MAX_FILE_LENGTH;
	public int maxFileLenOrder = 0;
	public long maxFileLenReq = DEFAULT_MAX_FILE_LENGTH;
	public int minMatches = DEFAULT_MIN_MATCHES;
	public boolean checkModified = false;
	public long lastModified = 0;
	public Date lmd = null;
	public String dateReq;
	public boolean verbose = false;
	public boolean showWork = false;
	public boolean showError = true;
	public boolean logToFile = true;
	public boolean showGUI = true;
	public boolean appendConfs = false;
	public boolean appendLog = false;
	public boolean encryptLog = false;
	public String logPass;
	public File[] rootSearchDirs = null;
	public String logFilename = "";
	public Seed[] scanners = null;
	public SenfACL senfACL = null;
	public boolean run = true;
	public boolean scanHD = true;
	//public boolean scanWeb = false;
	public boolean junkshun = false;
	public int junk = 0;
	public String[] args;

	public void setupOutputLog() {
		//if no log file is provided, use the default name
		if(logFilename.length() == 0) {
			logFilename = DEFAULT_LOG_PREFIX;
			Calendar now = Calendar.getInstance();
			int day = now.get(Calendar.DAY_OF_MONTH);
			int month = (now.get(Calendar.MONTH)) + 1;
			String date = now.get(Calendar.YEAR) + "" + ((month < 10) ? "0" : "") + month + ((day < 10) ? "0" : "") + day + "" + now.get(Calendar.HOUR_OF_DAY);
			logFilename += date;

		}
		//check for log extension
		if(!logFilename.endsWith(DEFAULT_LOG_EXTENSION)) {
			logFilename = logFilename + DEFAULT_LOG_EXTENSION;
		}
		//Check if file exists if not append to log
		if(!appendLog) {
			while(new File(logFilename).exists()) {
				logFilename = logFilename.substring(0, logFilename.length() - DEFAULT_LOG_EXTENSION.length()) + "_new" + DEFAULT_LOG_EXTENSION;
			}
		}

		//Set junction scanning to true if the OS is a Windows Base
		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			junkshun = true;
		}


	}

	public void setMaxFileLength(String fileLen) throws NumberFormatException {
		switch(Character.toLowerCase(fileLen.charAt(fileLen.length() - 1))) {
			case 'g':
				this.maxFileLenReq = Long.parseLong(fileLen.substring(0, fileLen.length() - 1));
				this.maxFileLen = this.maxFileLenReq << 30;
				this.maxFileLenOrder = 3;
				break;
			case 'm':
				this.maxFileLenReq = Long.parseLong(fileLen.substring(0, fileLen.length() - 1));
				this.maxFileLen = this.maxFileLenReq << 20;
				this.maxFileLenOrder = 2;
				break;
			case 'k':
				this.maxFileLenReq = Long.parseLong(fileLen.substring(0, fileLen.length() - 1));
				this.maxFileLen = this.maxFileLenReq << 10;
				this.maxFileLenOrder = 1;
				break;
			case 'b':
				this.maxFileLenReq = Long.parseLong(fileLen.substring(0, fileLen.length() - 1));
				this.maxFileLen = this.maxFileLenReq;
				this.maxFileLenOrder = 0;
				break;
			default:
				this.maxFileLenReq = Long.parseLong(fileLen.substring(0, fileLen.length()));
				this.maxFileLen = this.maxFileLenReq;
				this.maxFileLenOrder = 0;
				break;
		}
	}

	public void setLogFilename(String filename) {
		this.logFilename = filename;
	}

	public void setVerbose(boolean on) {
		this.verbose = on;
		if(on) {
			this.showWork = true;
			this.showError = true;
		}
	}

	public void setMinMatches(String min) throws NumberFormatException {
		int minVal = Integer.parseInt(min);
		if(minVal < 1) {
			throw new NumberFormatException("Minimum matches must not be less than 1");
		}
		this.minMatches = minVal;
	}

	// TODO: Detect and avoid addition of children if parents selected
	// Idea: Sort by number of file separators ascending, break up around these,
	//          insert into a trie-like structure?
	// Better Idea: Just leave it. If they're dumb enough to do it, they're dumb
	// 		enough to sit through it. (TM)
	public void setRootDirs(String[] dirs) throws IOException {
		this.rootSearchDirs = new File[dirs.length];
		for(int i = 0; i < dirs.length; ++i) {
			if(dirs[i].equals(SCAN_ALL_ROOTS)) {
				this.rootSearchDirs = File.listRoots();
				return;
			} else {
				File tmp = new File(dirs[i]);
				if(tmp.isDirectory()) {
					this.rootSearchDirs[i] = tmp.getCanonicalFile();
				} else if(tmp.isFile()) {
					this.rootSearchDirs[i] = tmp.getParentFile().getCanonicalFile();
				} else {
					throw new IOException("Invalid file/folder");
				}
			}
		}
	}

	public void setLastModifiedDate(String date) throws ParseException {
		this.dateReq = date;
		this.lmd = new SimpleDateFormat("yyyyMMdd").parse(date);
		this.lastModified = this.lmd.getTime();
		this.checkModified = true;
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
		scanners = new Seed[plugins.length];

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
				scanners[i] = (Seed)seedloader.findClass("seeds." + classname).newInstance();
			} catch(Exception e) {
				System.out.println("Error loading plugin: " + e);
			}
		}

		if(scanners.length > 0) {
			System.out.println("Successfully loaded " + scanners.length + " seeds.");
		}
	}
}
