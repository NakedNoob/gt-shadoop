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

/**
 * Query.java
 * 
 * Description: Query performs all of the tasks required to formulate a query,
 * create a Process to run the query, save the results to a local filesystem,
 * and return the pathname to the result file.
 * 
 * 
 */

public class Query {

	/**
	 * User defined name of each job. Output will be sent to a directory by this
	 * name. Each job must be unique, and the job directory deleted upon
	 * returning the values.
	 */
	private String jobName;

	/**
	 * HDFS Utility to perform basic duties in the hadoop file system
	 */
	private HDFSadmin hdfs = null;

	/**
	 * Hadoop's Path
	 */
	Path hadoopPath = null;

	/**
	 * StringBuilder to contain the data from the output of the process
	 */
	private final StringBuilder bufferOut = new StringBuilder();

	/**
	 * Configuration file for usernames and pathnames of the current system.
	 */
	private HConfig config = null;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public Query() throws IOException {
		config = new HConfig();
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
	 * @param Name
	 *            of the job to be tested.
	 * @return True if directory doesn't exist. False if the directory already
	 *         exists.
	 */
	private boolean nameValid(String pName) {
		boolean result = false;

		try {
			if (!hdfs.ifExists(new Path(config.getHdfsHomeDir() + pName))) {
				result = true;
				jobName = pName;
				hadoopPath = new Path(config.getHdfsHomeDir() + pName);
			}
		} catch (IOException e) {
			System.err.printf("\nDirectory '%s' exists.  Choose another name.");
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Runs the range query job using four integers as bounding box parameters
	 * for the requested data.
	 * 
	 * @param a
	 *            - box coordinate
	 * @param b
	 *            - box coordinate
	 * @param c
	 *            - box coordinate
	 * @param d
	 *            - box coordinate
	 * @return Full path of the filename containing the results
	 * @throws InterruptedException
	 */
	public String runRangeQuery(int a, int b, int c, int d)
			throws InterruptedException {
		String result = "";
		jobName = generateName();

		// form the string command
		StringBuilder cmd = new StringBuilder();

		cmd.append("bin/hadoop jar hadoop-operations-*.jar ");
		cmd.append(String.format("rangequery points %s ", jobName));
		cmd.append(String.format("rect:%d,%d,%d,%d ", a, b, c, d));
		cmd.append("shape:point -overwrite");

		// run the command
		try {
			runCommand(cmd.toString());
			// get the results
			result = getResults();

		} catch (Exception e) {
			System.err.printf("\nError: %s", e.getMessage());
			result = null;
		}
		// delete the hdfs results directory
		if (!deleteResultsHDFS(config.getHdfsHomeDir() + jobName)) {
			System.out.printf("\nHDFS Directory not deleted.");
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
	public String runWordCount(String pTxtFile) {
		String result = "";
		StringBuilder cmd = new StringBuilder();

		String hdfsPathName = config.getHdfsDataDir() + pTxtFile;

		// Check to see if the txt file exists in the ext file system
		File input = new File(config.getLocalDataSource() + pTxtFile);
		Path outPath = new Path(hdfsPathName);

		if (!input.exists()) {
			System.err.printf("\n'%s' does not exist", input.getAbsolutePath());
			return null;
		} else {
			System.out.printf("\n'%s' exists, copying to hdfs...",
					input.getAbsolutePath());
			// file exists, now transfer it to the hdfs data directory
			try {
				if (!hdfs.ifExists(new Path(config.getHdfsDataDir()))) {
					System.out.printf("\n *** '%s' does NOT exist in hdfs\n",
							config.getHdfsDataDir());

					if (hdfs.mkdir(config.getHdfsDataDir())) {
						System.err.printf("\n'%s' was not created",
								config.getHdfsDataDir());
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

		if (runCommand(cmd.toString()) == 0) {
			result = config.getOutputDir() + "/" + jobName;
		}

		System.out.printf("\nResults: \n%s", getResults());

		if (!deleteResultsHDFS(config.getHdfsHomeDir() + jobName)) {
			System.out.printf("\nHDFS Directory not deleted.");
		}

		return result;
	}

	/**
	 * Get the results file, copies it to /tmp/gt-shadoop and returns the
	 * pathname.
	 * 
	 * @return Full path and filename of the results file
	 */
	private String getResults() {
		File tmpDir = new File(config.getOutputDir());
		// Make sure local temporary directory exists
		if (!tmpDir.exists()) {
			// create if it doesn't exist
			tmpDir.mkdir();
		}

		// copy results file from hdfs to local

		// TODO - check if there are multiple files as the result
		// Not needed at this time
		String fileName = tmpDir.getAbsolutePath() + "/" + jobName + ".txt";
		try {
			hdfs.copyToLocal(config.getHdfsHomeDir() + jobName + "/part-00000",
					fileName);

		} catch (IOException e) {
			// set filename to null
			fileName = null;
		}

		return fileName;
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

		name.append(config.getDefaultOutputName());
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

		String fileName = config.getHdfsHomeDir() + jobName;
		try {
			result = hdfs.deleteFile(fileName);
		} catch (IOException e) {
			System.err.printf("\nError: %s", e.getMessage());
		}
		return result;
	}

	/**
	 * Executes the command by creating a Process, and waiting for its
	 * completion.
	 * 
	 * @param Command
	 *            line to be run in the bash shell.
	 * @return Exit status of the process. Return of '0' indicates successful.
	 *         Return of '-1' indicates an exception has been thrown. Non-zero
	 *         positive number means a problem occurred during execution of the
	 *         shell.
	 */
	private int runCommand(String pCmdLine) {
		int result = -1;
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", pCmdLine);
		pb.directory(new File(config.getLocalHadoopDir()));
		pb.redirectErrorStream(true);

		StringBuilder response = new StringBuilder();

		try {
			Process shell = pb.start();
			InputStream shellIn = shell.getInputStream();
			response.append(convertStreamToStr(shellIn));
			result = shell.waitFor();

			shellIn.close();

		} catch (IOException e) {
			// do nothing
		} catch (InterruptedException e) {
			// do nothing
		}
		return result;
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
