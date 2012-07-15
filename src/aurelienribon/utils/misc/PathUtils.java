package aurelienribon.utils.misc;

import java.io.File;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

/**
 * Collection of utility methods for file and directory path manipulations.
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class PathUtils {
	/**
	 * Removes every '"' character before and after the path, if any.
	 */
	public static String trim(String path) {
		path = path.trim();
		while (path.startsWith("\"") && path.endsWith("\""))
			path = path.substring(1, path.length()-1);
		return path;
	}

	/**
	 * Gets the relative path from one file to another.
	 */
	public static String getRelativePath(String targetPath, String basePath) {
		if (targetPath == null) throw new NullPointerException("targetPath");
		if (basePath == null) throw new NullPointerException("basePath");

		targetPath = trim(targetPath);
		basePath = trim(basePath);

		if (basePath.equals("") || targetPath.equals("")) return targetPath;
		if (basePath.equals(targetPath)) return "";

		String sep = File.separator;
		String norTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
		String norBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

		if (sep.equals("/")) {
			norTargetPath = FilenameUtils.separatorsToUnix(norTargetPath);
			norBasePath = FilenameUtils.separatorsToUnix(norBasePath);
		} else if (sep.equals("\\")) {
			norTargetPath = FilenameUtils.separatorsToWindows(norTargetPath);
			norBasePath = FilenameUtils.separatorsToWindows(norBasePath);
		} else {
			throw new IllegalArgumentException("Unrecognised dir separator '" + sep + "'");
		}

		String[] base = norBasePath.split(Pattern.quote(sep));
		String[] target = norTargetPath.split(Pattern.quote(sep));
		StringBuilder commonSb = new StringBuilder();

		int idx = 0;

		while (idx < target.length && idx < base.length && target[idx].equals(base[idx])) {
			commonSb.append(target[idx]).append(sep);
			idx += 1;
		}

		if (idx == 0) return targetPath;

		boolean baseIsFile = true;
		File baseResource = new File(norBasePath);

		if (baseResource.exists()) {
			baseIsFile = baseResource.isFile();
		} else if (basePath.endsWith(sep)) {
			baseIsFile = false;
		}

		StringBuilder relativeSb = new StringBuilder();

		if (base.length != idx) {
			int numDirsUp = baseIsFile ? base.length - idx - 1 : base.length - idx;
			for (int i = 0; i < numDirsUp; i++) {
				relativeSb.append("..").append(sep);
			}
		}

		relativeSb.append(norTargetPath.substring(commonSb.length()));
		return relativeSb.toString();
	}
}
