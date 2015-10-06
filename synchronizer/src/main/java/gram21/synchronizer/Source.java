package gram21.synchronizer;

import java.io.File;
import java.net.URI;
import java.util.HashSet;

/**
 * This class represents a Source folder
 *
 */
public class Source {
	
	// TODO: calculate files and whole size

	private File sourceFile;
	private HashSet<String> files;
	
	private long totalSize = 0;
	private long amountOfFiles = 0;

	public Source(File sourceFile) {
		if (sourceFile == null || !sourceFile.exists()) {
			throw new IllegalArgumentException("Invalid source file.");
		}
		this.sourceFile = sourceFile;
		this.files = generateFileSet(sourceFile);
	}
	
	private HashSet<String> generateFileSet(File sourceFile) {
		HashSet<String> retSet = new HashSet<String>();
		return getAllFileStrings(sourceFile, retSet, sourceFile.toURI());
	}
	
	private HashSet<String> getAllFileStrings(File file, HashSet<String> fileStrings, URI base) {
		for (File f: file.listFiles()) {
			if (f.isFile()) {
				fileStrings.add(base.relativize(f.toURI()).getPath());
				this.totalSize += f.length();
				this.amountOfFiles++;
			} else if (f.isDirectory()) {
				fileStrings = getAllFileStrings(f, fileStrings, base);
			}
		}
		return fileStrings;
	}

	public String getTotalSizeInMBString() {
		return String.format("%.2f MB", (this.totalSize / (1024d * 1024d)));
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public String getSourcePathString() {
		return sourceFile.getAbsolutePath();
	}

	public HashSet<String> getFiles() {
		return files;
	}

	/**
	 * @return the totalSize
	 */
	public long getTotalSize() {
		return totalSize;
	}

	/**
	 * @return the amountOfFiles
	 */
	public long getAmountOfFiles() {
		return amountOfFiles;
	}

}
