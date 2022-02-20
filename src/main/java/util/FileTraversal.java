package util;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Recursively visit every file in the given root path using the 
 * extended IFileVisitor
 *
 */
public class FileTraversal {
	/**
	 * The visitor interface 
	 * Detail operation should be implemented here
	 */
	public interface IFileVisitor {
		void visit(File file) throws Exception;
	}
	
	HashMap<String,Integer> fileList = new HashMap<String,Integer>();
	IFileVisitor visitor;
	private ArrayList<String> extensionFilters = new ArrayList<>();
	boolean shouldVisitDirectory = false;
	boolean shouldVisitFile = true;
	public FileTraversal(IFileVisitor visitor){
		this.visitor = visitor;
	}

	public FileTraversal(IFileVisitor visitor,boolean shouldVisitDirectory,boolean shouldVisitFile){
		this.visitor = visitor;
		this.shouldVisitDirectory = shouldVisitDirectory;
		this.shouldVisitFile = shouldVisitFile;

	}
	public void getFileList(String path) throws Exception{
		File dir = new File(path);
		getFileList(dir);
	}
	public void getFileList(File path) throws Exception{
		File[] files = path.listFiles();
		if (files == null)
			return;
			
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				getFileList(files[i]);
				if (shouldVisitDirectory) {
					fileList.put(files[i].getAbsolutePath(),0);
				}
			} else {
				if (shouldVisitFile) {
					fileList.put(files[i].getAbsolutePath(),0);
				}
			}
		}		
	}
	
	public HashMap<String, Integer> getfile(){
		return fileList;
	}
	
	public void travers(String path) throws Exception {
		File dir = new File(path);
		travers(dir);
	}

	public void travers(File root) throws Exception {
		File[] files = root.listFiles();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				travers(files[i]);
				if (shouldVisitDirectory) {
					invokeVisitor(files[i]);
				}
			} else {
				if (shouldVisitFile) {
					invokeVisitor(files[i]);
				}
			}
		}		
	}

	private void invokeVisitor(File f) throws Exception {
		if (extensionFilters.size()==0) {
			visitor.visit(f);
		}else {
			for (String ext:extensionFilters) {
				if (f.getAbsolutePath().toLowerCase().endsWith(ext.toLowerCase())) {
					visitor.visit(f);
				}
			}
		}
	}

	public FileTraversal extensionFilter(String ext) {
		this.extensionFilters.add(ext.toLowerCase());
		return this;
	}

	public void extensionFilter(String[] fileSuffixes) {
		for (String fileSuffix:fileSuffixes){
			extensionFilter(fileSuffix);
		}
	}
}