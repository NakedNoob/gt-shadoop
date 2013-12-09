package org.geotools.data.shadoop.query;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;

public class HDFSadmin {

	HConfig config = new HConfig();
	/**
	 * Empty filename created upon completion of a hadoop job. It is present in
	 * the output directory.
	 */
	private final String SUCCESS_FLAG = "_SUCCESS";

	/**
	 * Path to the core-site.xml
	 */
	private final String coreSiteCfg = config.getLocalHadoopDir()
			+ "/conf/core-site.xml";

	/**
	 * Path to the hdfs-site.xml
	 */
	private final String hdfsSiteCfg = config.getLocalHadoopDir()
			+ "/conf/hdfs-site.xml";

	/**
	 * Path to the mapred-site.xml
	 */
	private final String mapredSiteCfg = config.getLocalHadoopDir()
			+ "/hadoop-s1.0.1/conf/mapred-site.xml";

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public HDFSadmin() throws IOException {
		// nothing
	}

	/**
	 * Print usage statistics
	 */
	public static void printUsage() {
		System.out
				.println("Usage: hdfsclient add" + "<local_path> <hdfs_path>");
		System.out.println("Usage: hdfsclient read" + "<hdfs_path>");
		System.out.println("Usage: hdfsclient delete" + "<hdfs_path>");
		System.out.println("Usage: hdfsclient mkdir" + "<hdfs_path>");
		System.out.println("Usage: hdfsclient copyfromlocal"
				+ "<local_path> <hdfs_path>");
		System.out.println("Usage: hdfsclient copytolocal"
				+ " <hdfs_path> <local_path> ");
		System.out
				.println("Usage: hdfsclient modificationtime" + "<hdfs_path>");
		System.out.println("Usage: hdfsclient getblocklocations"
				+ "<hdfs_path>");
		System.out.println("Usage: hdfsclient gethostnames");
	}

	/**
	 * Checks to see if the current file exists
	 * 
	 * @param Path
	 *            of the file you are checking
	 * @return True if the Path exists, false otherwise
	 * @throws IOException
	 */
	public boolean ifExists(Path source) throws IOException {
		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);

