package Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Formatter;

public class Logger {

	private final static String generalLogsFilePath = "general-logs.txt";
	private final static String eventLogsFilePath = "event-logs.txt";
	private final static String queryLogsFilePath = "query-logs.txt";
	private final static Integer maxLogFileSize = 0;
	
	//
	
	public final static String databaseCreationChangeMessage = "Database %s created sucessfully ";
	public final static String databaseMetaErrorChangeMessage = "Database %s created sucessfully, Meta file creation failed ";
	public final static String databaseCreationErrorChangeMessage = "Database creation failed ";
	
	
	public Logger() {
		checkIfLogFileExists();
	}
	
	public static void processLogs(MainLogger logger) throws IOException {
		File generalLogs = new File(generalLogsFilePath);
		File eventLogs = new File(eventLogsFilePath);
		File queryLogs = new File(queryLogsFilePath);
		checkIfLogFileExists();
		processGeneralLogs(logger,generalLogs);
		processQueryLogs(logger, queryLogs);
		processEventLogs(logger, eventLogs);
	}
	
	private static void processGeneralLogs(MainLogger logger, File generalLogs) throws IOException {
		Formatter formatter = new Formatter();
		if(logger.getUserName()!=null) {
			formatter.format("%s|%s\n", "user",logger.getUserName());
		}
		if(logger.getActiveDatabase()!=null) {
			formatter.format("%s|%s\n","database",logger.getActiveDatabase());
		}
		if(logger.getTotalTables()!=null) {
			formatter.format("%s|%s\n", "Total Tables",logger.getTotalTables());
		}
		if(logger.getTotalRecords()!=null) {
			formatter.format("%s|%s\n", "Total Records",logger.getTotalRecords());
		}
		if(logger.getCommand()!=null) {
			formatter.format("%s|%s\n","query",logger.getCommand());
		}
		formatter.format("%s|%s%s\n","start_time",logger.getCurrentTimeMillis(),"ms");
		formatter.format("%s|%s%s\n","execution_time",logger.getExecutionTimeMillis(),"ms");
		formatter.format("%s", "\n");
		System.out.println(formatter);
		Files.write(generalLogs.toPath(), formatter.toString().getBytes(), StandardOpenOption.APPEND);
	}
	
	private static void processQueryLogs(MainLogger logger, File queryLogs) throws IOException {
		Formatter formatter = new Formatter();
		if(logger.getUserName()!=null) {
			formatter.format("%s|%s\n", "user",logger.getUserName());
		}
		if(logger.getActiveDatabase()!=null) {
			formatter.format("%s|%s\n","database",logger.getActiveDatabase());
		}
		if(logger.getTableName()!=null) {
			formatter.format("%s|%s\n","table",logger.getTableName());
		}
		if(logger.getCommand()!=null) {
			formatter.format("%s|%s\n","query",logger.getCommand());
		}
		formatter.format("%s|%s%s\n","start_time",logger.getCurrentTimeMillis(),"ms");
		formatter.format("%s|%s%s\n","execution_time",logger.getExecutionTimeMillis(),"ms");
		formatter.format("%s","\n");
		Files.write(queryLogs.toPath(), formatter.toString().getBytes(), StandardOpenOption.APPEND);
		}
	
	private static void processEventLogs(MainLogger logger, File eventLogs) throws IOException {
		Formatter formatter = new Formatter();
		if(logger.getActiveDatabase()!=null) {
			formatter.format("%s|%s\n", "database",logger.getActiveDatabase());
		}
		if(logger.getTableName()!=null) {
			formatter.format("%s|%s\n","table",logger.getTableName());
		}
		if(logger.getChangeMessage()!=null) {
		formatter.format("%s|%s\n", "message",logger.getChangeMessage());
		}
		if(logger.getLogType()!=null) {
		formatter.format("%s|%s\n", "type",logger.getLogType());
		}
		formatter.format("%s", "\n");
		Files.write(eventLogs.toPath(), formatter.toString().getBytes(), StandardOpenOption.APPEND);
	}
	
	private static void checkIfLogFileExists() {
	
		try {
			File generalLogs = new File(generalLogsFilePath);
			File eventLogs = new File(eventLogsFilePath);
			File queryLogs = new File(queryLogsFilePath);
			if(!generalLogs.exists()) {
				generalLogs.createNewFile();
			}
			if(!eventLogs.exists()) {
				eventLogs.createNewFile();
				System.out.println(eventLogs.exists());
			}
			if(!queryLogs.exists()) {
				queryLogs.createNewFile();
				System.out.println(eventLogs.exists());
			}
		} catch (Exception e) {
			System.err.println("Error in creating log files");
		}
	}
	
	public static void log(MainLogger logger) throws IOException {
		processLogs(logger);
	}

}
