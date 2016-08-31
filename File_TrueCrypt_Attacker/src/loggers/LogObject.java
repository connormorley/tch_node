package loggers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* Create by: 	Connor Morley
* Date: 		April 2016
* Title: 		Error Scraper Application
* Version:		1.0
*/

public class LogObject implements LtA {
	@Override
    public void doLog(String sentName, String logInfo, String level) {
		if(level.equals("Info"))
		{
        try {
            LogHandler.getLogger(sentName).log(Level.INFO, logInfo);
        } catch (IOException ex) {
            Logger.getLogger(sentName).log(Level.SEVERE, null, ex);
        }
		}
		else if(level.equals("Warning"))
		{
	        try {
	            LogHandler.getLogger(sentName).log(Level.WARNING, logInfo);
	        } catch (IOException ex) {
	            Logger.getLogger(sentName).log(Level.SEVERE, null, ex);
	        }
		}
		else if(level.equals("Critical"))
		{
	        try {
	            LogHandler.getLogger(sentName).log(Level.SEVERE, logInfo);
	        } catch (IOException ex) {
	            Logger.getLogger(sentName).log(Level.SEVERE, null, ex);
	        }
		}
    }
}