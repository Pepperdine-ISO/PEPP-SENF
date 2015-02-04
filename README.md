### senf: the mustardy sensitive number finder

senf is a portable tool for finding sensitive numbers. Use this tool to identify files on your system that may have Social Security Numbers (SSNs) or Credit Card Numbers (CCNs). The latest version can always be found at the senf site.

##### Download pre-built release binary

The latest release build can be downloaded at https://github.com/utiso/senf/releases/latest

##### Other senf-related products for superb purchase agreeings
* senf suite, for enterprise experts: http://www.otc.utexas.edu/ATdisplay.jsp?id=1065&term=senf
* senf preprocessor, for ids zoomzoom: http://www.otc.utexas.edu/ATdisplay.jsp?id=394&term=senf

##### License
senf is licensed under a Creative Commons License; see http://creativecommons.org/licenses/by-nc-sa/3.0/us/

##### Warning! [About senf]

*Warning*: Do not have a false sense of security after running this program

It is important to understand what senf is, and what senf is not

##### What senf is

* A program written in Java by humans
* A program which can quickly and conveniently point the operator of a computer to files which contain strings of text which resemble SSNs or CCNs
* A program which tries to find files with large quantities of SSNs/CCNs; you can tell it to find a single occurence of a pattern, but that will yield many false positives.

##### What senf is not

* An infallible oracle which will detect all SSNs/CCNs and only SSNs/CCNs.
* A prophet which will enlighten a computer operator as to the exact location of an SSN/CCN within a file (although the GUI version is somewhat sibylline).
* Designed to detect sensitive data in encoded/encrypted binary files. In fact, it skips many file extensions by default.


##### What all this means

* There will be false positives
* There will be false negatives


##### Which reduces to (even though it's longer)!
This tool is not to be regarded as the end-all in your effort to ensure your computer is free of SSN/CCN records. It simply will report to you files that contain numbers that could pose a security threat. Remember, it looks for strings of numbers -- and the typical computer has lots of these.

---

### Requirements

#### Java 1.6 JRE
No matter what system you're running on, you need the  Java 1.6 runtime (or greater); you do not need the whole Java 1.6 SDK, which includes the runtime.

#### System path
We assume that the Java interpreter is in your environment path (meaning, no matter where we try to run "java" from, it will run). The JRE installer should modify your system path to include the Java interpreter.

If you get a strange error message saying something along the lines of "If you see this, senf did not run!" then chances are your path is not set up correctly. Unfortunately, the solution to this is beyond the scope of this text.

---

### Installation
Once the JRE is installed, all you need to do is copy senf.jar and the seeds folder to some folder on the computer that is going to run the scan. You might also want to copy the configuration files to the same folder.

---

### Running
#### Brief Note

On some Operating Systems (typically Windows and Mac OS X), simply double-clicking on the senf.jar file will launch the program automatically. If this works, you can skip the rest of this section.

#### Windows

Open a command prompt
Navigate to the folder in which senf is installed.
Run java -jar senf.jar (with optional arguments)

#### Linux and Mac OS X

Open a command shell
Navigate to the folder in which senf is installed.
Run java -jar senf.jar (with optional arguments)

---

### Using senf
#### Usage: senf [OPTIONS]

| Option         | Default        | Effect |
| -------------- | -------------- | ------ |
| -q             | off            | quiet mode (display no output)
| -v	           | off            | verbose mode (display everything)
| -e	           | off            | print error messages to the screen
| -p <scan path> | working dir    | Set the path to start scanning from
| -l <yyyyMMdd>  | off            | Set modified-date check; files last modified before this date are skipped
| -f <filesize>  | infinite       | Set the max file size to scan; end size (no spaces) with 'g' for gigs, 'm' for megs, 'k' for kilobytes, and nothing for bytes
| -m <number>    | 15             | Set minimum number of times to match a CCN/SSN pattern before reporting a file
| -o <log file>  | senf\_DATE.txt | Set the name of the file (including path, if you like) where log information will be saved
| -al            | on             | Append the current log to the end of the file if it already exists
| -ac            | off            | Append configuration information to the end of the output log
| -nl            | off            | Do not use a log file
| -g             | off            | Hide the GUI
| -as            | off            | Auto-start scanning (ignored when -g is specified)
| -h             | n/a            | Display this help and exit

By default, senf only prints to the screen files which are matched -- not all output is shown.

#### Examples

* To search all files in your home directory in Linux/Mac OS X
	* java -jar senf.jar -p ~/
* To search all files in your home directory in Windows XP
	* java -jar senf.jar -p "C:\Documents and Settings\<yourname>"
* To scan only files <= 100MB, ensure that each one has at least 12 matches before marking it as possible, display error messages, and start in a folder called C:\mustard\gruga
	* java -jar senf.jar -f 100m -m 12 -e -p "C:\mustard\gruga"

