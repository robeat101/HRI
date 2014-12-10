package robotGUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class DataLogger {
	private static final String dataLogBaseName = "HRI_sim_log";
	private static final String dataLogNumbers = "HRI_sim_log_numbers.txt";
	private static DataLogger theDataLogger; //singleton

	
	BufferedWriter logFileWriter;
	int curLogFileNumber;
	public DataLogger(){
		curLogFileNumber = 100;
//		System.out.println("Working Directory = " +  System.getProperty("user.dir"));
		String logNumberFile = "\\\\data\\"+dataLogNumbers;
		try{
			BufferedReader logFileNumberReader = new BufferedReader(new FileReader(logNumberFile));
			String strLogNumber = logFileNumberReader.readLine();
			curLogFileNumber = Integer.parseInt(strLogNumber);
			logFileNumberReader.close();
		}catch(IOException e){
			System.out.println("ERROR: could not determine current log number file '" +logNumberFile + "'");
		}
		System.out.println("Creating Log file for trial " + curLogFileNumber);

		
		curLogFileNumber++;
		String fileName = "\\\\data"+File.pathSeparator+dataLogBaseName+Integer.toString(curLogFileNumber)+".txt";
		try{
			//create the file if it doesn't exist
			File f = new File(fileName);
			//f.mkdirs();
			f.createNewFile();
			//make a writer to the file
			logFileWriter = new BufferedWriter(new FileWriter(fileName));
			logFileWriter.flush();
			logFileWriter.close();
		}catch(IOException e){
			System.out.println("ERROR: could not create log file '"+fileName+"'.");
		}
		
	}
	
	public static void initDataLogger(){
		theDataLogger = new DataLogger();
	}
	public static DataLogger getDataLogger(){
		return theDataLogger;
	}
	
	protected void finalize(){
		saveLog();
	}
	
	public void saveLog(){
		try{
			logFileWriter.flush();
			logFileWriter.close();
		}catch(IOException e){
			System.out.println("ERROR: could close log file.");
		}
	}
	
	public void log(String info, long time){
		try{
			logFileWriter.write(info+"\tt"+Long.toString(SimTimer.getCurTime()));
		}catch(IOException e){
			System.out.println("ERROR: could write to log file.");
		}
	}
}
