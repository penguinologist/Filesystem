package test;

import java.util.ArrayList;
import java.util.List;

/**
 * FileSystem class
 * 
 * @author Jeroen
 *
 */
public class FileSystem {

	// class variable - root of all directories
	private DirectoryObjects directory;

	/**
	 * base constructor
	 */
	public FileSystem() {
		directory = new DirectoryObjects("/");
	}

	/**
	 * retrieves the content of a directory including both subdirectories as
	 * well as files within that directory
	 * 
	 * @param path
	 *            the path where we're looking.
	 * @return a list of files and subdirectories
	 */
	public List<String> ls(String path) {
		return getDirectory(path, directory).stringy();
	}

	/**
	 * gets the directory for the path provided. Made it a private method for
	 * other functions to call for future reference.
	 * 
	 * @param path
	 *            assumed to always be a correct path.
	 * @param current
	 *            current directory object - used to traverse
	 * @return the directory object where we finish
	 */
	private DirectoryObjects getDirectory(String path, DirectoryObjects current) {
		if (path.lastIndexOf("/") == 0) {
			return current;// done
		} else {
			// parse directories...
			ArrayList<String> direcs = new ArrayList<>();
			String temp = path;
			boolean cont = true;
			temp = temp.substring(1);
			while (temp.length() > 1 && cont) {

				// add the substring to the list of directories that need to be
				// checked and/or created
				direcs.add(temp.substring(0, temp.indexOf("/")));
				// if there are more "/"s left, repeat
				if (temp.contains("/")) {
					temp = temp.substring(temp.indexOf("/") + 1);
				}
				// else break out of the loop
				else {
					cont = false;
				}
			}

			// do one last check to see if there are any extra characters left
			if (temp.length() > 0) {
				// there is still something left
				direcs.add(temp);
			}

			// go through each directory, checking if it exists. if not, create
			// the directory and repeat.
			DirectoryObjects l = current;

			for (String p : direcs) {

				if (l.getDirectoryByName(p) != null) {
					l = l.getDirectoryByName(p);
				} else {
					// instead of creating a new directory, we just throw an
					// exception
					return l;
				}
			}
			return l;

		}

	}

	/**
	 * creates a new directory given a path.
	 * 
	 * @param dirPath
	 *            the path that needs to be created.
	 */
	public void mkdirP(String dirPath) {

		// check if you need to go anywhere further
		if (dirPath.lastIndexOf("/") == 0) {
			return;// done
		} else {
			// parse directories...
			ArrayList<String> direcs = new ArrayList<>();
			String temp = dirPath;
			boolean cont = true;
			temp = temp.substring(1);
			while (temp.length() > 1 && cont) {

				// add the substring to the list of directories that need to be
				// checked and/or created
				direcs.add(temp.substring(0, temp.indexOf("/")));
				// if there are more "/"s left, repeat
				if (temp.contains("/")) {
					temp = temp.substring(temp.indexOf("/") + 1);
				}
				// else break out of the loop
				else {
					cont = false;
				}
			}

			// do one last check to see if there are any extra characters left
			if (temp.length() > 0) {
				// there is still something left
				direcs.add(temp);
			}

			// go through each directory, checking if it exists. if not, create
			// the directory and repeat.
			DirectoryObjects current = directory;

			for (String p : direcs) {

				if (current.getDirectoryByName(p) != null) {
					current = current.getDirectoryByName(p);
				} else {
					current.getSubdirectories().add(new DirectoryObjects(p));
					current = current.getDirectoryByName(p);
				}
			}

		}

	}

	/**
	 * add a new file with content. Ignore duplicates.
	 * 
	 * @param filePath
	 *            the path where the file needs to be added.
	 * @param content
	 *            the content of the file that needs to be created.
	 */
	public void addFileWithContent(String filePath, String content) {
		// get the path
		String path = filePath.substring(0, filePath.lastIndexOf("/"));
		// get the file names
		String name = filePath.substring(filePath.lastIndexOf("/") + 1);
		// add content to the file's location with the name provided
		getDirectory(path, directory).getFiles().add(new FileObjects(name, content));
	}

