package Analytics;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import Logger.LogReader;
import Logger.LogType;
import Logger.MainLogger;

public class BasicAnalytics {

	private LogReader logReader;
	private static String analysis = "analytics.txt";
	
	static {
		File file = new File(analysis);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				System.out.println("Unable to load analytics module");
			}
		}
	}
	
	public String getNumberOfQueries(String currentUser,String database) throws IOException {
		File file = new File(analysis);
		
		int validCount = 0;
		int invalidCount = 0;
		logReader = new LogReader();
		List<MainLogger> logs = logReader.getLogger();
		if(logs.isEmpty()) {
			System.out.println("No operations performed by the current user");
			return "0";
		}
		for(MainLogger log:logs) {
			if(log.getActiveDatabase()==null || log.getUserName() == null) continue;
			if(log.getActiveDatabase().equals(database) && log.getUserName().equals(currentUser)) {
				if(log.getLogType().equals(LogType.INVALID)) {
					invalidCount++;
				}else {
					validCount++;
				}
			}
		}
		if(validCount+invalidCount>0) {
			String analysisString = "user %s submitted %s queries (%s valid | %s invalid) on %s\n".formatted(currentUser,validCount+invalidCount,validCount,invalidCount,database);
			System.out.println(analysisString);
			Files.write(file.toPath(), analysisString.getBytes(), StandardOpenOption.APPEND);
		}else {
			System.out.format("no queries on %s by %s",database,currentUser);
		}
		return null;
	}
	
	public void getUpdateOperations(String currentUser, String database) throws IOException {
		logReader = new LogReader();
		File file = new File(analysis);
		List<MainLogger> logs = logReader.getLogger();
		List<String> output = new ArrayList<>();
		Map<String,Integer> countMap = new HashMap<String,Integer>();
		if(logs.isEmpty()) {
			System.out.println("No operations performed by the current user");
			return;
		}
		for(MainLogger log:logs) {
			if(log.getLogType().equals(LogType.UPDATE) && log.getActiveDatabase().equals(database) && log.getUserName().equals(currentUser)) {
				if(countMap.containsKey(log.getTableName())) {
					countMap.replace(log.getTableName(), countMap.get(log.getTableName())+1);
				}else {
					countMap.put(log.getTableName(),1);
				}
			}
		}
		if(countMap.isEmpty()) {
			String outputString = "No Update operations are performed on database %s\n".formatted(database);
			System.out.println(outputString);
			Files.write(file.toPath(), outputString.getBytes(), StandardOpenOption.APPEND);
		}
		for(Entry<String, Integer> countEntry : countMap.entrySet()) {
			String outputString = "Total %s Update operations are performed on %s\n".formatted(countEntry.getValue(),countEntry.getKey());
			System.out.println(outputString);
			Files.write(file.toPath(), outputString.getBytes(), StandardOpenOption.APPEND);
		}

	}
	public static void main(String[] args) throws IOException {
		BasicAnalytics analytics = new BasicAnalytics();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter analytics query string");
		String queryString = scanner.nextLine();
		System.out.println("query string is "+queryString);
		if(queryString.contains("count queries")) {
			String [] entries = queryString.split(" ");
			if(!(entries.length==3)) {
				System.out.println("Invalid count query");
				return;
			}
			if(entries[2].contains(";")) {
				analytics.getNumberOfQueries(args[0], entries[2].substring(0, entries[2].length()-1));
			}else {
				analytics.getNumberOfQueries(args[0], entries[2]);
			}
		}else if(queryString.contains("count update")){
			String [] entries = queryString.split(" ");
			if(entries.length>3) {
				System.out.println("Invalid count query");
				return;
			}
			if(entries[2].contains(";")) {
				analytics.getUpdateOperations(args[0], entries[2].substring(0, entries[2].length()-1));
			}else {
				analytics.getUpdateOperations(args[0], entries[2]);
			}
		}
		else {
			System.out.println("Analysis query not yet supported.");
		}
		return;
	}
}
