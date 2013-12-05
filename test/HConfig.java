/**
 * HConfig.java
 * 
 * Description: HConfig contains all of the configurable items for the Query
 * class. The default user name, the hdfs directory names, and the local file
 * system hadoop directory.
 * 
 */
public class HConfig {

	/**
	 * Directory name of the current version of hadoop on the system.
	 */
	private final String hdpWorkingDir = "hadoop-s1.0.1p3";

	/**
	 * User name on the hadoop system VM
	 */
	private final String USER = "shadoop";

	/**
	 * Wordcount: Source directory for texts to be submitted to wordcount.
	 */
	private final String localDataSource = "/tmp/text/";

	/**
	 * Wordcount: hdfs directory where text files are copied to, prior to
	 * running the wordcount mapreduce job.
	 */
	private final String HDFS_DATA_DIR = "/user/" + USER + "/data/";

	/**
	 * All Queries: Local file system temp directory, where subfolders hold job
	 * results at their completion.
	 */
	private final String outputTempDir = "/tmp";

	/**
	 * All Queries: Subdirectory under TMP where all query results will be
	 * saved.
	 */
	private final String outputSubDir = "/gt-shadoop";

	/**
	 * All Queries: String - this is the default name the output directory will
	 * be called after results are created. A timestamp will preceed this name.
	 * Example: "../20131205120505_Results" will be the subdirectory containing
	 * query results.
	 */
	private final String DEFAULT_NAME = "_Results";

	/**
	 * HDFS Pathname: Internal hdfs path to the user's directory.
	 */
	private final String hdfsHome = "/user/" + USER + "/";

	/**
	 * Used in reference to the local filesystem
	 */
	private final String hadoopMainDir = "/home/" + USER + "/" + hdpWorkingDir;

	/**
	 * Constructor
	 */
	public HConfig() {
		// Combine some of the separate elements into strings usable by method
		// calls.
	}

	/**
	 * The user's home directory in the hdfs filesystem.
	 * 
	 * @return hdfs path of the user's home directory with a trailing '/'
	 *         Example: /user/joesmith/
	 */
	public String getHdfsHomeDir() {
		return hdfsHome;
	}

	/**
	 * The local filesystem hadoop main directory.
	 * 
	 * @return String of the local hadoop directory Example:
	 *         /home/username/hadoop-s1.x.x
	 */
	public String getLocalHadoopDir() {
		return hadoopMainDir;
	}

	/**
	 * Gets the default output name that is combined with the timestamp to
	 * produce this job's name.
	 * 
	 * @return String of the default job name.
	 */
	public String getDefaultOutputName() {
		return DEFAULT_NAME;
	}

	/**
	 * Get the output directory to which the query results file will be written.
	 * 
	 * @return Output directory where the job results are saved locally.
	 */
	public String getOutputDir() {
		return outputTempDir + outputSubDir;
	}

	/**
	 * The HDFS directory where text files are copied to, prior to running the
	 * wordcount mapreduce job
	 * 
	 * @return Hdfs Data Directory pathname
	 */
	public String getHdfsDataDir() {
		return HDFS_DATA_DIR;
	}

	/**
	 * Get the local directory that contains any text files intended for a
	 * wordcount job.
	 * 
	 * @return pathname to the local data directory, with a trailing '/'
	 *         Example: /tmp/text/
	 */
	public String getLocalDataSource() {
		return localDataSource;
	}
}