	/**
	 * retrieves the content of a file for the provided file path.
	 * 
	 * @param filePath
	 *            the path of the file to be searched for.
	 * @return the file's contents, or an empty string if nothing is found.
	 */
	public String getFileContent(String filePath) {
		String content = "";
		// get the path
		String path = filePath.substring(0, filePath.lastIndexOf("/"));
		// get the file's name
		String name = filePath.substring(filePath.lastIndexOf("/") + 1);
		// iterate through all the files to see if we can find it...
		for (FileObjects t : getDirectory(path, directory).getFiles()) {
			if (name.equals(t.getName())) {
				content = t.getContent();
			}
		}
		// return the content of the file, or just an empty string if it's
		// empty.
		return content;

	}

	// -----------------------------------------------

	public static void main(String[] args) {
		// assumption: all path starts with / and do not end with /
		FileSystem fs = new FileSystem();

		// should print []
		System.out.println(fs.ls("/"));

		fs.mkdirP("/a/b/c");
		fs.addFileWithContent("/a/b/c/d", "hello world");
		//
		// should print [a]
		System.out.println(fs.ls("/"));
		//
		// should print [d]
		System.out.println(fs.ls("/a/b/c"));

		// should print [d]
		System.out.println(fs.ls("/a/b/c/d"));

		// should print hello world
		System.out.println(fs.getFileContent("/a/b/c/d"));
	}

	/**
	 * class that stores all the files along with content
	 */
	class FileObjects {
		private String name;
		private String content;

		FileObjects(String name, String content) {
			this.name = name;
			this.content = content;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the content
		 */
		public String getContent() {
			return content;
		}

		/**
		 * @param content
		 *            the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}

	}

	/**
	 * class that stores all the directories.
	 */
	class DirectoryObjects {

		private List<DirectoryObjects> subdirectories;
		private List<FileObjects> files;
		private String name;

		/**
		 * constructor
		 * 
		 * @param name
		 *            of the directory
		 */
		DirectoryObjects(String name) {
			subdirectories = new ArrayList<>();
			files = new ArrayList<>();
			this.name = name;

		}

		/**
		 * @return the subdirectories
		 */
		public List<DirectoryObjects> getSubdirectories() {
			return subdirectories;
		}

		/**
		 * @param subdirectories
		 *            the subdirectories to set
		 */
		public void setSubdirectories(List<DirectoryObjects> subdirectories) {
			this.subdirectories = subdirectories;
		}

		/**
		 * retrieves all the file objects
		 * 
		 * @return a list of file objects
		 */
		public List<FileObjects> getFiles() {

			return files;
		}

		/**
		 * This method retrieves the file names in the current directory.
		 * 
		 * @return file names as a list of strings.
		 */
		public List<String> getFileNames() {
			List<String> names = new ArrayList<>();
			for (FileObjects t : files) {
				names.add(t.getName());
			}

			return names;
		}

		/**
		 * @param files
		 *            the files to set
		 */
		public void setFiles(List<FileObjects> files) {
			this.files = files;
		}

		/**
		 * This method creates a list of strings for the names of all
		 * directories and files in the filesystem
		 * 
		 * @return list of file system content.
		 */
		public List<String> stringy() {
			List<String> temp = new ArrayList<>();
			// add all the file names
			temp.addAll(getFileNames());
			// add all the directories [indicated by a "-"]
			for (DirectoryObjects t : subdirectories) {
				temp.add(t.name);
			}

			return temp;
		}

		/**
		 * retrieves directories by name. This method was created to remove
		 * repetitive calls
		 * 
		 * @param nam
		 *            the name of the object searched for.
		 * @return the object if found, else null.
		 */
		public DirectoryObjects getDirectoryByName(String nam) {
			for (DirectoryObjects l : subdirectories) {
				if (l.name.equals(nam)) {
					return l;
				}
			}
			return null;
		}

	}

}
