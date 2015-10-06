package gram21.synchronizer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Main class of the Sync project. This controls the whole process
 */
public class Sync extends Thread {

	private String sourceA;
	private String sourceB;

	public Sync(String sourceA, String sourceB) {
		this.sourceA = sourceA;
		this.sourceB = sourceB;
	}

	/**
	 * starts the sync process by checking the input and then initiate the main
	 * method
	 * 
	 * @param sourceA
	 *            First Source
	 * @param sourceB
	 *            Second Source
	 */
	@Override
	public void run() {
		if (this.sourceA == null || this.sourceB == null) {
			SyncAPI.error("At least one invalid input directory!");
			return;
		}
		
		Source srcA = null;
		Source srcB = null;

		// Check if sources already exists, then use them. Else create sources
		if (SyncAPI.getSourceA() != null && this.sourceA.equals(SyncAPI.getSourceA().getSourcePathString())) {
			srcA = SyncAPI.getSourceA();
		} else {
			File srcAFile = new File(this.sourceA);
			try {
				if (!srcAFile.isDirectory()) {
					SyncAPI.error("At least one invalid input directory!");
					return;
				}
			} catch (SecurityException se) {
				SyncAPI.error("At least one invalid input directory!");
				return;
			}
			srcA = new Source(srcAFile);
		}

		if (SyncAPI.getSourceB() != null && this.sourceB.equals(SyncAPI.getSourceB().getSourcePathString())) {
			srcB = SyncAPI.getSourceB();
		} else {
			File srcBFile = new File(this.sourceB);
			try {
				if (!srcBFile.isDirectory()) {
					SyncAPI.error("At least one invalid input directory!");
					return;
				}
			} catch (SecurityException se) {
				SyncAPI.error("At least one invalid input directory!");
				return;
			}
			srcB = new Source(srcBFile);
		}

		// Start the whole copy process
		copyMissingFiles(srcA, srcB);

		// Set progressBar invisible again TODO: put somewhere else?
		SyncAPI.getProgressBar().setVisible(false);
	}

	/**
	 * main method that calculates the missing files for each source and then
	 * transfers them
	 * 
	 * @param srcA
	 *            First Source
	 * @param srcB
	 *            Second Source
	 */
	private static void copyMissingFiles(Source srcA, Source srcB) {
		if (srcA == null || srcB == null) {
			SyncAPI.error("Error starting copy process. Check Sources!");
			return;
		}

		SyncAPI.updateStatus("Calculating combined file list");
		HashSet<String> combFiles = getCombinedFiles(srcA, srcB);
		
		// Possibly multi-thread the following by handling A and B in own Threads

		// get missing files
		SyncAPI.updateStatus("Calculating missing files for Source A");
		HashSet<String> missingA = getMissingFiles(srcA, combFiles);
		SyncAPI.updateStatus("Calculating missing files for Source B");
		HashSet<String> missingB = getMissingFiles(srcB, combFiles);
		
		// copy
		SyncAPI.updateStatus("Copying files from Source A to B");
		copyFilesFromTo(srcA, srcB, missingB);
		SyncAPI.updateStatus("Copying files from Source B to A");
		copyFilesFromTo(srcB, srcA, missingA);
		SyncAPI.updateStatus("Finished Synchronizing");
	}
	
	/**
	 * Copy all the provided missing files - provided with a HashSet containing
	 * the relative (from the source onwards) paths
	 * 
	 * @param from
	 *            Origin "base" of the missing files
	 * @param to
	 *            Target "base" the files should be copied to
	 * @param missing
	 *            Strings containing relative paths of the files, that should be
	 *            copied
	 */
	private static void copyFilesFromTo(Source from, Source to, HashSet<String> missing) {
		String pathFrom = from.getSourceFile().getAbsolutePath();
		String pathTo = to.getSourceFile().getAbsolutePath();
		
		double counter = 1;
		int amount = missing.size();
		for (String file : missing) {
			updateProgress(counter++ / amount);
			File fileToCopyFrom = new File(pathFrom + SyncAPI.getSeparator() + file);
			File fileToCopyTo = new File(pathTo + SyncAPI.getSeparator() + file);
			try {
				//System.out.println("Copying the following file " + fileToCopyFrom + " to " + fileToCopyTo);
				if (!fileToCopyTo.exists()) {
					String parent = fileToCopyTo.getParent();
					if (parent != null) 
						new File(parent).mkdirs();
				}
				copyFile(fileToCopyFrom, fileToCopyTo);
			} catch (IOException e) {
				SyncAPI.error("Error while copying the following file " + fileToCopyFrom + " to " + fileToCopyTo);
				e.printStackTrace();
			}
		}	
	}
	
	/**
	 * Calculates the files, that the Source src is missing in comparison to the
	 * provided HashSet containing the Strings with all files (relative to the
	 * source)
	 * 
	 * @param src
	 *            Source, that should be checked for missing files
	 * @param combinedFiles
	 *            HashSet of Strings that contains the relative paths (from the
	 *            source) of ALL files
	 * @return HashSet containing Strings with the relative paths of the missing
	 *         files
	 */
	private static HashSet<String> getMissingFiles(Source src, HashSet<String> combinedFiles) {
		HashSet<String> files = src.getFiles();
		return combinedFiles.stream().filter(file -> !files.contains(file))
				.collect(Collectors.toCollection(HashSet::new));
	}
    
	/**
	 * Calculates the Set of all files (represented by Strings with the relative
	 * Path to the source)
	 * 
	 * @param srcA
	 *            First Source
	 * @param srcB
	 *            Second Source
	 * @return combined HashSet of the Strings with (relative) paths to the
	 *         Files
	 */
	private static HashSet<String> getCombinedFiles(Source srcA, Source srcB) {
		HashSet<String> combFiles = new HashSet<String>();
		combFiles.addAll(srcA.getFiles());
		combFiles.addAll(srcB.getFiles());
		return combFiles;
	}
	
	/**
	 * Copies a File from the source to the destination
	 * 
	 * @param source
	 *            Source of the file copy
	 * @param dest
	 *            target destination to copy the file to
	 * @throws IOException .
	 */
	private static void copyFile(File source, File dest) throws IOException {
    	Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
    }
	
	/**
	 * Method used for printing a Progress Bar
	 * 
	 * @param progressPercentage
	 */
	private static void updateProgress(double progressPercentage) {
		SyncAPI.updateProgress(progressPercentage);
	}
	
}