Also, note that this program may take a while to complete; again, by default, the only things it prints to the screen are possible matches (ie no errors), so it may look like it's frozen, not printing anything for a while, but it's (probably) not.

As of the Sasuke.188 release, senf provides a GUI for ease of use. The GUI offers a results viewer to help the user quickly identify what was flagged by senf as being sensitive. Results appear in the central pane of the senf window as they are found; if an entry is clicked on, the senf Analyzer will pop up, showing the applicable matches in the file.

---

### Configuration files

#### Configuration

senf uses the file senf.conf to load default settings.

#### Extensions

As of the Haku version, senf uses an ACL in place of the old whitelist/blacklist system. The ACL is contained in the file senf.acl, and can be modified either by editing the file, or through the senf GUI.

ACL entries have three columns. The first column denotes whether to allow or deny matches. The second dictates what type of match to look for. The third contains the expression to search for. Possible entries for each row are listed below.

| ROW1  | ROW2       | ROW3 |
| ----- | ---------- | ---- |
| ALLOW | BEGINSWITH | <user_defined_expression>
| DENY	| CONTAINS   |
|       | ENDSWITH   |
|       | EXACTLY    |
|       | REGEX	     |

An example "senf.acl" file is included with common entries. In the case of two conflicting entries, the entry listed first will over rule the later entry.

---

### Libraries
senf relies on the Apache Tika library (https://tika.apache.org/) to parse file types. This library should be placed in the "lib" folder in order for senf to function properly. While use of this library does allow senf to scan many file types, there is a caveat: currently, Tika does make use of temporary files while scanning; under normal circumstances, these are deleted when they are no longer used, but in certain circumstances (e.g. JVM crash) they might not get cleaned up.

---

### How senf Works
The way senf scans has changed drasticly with the release of Haku. There are four important parts to senf Haku. Parsers, Seeds, Streams, and Stream Sources.

#### Streams

A Stream is something that senf can scan, and implements the class senfStream. An example of a "Stream" is a text file.

#### Stream Sources

A Stream Source is something that contains streams, and implemtnts the class senfStreamSource. An example of a Stream Source is a directory, or a zip file.

#### Seeds

A Seed is something that senf will look for in a Stream. Seeds implement the class Seed. As of senf Sasuke.188, Seeds are modular. This means that seeds may be added/removed from the "seeds" directory to modify what senf will or will not search for within a Stream. At the moment, senf includes a Seed for both Social Security Numbers and Credit Card numbers.

#### Parsers

Parses are the objects that tell the senf engine what each "object" that is to be scanned should be scanned as. That is, the Parser tells senf what type of Stream or StreamSource each senfObject should be cast as. Parsers implement the class senfParser, and are modular.

---

### Algorithms

senf looks for certain patterns to reduce false positives. Those patterns are described here. These patterns cannot be used to find every conceivable incarnation of the numbers senf searches for. However, if you have suggestions for improving the algorithms (and, better, known false negatives to back up your suggestions) please let us know.

#### Credit card numbers

##### Formats

There are a number of valid credit card formats. senf supports only the 16 digit formats. This includes Mastercard, some (but evidently not all) VISA, and Discover. It does NOT include, for example, American Express.

##### Separators

Credit cards numbers may be one long string of numbers (nnnnnnnnnnnnnnnn), or may be separated into groups of four digits (nnnn-nnnn-nnnn-nnnn). There are, of course, as many ways to delimit groups of digits as can be imagined; senf only counts matches that use either no separator, or only one of:

* dash ("-")
* space (" ")
* dot (".")
* pipe ("|")

##### Luhn check

Credit cards must pass a Luhn mod 10 check to be considered valid.

#### Social Security Numbers

##### Formats and separators

Socials are detected in both single string (nnnnnnnnn) and grouped (nnn-nn-nnnn) formats; permitted separators are the same as credit card numbers.

##### Validity checking

Socials are verified against their area (the first three digits), according to the Social Security Administration's current list of valid high groups. In addition, group and serial numbers may not be all zeroes.

---

### Developers developers developers developers

#### Buildin'

To compile the source code and build a runnable jar file, just run the included build.sh script. Or, to do it manually:

	javac -cp lib/tika.jar senf/*.java streams/*.java streamsources/*.java seeds/*.java parsers/*.java
	jar cmf manifest senf.jar senf/*.class senf/images/* streams/*.class streamsources/*.class

#### Extendin'

Seeds are objects which implement the senf.Seed interface. At runtime, senf will load any seeds located in the "seeds" directory and use them when scanning.

---

### Okay, that's all

Thank you for using senf! Feedback and questions are welcome; email security@utexas.edu
