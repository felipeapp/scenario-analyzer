package br.ufrn.dimap.ttracker.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

public class FileUtil {
	
	public static void saveObjectToFile(Object obj, String fileDirectory, String fileName, String extension) {
		File dir = new File(fileDirectory);
		if(!dir.exists())
			dir.mkdir();
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(fileDirectory+"/"+fileName+"."+extension);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(obj);
			objectOutputStream.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public static Object loadObjectFromFile(String fileDirectory, String fileName, String extension) {
		try {
			File file = new File(fileDirectory+"/"+fileName+"."+extension);
			if(!file.exists())
				return null;
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			Object object = objectInputStream.readObject();
			objectInputStream.close();
			return object;
		} catch(Exception e) {
			return null;
		}
	}
	
	public static void saveTextToFile(String text, String fileDirectory, String fileName, String extension){
		fileDirectory = fileDirectory.replace('\\', '/');
		File dir = new File(fileDirectory);
		if (!dir.exists())
			dir.mkdirs();
		try {
			FileWriter fw = new FileWriter(fileDirectory+"/"+fileName+"."+extension);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String loadTextFromFile(File file){
		String content = null;
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getProjectFolderByResource(Class<?> aClass) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(getBuildFolderByResource(aClass)+"/projectFolder.txt"));
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "C:/";
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static Boolean getWasProjectBuildedByResource(Class<?> aClass, String projectName) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(getBuildFolderByResource(aClass)+"/Was"+projectName+"Builded.txt"));
			return (br.readLine().equals("1") ? true : false);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static Integer getTestClassesSizeByResource(Class<?> aClass) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(getBuildFolderByResource(aClass)+"/testClassesSize.txt"));
			return Integer.valueOf(br.readLine());
		} catch (IOException e) {
			return -1;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static String getTestCoverageMappingNameByResource(Class<?> aClass) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(getBuildFolderByResource(aClass)+"/testCoverageMappingName.txt"));
			return br.readLine();
		} catch (IOException e) {
			return "AllTests";
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static String getResultFolderByResource(Class<?> aClass) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(getBuildFolderByResource(aClass)+"/resultFolder.txt"));
			return br.readLine();
		} catch (IOException e) {
			return "D:/result";
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static String getProjectNameByResource(Class<?> aClass) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(getBuildFolderByResource(aClass)+"/projectName.txt"));
			return br.readLine();
		} catch (IOException e) {
			return "UnknowProjectName";
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static String getBuildFolderByResource(Class<?> aClass) {
		String buildFolder = aClass.getResource("").toString();
		return buildFolder.substring(buildFolder.indexOf("/")+1,(buildFolder.length()-aClass.getPackage().getName().length()-2));
	}
	
}
