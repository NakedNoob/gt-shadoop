import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.fs.Path;

public class Query {

	private final String TXT_DATA = "/tmp/text/";
	/**
	 * User name on the hadoop system
	 */
	private final String USER = "gordon";
	/**
	 * Data directory to hold txt files for the wordcount
	 */
	private final String HDFS_DATA_DIR = "/user/" + USER + "/data/";
	/**
	 * Local file system temp directory
	 */
	private final String TMP = "/tmp";

	/**
	 * gt-shadoop temporary directory within the local /tmp
	 */
	private final String DIR = "/gt-shadoop";

	/**
	 * User defined name of each job. Output will be sent to a directory by this
	 * name. Each job must be unique, and the job directory deleted upon
	 * returning the values.
	 */
	private String jobName;

	/**
	 * String - prefixed with a timestamp, this is the default name the output
	 * directory will be called after results are created.
	 */
	private final String DEFAULT_NAME = "_Results";

	/**
	 * HDFS Utility to perform basic duties in the hadoop file system
	 */
	private final HDFSadmin hdfs;

	/**
	 * Hadoop's Path
	 */
	Path hadoopPath = null;

	/**
	 * Used in reference to the HDFS filesystem
	 */
	private final String hdfsHome = "/user/gordon/";

	/**
	 * Used in reference to the ext* filesystem
	 */
	private final String hadoopMainDir = "/home/gordon/hadoop-s1.0.1/";

	/**
	 * Used in reference to the ext* filesystem
	 */
	private final String hadoopBinDir = hadoopMainDir + "bin/";

	/**
	 * ProcessBuilder
	 */
	// private final ProcessBuilder process = null;

