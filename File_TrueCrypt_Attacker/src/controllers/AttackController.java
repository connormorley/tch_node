	package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;

import Threads.JobPollThread;
import loggers.LogObject;
import loggers.LtA;
import objects.PostKey;

/*	Created by:		Connor Morley
 * 	Title:			TCrunch Node Attack Controller
 *  Version update:	2.4
 *  Notes:			Class controls the attack operation of the node, depending on attack method either creates password range from ARN
 *  				or retrieves password range from associated DB wordlist. Cycles all passwords in ARN sequqnce against file fragment
 *  				and either generates a positive results (ending attack) or runs out of passwords in sequqnce resutling in cycling a 
 *  				ARN request. 
 *  
 *  References:		N/A
 */

public class AttackController {

	public static int passwordMasterCounter = 0;
	public static int balanceNumber = 0;
	public static ArrayList<ArrayList<Integer>> testingSet;
	static LtA logA = new LogObject();
	public static boolean speedTest = false;
	static long startTime = 0;
	static long endTime = 0;
	static long totalTime= 0;
	
	// Initiate attack on specified file, Alpha version is set to predetermined numerical figure and test series
	public static String attack(String filePath, int sequenceIdentifier, String attackMethod) throws InterruptedException {
		System.out.println("Balance number for attack is : " + balanceNumber);
		if(attackMethod.equals("Brute Force"))
		{
			passwordMasterCounter = sequenceIdentifier;
			testingSet = PasswordGenerator.generatePasswords();
			ArrayList<ArrayList<Integer>> checkDebug = testingSet;
			int attempt = 0;
			int healthCounter = 0; //Trigger to prompt health check to server
			if (filePath.contains("\\"))
				filePath = filePath.replaceAll("'", "\\\'");
			while (attempt != testingSet.size()) {
				if(speedTest){
				startTime = System.currentTimeMillis();
				}
				String password = PasswordGenerator.getPassword(attempt);
				System.out.println(password);
				if(password.contains("\\")) // Replace all forward slashes with excepted forward slashes
					password = password.replaceAll("\\\\", "\\\\\\\\");
				if(password.contains("\"")) // Replace all quotations with excepted quotations
					password = password.replaceAll("\"", "\\\\\"");
				System.out.println(filePath);
				Process p;
				String file = "";
				try {
					if (System.getProperty("os.name").contains("Windows")) //If the host system is windows
					{
						file = ".\\truecrypt.exe";
						p = Runtime.getRuntime().exec(file + " /s /l x /v " + filePath + " /p\"" + password + "\" /q");
						BufferedReader stdError1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String output1 = stdError1.readLine();
						p = Runtime.getRuntime().exec("find \"Block\" x:\\\\");
						BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String output = stdError.readLine();
						if(password.contains("\\\\\"")) // Replace all quotations with excepted quotations
							password = password.replaceAll("\\\\\"", "\"");
						if(password.contains("\\\\\\\\")) // Replace all forward slashes with excepted forward slashes
							password = password.replaceAll("\\\\\\\\", "\\\\");
						if (output.equals("Access denied - X:\\\\")) { // Error output will differ depending on drive letter, this should be an option!
							System.out.println("Correct Password: " + password);
							p = Runtime.getRuntime().exec(file + " /q /dx"); //Dismount the file when finished if mounted successfully.
							return password;
						} else {
							System.out.println("Password: " + password);
						}
					}
					else if (System.getProperty("os.name").contains("Linux")) //If the host system in LNX
					{
						file = "./truecrypt";
						String command = "truecrypt -t " + filePath + " /media/tc -p=\"" + password + "\" -k= --protect-hidden=no --non-interactive -v";
						p = Runtime.getRuntime().exec(command);
						BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String output = stdError.readLine();
						System.out.println(output);
						if(password.contains("\\\\\"")) // Replace all quotations with excepted quotations
							password = password.replaceAll("\\\\\"", "\"");
						if(password.contains("\\\\\\\\")) // Replace all forward slashes with excepted forward slashes
							password = password.replaceAll("\\\\\\\\", "\\\\");
						if (output != null) {
							if(output.equals("Error: Failed to set up a loop device:")) {
								System.out.println("Correct Password: " + password);
								p = Runtime.getRuntime().exec("truecrypt -d /media/tc"); //Dismount the file when finished if mounted successfully.
								return password;
							} else if(output.contains("device-mapper: resume ioctl on")){
								System.out.println("Correct Password: " + password);
								p = Runtime.getRuntime().exec("truecrypt -d /media/tc"); //Dismount the file when finished if mounted successfully.
								return password;
							} else
							System.out.println("Password: " + password);
						}
						else {
							System.out.println("Correct Password: " + password);
							p = Runtime.getRuntime().exec("truecrypt -d /media/tc"); //Dismount the file when finished if mounted successfully.
							return password;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				attempt++;
				healthCounter++;
				if(healthCounter == 4 && !speedTest)
				{
					try {
						String hCheck = sendHealth();
						if(hCheck.equals("disconnect"))
							return "disconnect";
						else if(hCheck.equals("abort"))
							return "";
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				healthCounter = 0;
				}
				if(speedTest)
				{
				endTime = System.currentTimeMillis();
				long result = endTime - startTime;
				totalTime = totalTime + result;
				if(password.equals("9"))
				return Long.toString(totalTime);
				}
			}
		}
		else if(attackMethod.equals("Dictionary"))
		{
			ArrayList<String> wordList = DatabaseController.getAttackSequence(sequenceIdentifier, balanceNumber);
			if(wordList.isEmpty())
				return "exhausted";
			Process p;
			String file = "";
			int healthCounter = 0; //Trigger to prompt health check to server
			for(String password : wordList)
			{
				if(password.contains("\\")) // Replace all forward slashes with excepted forward slashes
					password = password.replaceAll("\\\\", "\\\\\\\\");
				if(password.contains("\"")) // Replace all quotations with excepted quotations
					password = password.replaceAll("\"", "\\\\\"");
			try {
				if (System.getProperty("os.name").contains("Windows")) //If the host system is windows
				{
					file = ".\\truecrypt.exe";
					p = Runtime.getRuntime().exec(file + " /s /l x /v " + filePath + " /p\"" + password + "\" /q");
					BufferedReader stdError1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String output1 = stdError1.readLine();
					p = Runtime.getRuntime().exec("find \"Block\" x:\\\\");
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String output = stdError.readLine();
					if(password.contains("\\\\\"")) // Replace all quotations with excepted quotations
						password = password.replaceAll("\\\\\"", "\"");
					if(password.contains("\\\\\\\\")) // Replace all forward slashes with excepted forward slashes
						password = password.replaceAll("\\\\\\\\", "\\\\");
					if (output.equals("Access denied - X:\\\\")) {
						System.out.println("Correct Password: " + password);
						p = Runtime.getRuntime().exec(file + " /q /dx"); //Dismount the file when finished if mounted successfully.
						return password;
					} else {
						System.out.println("Password: " + password);
					}
				}
				else if (System.getProperty("os.name").contains("Linux")) //If the host system in LNX
				{
					file = "./truecrypt";
					String command = "truecrypt -t " + filePath + " /media/tc -p=\"" + password + "\" -k= --protect-hidden=no --non-interactive -v";
					p = Runtime.getRuntime().exec(command);
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String output = stdError.readLine();
					System.out.println(output);
					if(password.contains("\\\\\"")) // Replace all quotations with excepted quotations
						password = password.replaceAll("\\\\\"", "\"");
					if(password.contains("\\\\\\\\")) // Replace all forward slashes with excepted forward slashes
						password = password.replaceAll("\\\\\\\\", "\\\\");
					if (output != null) {
						if(output.equals("Error: Failed to set up a loop device:")) {
							System.out.println("Correct Password: " + password);
							p = Runtime.getRuntime().exec("truecrypt -d /media/tc"); //Dismount the file when finished if mounted successfully.
							return password;
						} else if(output.contains("device-mapper: resume ioctl on")){
							System.out.println("Correct Password: " + password);
							p = Runtime.getRuntime().exec("truecrypt -d /media/tc"); //Dismount the file when finished if mounted successfully.
							return password;
						} else
						System.out.println("Password: " + password);
					}
					else {
						System.out.println("Correct Password: " + password);
						p = Runtime.getRuntime().exec("truecrypt -d /media/tc"); //Dismount the file when finished if mounted successfully.
						return password;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			healthCounter++;
			if(healthCounter == 4)
			{
				try {
					String hCheck = sendHealth();
					if(hCheck.equals("disconnect"))
						return "disconnect";
					else if (hCheck.equals("abort"))
						return "";
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			healthCounter = 0;
			}
			}
		}
		
		return "";
	}
	
	public static String sendHealth() throws JSONException
	{
		try{
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("password", "test"));
        sending.add(new PostKey("deviceid", JobPollThread.macAddress));
        String hCheck = TransmissionController.sendToServer(sending, "healthCheck");
        if(hCheck.equals("abort"))
        	return "abort";
        return "";
		}catch(IOException e)
		{
			System.out.println("Server connection issue occured!");
			return "disconnect";
		}
	}
}
	