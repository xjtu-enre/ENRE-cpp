package demo.util;


import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class FileUtil {
	public static String uniqFilePath(String filePath) {
		try {
			File f = new File(filePath);
			filePath = f.getCanonicalPath();
		} catch (IOException e) {
		}
		return filePath;
	}

	public static boolean existFile(String path) {
		return new File(path).exists();
	}

	public static boolean isDirectory(String path) {
		return new File(path).isDirectory();
	}

	public static String getLocatedDir(String filepath) {
		File file = new File(filepath);
		if (!file.exists()) return null;
		return file.getParent();
	}

	public static String getShortFileName(String path) {
		return new File(path).getName();
	}
	
	public static String uniformPath(String path) {
		String[] paths = path.split("[/|\\\\]");
		StringBuilder sb = new StringBuilder();
		Stack<String> pathStack = new Stack<>();
		for (int i=0;i<paths.length;i++) {
			String s = paths[i];
			if (s.equals(".")) continue;
			if (s.equals("..") && !pathStack.empty()) {
				pathStack.pop();
				continue;
			}
			pathStack.push(s);
		}
		
		for (int i=0;i<pathStack.size();i++) {
			sb.append(pathStack.get(i));
			if (i<pathStack.size()-1)
				sb.append(File.separator);
		}
		return sb.toString();
	}
}