	/**
	 * StringBuilder to contain the data from the output of the process
	 */
	private final StringBuilder bufferOut = new StringBuilder();

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public Query() throws IOException {
		try {
			hdfs = new HDFSadmin();
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	/**
	 * This checks to see if this directory already exists. If a job is
	 * attempted with an existing directory as it's target, it will fail.
	 * 
	 * @param pName
	 * @return True if directory doesn't exist. False if the directory already
	 *         exists.
	 */
	private boolean nameValid(String pName) {
		boolean result = false;

		// This will need the HDFSClient to check on the existence of the
		// directory
		// before setting the name.

		try {
			if (!hdfs.ifExists(new Path(hdfsHome + pName))) {
				result = true;
				System.out.printf("\nDirectory name is valid.");
				jobName = pName;
				hadoopPath = new Path(hdfsHome + pName);
			}
		} catch (IOException e) {
			System.err.printf("\nDirectory '%s' exists.  Choose another name.");
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Get the job name. This will be the output directory at the completion of
	 * the job.
	 * 
	 * @return The name of the current job.
	 */
	public String getName() {
		if (jobName.isEmpty()) {
			return "No name is set.\n";
		}
		return jobName;
	}

	/**
	 * The Range Query, for returning spatial data. Assumes ranges passed to it
	 * are valid range values.
	 * 
	 * @return True if no exceptions were detected
	 * @throws InterruptedException
	 */
	public boolean runRangeQuery(int a, int b, int c, int d)
			throws InterruptedException {
		boolean result = false;
		jobName = generateName();

		// form the string command
		StringBuilder cmd = new StringBuilder();

		cmd.append("bin/hadoop jar hadoop-operations-s1.0.1.jar ");
		cmd.append(String.format("rangequery points %s ", jobName));
		cmd.append(String.format("rect:%d,%d,%d,%d ", a, b, c, d));
		cmd.append("shape:point -overwrite");

		// StringBuilder cmd2 = new StringBuilder("ls /");

		try {
			runCommand(cmd.toString());
			result = true;
		} catch (Exception e) {
			System.err.printf("\nError: %s", e.getMessage());
		}

		return result;
	}

	/**
	 * Runs the wordcount example contained in the hadoop-examples-*.jar Working
	 * directory is currently set to ../hadoop-s1.0.1 Paths should be created
	 * with that in mind.
	 * 
	 * @return
	 */
	public boolean runWordCount(String pTxtFile) {
		boolean result = false;
		StringBuilder cmd = new StringBuilder();

		String hdfsPathName = HDFS_DATA_DIR + pTxtFile;

		// Check to see if the txt file exists in the ext file system
		File input = new File(TXT_DATA + pTxtFile);
		Path outPath = new Path(hdfsPathName);

		if (!input.exists()) {
			System.err.printf("\n'%s' does not exist", input.getAbsolutePath());
			return result;
		} else {
			System.out.printf("\n'%s' exists, copying to hdfs...",
					input.getAbsolutePath());
			// file exists, now transfer it to the hdfs data directory
			try {
				if (!hdfs.ifExists(new Path(HDFS_DATA_DIR))) {
					System.out.printf("\n *** '%s' does NOT exist in hdfs\n",
							HDFS_DATA_DIR);

					if (hdfs.mkdir(HDFS_DATA_DIR)) {
						System.err.printf("\n'%s' was not created",
								HDFS_DATA_DIR);
						// return result;
					}
				}
			} catch (IOException e1) {
				System.err.printf("\nError creating hdfs directory: %s",
						e1.getMessage());
				return result;
			}
			try {
				// check if the file already exists
				if (!hdfs.ifExists(outPath)) {
					// hdfs.copyFromLocal(input.getAbsolutePath(),
					// hdfsPathName);
					hdfs.addFile(input.getAbsolutePath(), hdfsPathName);
				}
			} catch (IOException e) {
				System.err
						.printf("\nError copying to hdfs: %s", e.getMessage());
				// e.printStackTrace();
				return result;
			}

		}
		cmd.append("bin/hadoop jar hadoop-examples-*.jar wordcount ");
		cmd.append(hdfsPathName + " "); // directory local
										// to
		// ../hadoop-s1.0.1
		jobName = generateName();
		cmd.append(jobName);

		try {
			runCommand(cmd.toString());
			result = true;
		} catch (Exception e) {
			System.err.printf("\nError: %s", e.getMessage());
		}

		System.out.printf("\nResults: \n%s", getResults());

		if (!deleteResultsHDFS(hdfsHome + jobName)) {
			System.out.printf("\nHDFS Directory not deleted.");
		}

		return result;
	}

	/**
	 * Get the results file, copies it to /tmp/gt-shadoop and returns the
	 * contents as a string
	 * 
	 * @return String of the contents of the result file
	 */
	private String getResults() {
		StringBuilder buffer = new StringBuilder();
		File tmpDir = new File(TMP + DIR);
		// Make sure local temporary directory exists
		if (!new File(TMP).exists()) {
			// create if it doesn't exist
			tmpDir.mkdir();
			// check for the existence of a /TMP/DIR directory
		} else if (!tmpDir.exists()) {
			// create if it doesn't exist
			tmpDir.mkdir();
		}

		// copy results file from hdfs to local

		// TODO - check if there are multiple files as the result
		// Not needed at this time
		String fileName = tmpDir.getAbsolutePath() + "/" + jobName + ".txt";
		try {
			hdfs.copyToLocal(hdfsHome + jobName + "/part-r-00000", fileName);
		} catch (IOException e) {
			System.err.printf("\nError, getResults(): %s", e.getMessage());
		}

		try {
			buffer.append(readFile(fileName));
		} catch (IOException e) {
			System.err.printf("\nError: %s", e.getMessage());
			buffer.setLength(0);
		}

		return buffer.toString();
	}

	/**
	 * Converts the output stream from the Process and displays it for the user.
	 * For debugging purposes only.
	 * 
	 * @param InputStream
	 *            from Process
	 * @return String from the Inputstream of the process.
	 * @throws IOException
	 */
	private String convertStreamToStr(InputStream is) throws IOException {

		if (is != null) {
			StringWriter writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
					bufferOut.append(buffer);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	/**
	 * Generates a name based on today's date and time, to prevent creating the
	 * same output directory twice.
	 * 
	 * @return String with a timestamp and default tag
	 */
	private String generateName() {
		StringBuilder name = new StringBuilder();

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhmmss");

		name.append(sdf.format(date));

		name.append(DEFAULT_NAME);
		return name.toString();
	}

	/**
	 * Delete the file and directory from the system. Call this immediately
	 * after a successful 'getResults()'
	 * 
	 * @param pName
	 * @return True if successfully deleted. False otherwise.
	 */
	private boolean deleteResultsHDFS(String pName) {
		boolean result = false;
		// TODO - delete the resulting contents and directory 'jobName'
		String fileName = hdfsHome + jobName;
		try {
			result = hdfs.deleteFile(fileName);
		} catch (IOException e) {
			System.err.printf("\nError: %s", e.getMessage());
		}
		return result;
	}

	/**
	 * Actually executes the command from the given command line
	 * 
	 * @param pCmdLine
	 */
	private void runCommand(String pCmdLine) throws Exception {
		// ProcessBuilder pb = new ProcessBuilder("bash", "-c", "ls");
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", pCmdLine);
		pb.directory(new File("/home/gordon/hadoop-s1.0.1"));
		pb.redirectErrorStream(true);

		StringBuilder response = new StringBuilder();

		BufferedReader br = null;
		try {
			Process shell = pb.start();
			InputStream shellIn = shell.getInputStream();
			response.append(convertStreamToStr(shellIn));
			int shellExitStatus = shell.waitFor();
			System.out.printf("\n\n\tExit Status " + shellExitStatus + "\n");

			// System.out.printf(response.toString());
			shellIn.close();

		} catch (IOException e) {
			throw new IOException(String.format("ERROR in runCommand(): %s",
					e.getMessage()));
		}
	}

	/**
	 * Read the contents of a text file into the buffer, and return the string.
	 * 
	 * @param filename
	 * @return String of the contents of the buffer.
	 */
	private String readFile(String filename) throws IOException {
		StringBuilder buffer = new StringBuilder();

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = "";
		String ls = "\n";
		System.out.printf("\n *** Reading the File: \n");
		while ((line = reader.readLine()) != null) {
			System.out.printf("%s\n", line);
			buffer.append(line);
			buffer.append(ls);
		}
		reader.close();
		return buffer.toString();
	}
}
