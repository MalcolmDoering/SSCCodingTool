package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.csv.*;
import org.apache.commons.io.*;
import org.apache.commons.io.input.BOMInputStream;

import main.Globals;
import toolbox.DebugTools;
import toolbox.notification.NotificationHandler;

public class FileIO {

	public char BOMChar;
	
	public void loadData() {
		
		boolean success = loadData(Globals.getDataFileName());
		
		if (success == false) return;
		
		DebugTools.print("Loading data from " + Globals.getDataFileName());
		DebugTools.print("Total records: " + Globals.dataManager.getLastRecordID());
		
		NotificationHandler.notify(Globals.Notifications.LIST_SELECTION);
	}
	
	
	
	public boolean loadData(String filename) {
		
		filename = "data" + File.separator + filename;
		
		//dataManager.records.clear();
		
		File f = new File(filename);
		
		if (f.exists() == false) {
			return false;
		}
		
		System.out.println("Now loading from " + f.getAbsolutePath());
		
		BufferedReader br;
		try {
		
			//br = new BufferedReader(new FileReader(filename));
			//br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
			
			br = new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(filename)), "UTF-8"));
			
			
			
			CSVParser parser = CSVParser.parse(br, CSVFormat.EXCEL.withQuote('"').withHeader());
			
			// get the headers...
			Map<String, Integer> headerMap = parser.getHeaderMap();
			ArrayList<String> headers = new ArrayList<String>();
			
			for (int i = 0; i < headerMap.size(); i++) {
				  headers.add(Integer.toString(0));
				}
			
			for (String header: headerMap.keySet()) {
				headers.set(headerMap.get(header), header);
			}
			
			Globals.dataManager.setHeaders(headers);
			
			// load the records...
			for (CSVRecord csvRecord : parser.getRecords()) {
				Globals.dataManager.addRecord(Record.fromCSVRecord(csvRecord));
			}	
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	
	public boolean saveData() {
		return saveData(Globals.getDataFileName());
	}
	
	
	public boolean saveData(String filename) {
		
		String extFilename = "data" + File.separator + filename;
		
		try {
			OutputStreamWriter fileWriter = new OutputStreamWriter(
					new FileOutputStream(extFilename),
					Charset.forName("UTF-8").newEncoder() 
					);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			
			// need this for csv with Japanese to open correctly in Microsoft Excel
			fileWriter.write('\uFEFF');
			//writer.flush();
			
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180);
	        
	        csvPrinter.printRecord(Globals.dataManager.getHeaders());
			
			for (Record record:Globals.dataManager.getRecords()) {
				csvPrinter.printRecord(record.toArrayList());
			}
			
	        csvPrinter.flush();
			csvPrinter.close();
			writer.close();
			
			DebugTools.print("CSV File written: " + extFilename);
			return true;
		} catch (IOException e) {
			System.out.println("Could not create output file.");
			return false;
		}
	}
	
	public boolean saveAnalysisResults(String filename, String results) {
		
		String extFilename = "data" + File.separator + filename;
		try {
			OutputStreamWriter fileWriter = new OutputStreamWriter(
					new FileOutputStream(extFilename),
					Charset.forName("UTF-8").newEncoder() 
					);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			writer.write(results);
			
			writer.close();
			DebugTools.print("CSV File written: " + extFilename);
			return true;
		} catch (IOException e) {
			System.out.println("Could not create output file: " + extFilename);
			return false;
		}
	}
	

}
