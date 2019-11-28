package com.project.log;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProjectLogTests {
	
	private static JobLogger jobLogger;
	
	@Test
	public void testLogToFile() throws Exception {
		Map<String, String> dbParamsMap =  new HashMap<String, String>();	
		dbParamsMap.put("logFileFolder", "F:/");
		
		jobLogger = new JobLogger(true, false, false, true, true, true, dbParamsMap);
		jobLogger.logMessage("problems processing data in the application.", true, true, true);
		
		assertTrue(true);
	}
	
	@Test
	public void testLogToConsole() throws Exception {		
		jobLogger = new JobLogger(false, true, false, true, true, true, null);
		jobLogger.logMessage("problems processing data in the application.", true, true, true);
		
		assertTrue(true);
	}
	
	@Test
	public void testLogToDatabase() throws Exception {
		Map<String, String> dbParamsMap =  new HashMap<String, String>();	
		dbParamsMap.put("userName", "root");
		dbParamsMap.put("password", "");
		dbParamsMap.put("dbms", "mysql");
		dbParamsMap.put("serverName", "localhost");
		dbParamsMap.put("dataBaseName", "comeletras");
		dbParamsMap.put("portNumber", "3306");
		
		jobLogger = new JobLogger(false, false, true, true, true, true, dbParamsMap);
		jobLogger.logMessage("problems processing data in the application.", true, true, true);
		
		assertTrue(true);
	}
}
