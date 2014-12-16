package robotGUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class DataLogger {
	private static final String dataLogBaseName = "HRI_sim_log_";
	private static final String dataLogNumbers = "HRI_sim_log_numbers.txt";
	private static DataLogger theDataLogger; //singleton

	
	BufferedWriter logFileWriter;
	int curLogFileNumber;
	public DataLogger(){
		curLogFileNumber = 100;
//		System.out.println("Working Directory = " +  System.getProperty("user.dir"));
		String logNumberFile = "data"+System.getProperty("file.separator")+dataLogNumbers;
		try{
			BufferedReader logFileNumberReader = new BufferedReader(new FileReader(logNumberFile));
			String strLogNumber = logFileNumberReader.readLine();
			curLogFileNumber = Integer.parseInt(strLogNumber);
			logFileNumberReader.close();
			
			BufferedWriter logFileNumberWriter = new BufferedWriter(new FileWriter(logNumberFile));
			logFileNumberWriter.write(Integer.toString(curLogFileNumber+1));
			logFileNumberWriter.flush();
			logFileNumberWriter.close();
		}catch(IOException e){
			System.out.println("ERROR: could not determine current log number file '" +logNumberFile + "'");
		}
		System.out.println("Creating Log file for trial " + curLogFileNumber);

		
		curLogFileNumber++;
		String fileName = "data"+System.getProperty("file.separator")+dataLogBaseName+Integer.toString(curLogFileNumber)+".txt";
		try{
			//create the file if it doesn't exist
			File f = new File(fileName);
			//f.mkdirs();
			f.createNewFile();
			//make a writer to the file
			logFileWriter = new BufferedWriter(new FileWriter(fileName));
			logFileWriter.write("Log for trial " + Integer.toString(curLogFileNumber)+".");
			logFileWriter.newLine();
//			logFileWriter.flush();
//			logFileWriter.close();
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
		System.out.println("Closing log file.");
		try{
			logFileWriter.flush();
			logFileWriter.close();
		}catch(IOException e){
			System.out.println("ERROR: could close log file.");
		}
	}
	
	public void log(String info, long time){
		try{
			String log = info+"\tt"+Long.toString(SimTimer.getCurTime());
//			System.out.println("Writing '"+log+"' to log file");
					
			logFileWriter.write(log);
			logFileWriter.newLine();
		}catch(IOException e){
			System.out.println("ERROR: could write to log file.");
		}
	}
}