		boolean isExists = hdfs.exists(source);
		return isExists;
	}

	/**
	 * Outputs the hostnames in the file system
	 * 
	 * @throws IOException
	 */
	public void getHostnames() throws IOException {

		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem fs = FileSystem.get(config);
		DistributedFileSystem hdfs = (DistributedFileSystem) fs;
		DatanodeInfo[] dataNodeStats = hdfs.getDataNodeStats();

		String[] names = new String[dataNodeStats.length];
		for (int i = 0; i < dataNodeStats.length; i++) {
			names[i] = dataNodeStats[i].getHostName();
			System.out.println((dataNodeStats[i].getHostName()));
		}
	}

	/**
	 * Output to display the block locations
	 * 
	 * @param pathway
	 *            of the source
	 * @throws IOException
	 */
	public void getBlockLocations(String source) throws IOException {
		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);

		Path srcPath = new Path(source);

		// Check if the file already exists
		if (!(ifExists(srcPath))) {
			System.out.println("No such destination " + srcPath);
			return;
		}
		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1,
				source.length());

		FileStatus fileStatus = hdfs.getFileStatus(srcPath);

		BlockLocation[] blkLocations = hdfs.getFileBlockLocations(fileStatus,
				0, fileStatus.getLen());
		int blkCount = blkLocations.length;

		System.out.println("File :" + filename + "stored at:");
		for (int i = 0; i < blkCount; i++) {
			String[] hosts = blkLocations[i].getHosts();
			System.out.format("Host %d: %s %n", i, hosts);
		}

	}

	/**
	 * Output to display the modification time of the given file
	 * 
	 * @param Filename
	 * @throws IOException
	 */
	public void getModificationTime(String source) throws IOException {
		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);
		Path srcPath = new Path(source);

		// Check if the file already exists
		if (!(hdfs.exists(srcPath))) {
			System.out.println("No such destination " + srcPath);
			return;
		}
		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1,
				source.length());

		FileStatus fileStatus = hdfs.getFileStatus(srcPath);
		long modificationTime = fileStatus.getModificationTime();

		System.out.format("File %s; Modification time : %0.2f %n", filename,
				modificationTime);

	}

	/**
	 * Copy from the local filesystem to the hdfs filesystem
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public void copyFromLocal(String source, String dest) throws IOException {
		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);

		Path srcPath = new Path(source);

		Path dstPath = new Path(dest);
		// Check if the file already exists
		if (!(hdfs.exists(dstPath))) {
			System.out.println("No such destination " + dstPath);
			return;
		}

		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1,
				source.length());

		try {
			hdfs.copyFromLocalFile(srcPath, dstPath);
			System.out.println("File " + filename + "copied to " + dest);
		} catch (Exception e) {
			System.err.println("Exception caught! :" + e);
			System.exit(1);
		} finally {
			hdfs.close();
		}
	}

	/**
	 * Copy a file from hdfs to the local filesystem
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public void copyToLocal(String source, String dest) throws IOException {

		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);
		Path srcPath = new Path(source);

		Path dstPath = new Path(dest);
		// Check if the file already exists
		if (!(hdfs.exists(srcPath))) {
			System.out.println("No such destination " + srcPath);
			return;
		}

		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1,
				source.length());

		try {
			hdfs.copyToLocalFile(srcPath, dstPath);
			System.out.println("File " + filename + "copied to " + dest);
		} catch (Exception e) {
			System.err.println("Exception caught! :" + e);
			// System.exit(1);
		} finally {
			hdfs.close();
		}
	}

	/**
	 * Rename a file currently in the hdfs.
	 * 
	 * @param fromthis
	 * @param tothis
	 * @throws IOException
	 */
	public void renameFile(String fromthis, String tothis) throws IOException {
		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);
		Path fromPath = new Path(fromthis);
		Path toPath = new Path(tothis);

		if (!(hdfs.exists(fromPath))) {
			System.out.println("No such destination " + fromPath);
			return;
		}

		if (hdfs.exists(toPath)) {
			System.out.println("Already exists! " + toPath);
			return;
		}

		try {
			boolean isRenamed = hdfs.rename(fromPath, toPath);
			if (isRenamed) {
				System.out.println("Renamed from " + fromthis + "to " + tothis);
			}
		} catch (Exception e) {
			System.out.println("Exception :" + e);
			System.exit(1);
		} finally {
			hdfs.close();
		}

	}

	/**
	 * Add a new file to the HDFS from local
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public void addFile(String source, String dest) throws IOException {
		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);
		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1,
				source.length());

		// Create the destination path including the filename.
		if (dest.charAt(dest.length() - 1) != '/') {
			dest = dest + "/" + filename;
		} else {
			dest = dest + filename;
		}

		// Check if the file already exists
		Path path = new Path(dest);
		if (hdfs.exists(path)) {
			System.out.println("File " + dest + " already exists");
			return;
		}

		// Create a new file and write data to it.
		FSDataOutputStream out = hdfs.create(path);
		InputStream in = new BufferedInputStream(new FileInputStream(new File(
				source)));

		byte[] b = new byte[1024];
		int numBytes = 0;
		while ((numBytes = in.read(b)) > 0) {
			out.write(b, 0, numBytes);
		}

		// Close all the file descripters
		in.close();
		out.close();
		hdfs.close();
	}

	/**
	 * Read a file and output the contents to the display.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void readFile(String file) throws IOException {
		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);
		Path path = new Path(file);
		if (!hdfs.exists(path)) {
			System.out.println("File " + file + " does not exists");
			return;
		}

		FSDataInputStream in = hdfs.open(path);

		String filename = file.substring(file.lastIndexOf('/') + 1,
				file.length());

		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				new File(filename)));

		byte[] b = new byte[1024];
		int numBytes = 0;
		while ((numBytes = in.read(b)) > 0) {
			out.write(b, 0, numBytes);
		}

		in.close();
		out.close();
		hdfs.close();
	}

	/**
	 * Delete the given file from hdfs
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public boolean deleteFile(String file) throws IOException {
		boolean result = false;
		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);
		Path path = new Path(file);
		if (!hdfs.exists(path)) {
			System.out.println("File " + file + " does not exists");
			return result;
		}

		result = hdfs.delete(new Path(file), true);

		hdfs.close();
		return result;
	}

	/**
	 * Make a directory from the given name
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public boolean mkdir(String dir) throws IOException {
		Configuration config = new Configuration();
		config.addResource(new Path(coreSiteCfg));
		config.addResource(new Path(hdfsSiteCfg));
		config.addResource(new Path(mapredSiteCfg));

		FileSystem hdfs = FileSystem.get(config);

		boolean result = false;
		Path path = new Path(dir);
		if (hdfs.exists(path)) {
			System.out.println("Dir " + dir + " already exists!");
			return result;
		}

		if (hdfs.mkdirs(path)) {
			result = true;
		}

		hdfs.close();
		return result;
	}

	/**
	 * Test to see if the SUCCESS_FLAG has been flown in the output directory.
	 * 
	 * @param pJobName
	 * @return
	 */
	public boolean isSuccess(String pJobName) {
		boolean result = false;
		String success = pJobName + "/" + SUCCESS_FLAG;
		try {
			result = ifExists(new Path(success));
		} catch (IOException e) {
			// do nothing
		}

		return result;
	}
}
