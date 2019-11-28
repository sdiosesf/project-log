package com.project.log;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {
    private static boolean logToFile;
    private static boolean logToConsole;
    private static boolean logMessage;
    private static boolean logWarning;
    private static boolean logError;
    private static boolean logToDatabase;
    private static Map<String, String> dbParams;
    private static Logger logger;

    public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam, boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map<String, String> dbParamsMap) {
        logger = Logger.getLogger("MyLog");
        logError = logErrorParam;
        logMessage = logMessageParam;
        logWarning = logWarningParam;
        logToDatabase = logToDatabaseParam;
        logToFile = logToFileParam;
        logToConsole = logToConsoleParam;
        dbParams = dbParamsMap;
    }

    /**
     * method that allows to process the type of log
     * @param messageText
     * @param message
     * @param warning
     * @param error
     */
	public void logMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {
        int logQuantity  = 0;
        String messageError = "";
        String logFileName = "logFile.txt";
        
        //validation
        if (messageText == null || messageText.trim().isEmpty()) {
        	logger.log(Level.INFO, "the message is empty.");
            return;
        }
        if (!logToConsole && !logToFile && !logToDatabase) {
            throw new Exception("Invalid configuration.");
        } else if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
            throw new Exception("Error or Warning or Message must be specified.");
        }

        //message
        if (message && logMessage) {
            logQuantity++;
            messageError = getMessageError(messageError, messageText, "message");
        }
        if (error && logError) {
            logQuantity++;
            messageError = getMessageError(messageError, messageText, "error");
        }
        if (warning && logWarning) {
            logQuantity++;
            messageError = getMessageError(messageError, messageText, "warning");
        }
	
        //log type
        if (logToFile) {
            File logFile = new File(dbParams.get("logFileFolder") + "/" + logFileName);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
            
            FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/" + logFileName);
            logger.addHandler(fh);
            logger.log(Level.INFO, messageText);
        }
        if (logToConsole) {
            logger.addHandler(new ConsoleHandler());
            logger.log(Level.INFO, messageText);
        }	
        if(logToDatabase) {
            Properties connectionProps = new Properties();
            connectionProps.put("user", dbParams.get("userName"));
            connectionProps.put("password", dbParams.get("password"));

            Connection connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName") + ":" + dbParams.get("portNumber") + "/" + dbParams.get("dataBaseName"), connectionProps);

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO log VALUES ('" + messageError + "', '" + String.valueOf(logQuantity) + "')");
            
            stmt.close();
            connection.close();
        }
	}
	
	/**
     * method that processes the error message
     * @param messageError
     * @param messageText
     * @param nameTypeLog
     */
	private String getMessageError(String messageError, String messageText, String nameTypeLog) {
		if(messageError != null && !messageError.trim().isEmpty()) {
			messageError+= "\n";
		}
		messageError+= nameTypeLog + " " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + " " + messageText;
		return messageError;
	}
}

