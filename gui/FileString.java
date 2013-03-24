package gui;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class FileString {	
	public FileString() {}
	
	public static String filetoString(String filePath){
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader;
		try {
			reader = new BufferedReader(
			new FileReader(filePath));
		
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		}
		return fileData.toString();
	}
}
