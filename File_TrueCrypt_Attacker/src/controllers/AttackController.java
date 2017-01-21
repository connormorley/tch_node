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

public class AttackController {

	private static int passwordMasterCounter = 0;
	private static ArrayList<ArrayList<Integer>> testingSet;
	private static char[] masterCharSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`¨!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\|≈…Ê".toCharArray();
	static LtA logA = new LogObject();
	
	// Initiate attack on specified file, Alpha version is set to predetermined numerical figure and test series
	public static String attack(String filePath, int sequenceIdentifier, String attackMethod) throws InterruptedException {
		if(attackMethod.equals("Brute Force"))
		{
			passwordMasterCounter = sequenceIdentifier * 500;
			testingSet = generatePasswords(); // Generates 5000 combinations to try per counter with default set.
			ArrayList<ArrayList<Integer>> checkDebug = testingSet;
			int attempt = 0;
			int healthCounter = 0; //Trigger to prompt health check to server
			if (filePath.contains("\\"))
				filePath = filePath.replaceAll("'", "\\\'");
			while (attempt != testingSet.size()) {
				String password = getPassword(attempt);
				System.out.println(password);
				if(password.contains("\""))
					password = password.replaceAll("\"", "\\\\\"");
				System.out.println(filePath);
				Process p;
				String file = "";
				try {
					if (System.getProperty("os.name").contains("Windows")) //If the host system in windows
					{
						file = ".\\truecrypt";
						//This try block is the inclusion of the external TC executable that must be used as the interface when testing password
						p = Runtime.getRuntime().exec(file + " /s /l x /v " + filePath + " /p " + password + " /q");
						Thread.sleep(150); // I want this down to 50.... HOW!!!!!!
						p = Runtime.getRuntime().exec("find \"Block\" x:\\\\");
						//p = Runtime.getRuntime().exec("if exist x:\\\\ (echo \"success\")");
						BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String output = stdError.readLine();
						//if (!output.equals("File not found - X:\\\\") || !output.equals("Access denied - X:\\\\")) { // Error output will differ depending on drive letter, this should be an option!
						//if (!output.equals("success")) {
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
						String command = "truecrypt " + filePath + " /media/tc -p=" + password + " -k= --protect-hidden=no --non-interactive -v --mount-options=nokernelcrypto";
						p = Runtime.getRuntime().exec(command);
						Thread.sleep(150); // I want this down to 50.... HOW!!!!!!
						BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String output = stdError.readLine();
						System.out.println(output);
						if (output != null) { // Error output will differ depending on drive letter, this should be an option!
							System.out.println("Password: " + password);
						} else {
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
				if(healthCounter == 4)
				{
					try {
						String hCheck = sendHealth();
						if (!hCheck.equals(""))
							return "";
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				healthCounter = 0;
				}
				
			}
		}
		else if(attackMethod.equals("Dictionary"))
		{
			ArrayList<String> wordList = DatabaseController.getAttackSequence(sequenceIdentifier);
			if(wordList.isEmpty())
				return "wordlistIsEmptyubhrgqrng[qeophmnboqwiptn230i7u24-024jh[q456jh2i05yj98246jyh902348yhj058j2-q89";
			Process p;
			String file = "";
			for(String password : wordList)
			{
			try {
				if (System.getProperty("os.name").contains("Windows")) //If the host system in windows
				{
					file = ".\\truecrypt";
					//This try block is the inclusion of the external TC executable that must be used as the interface when testing password
					p = Runtime.getRuntime().exec(file + " /s /l x /v " + filePath + " /p " + password + " /q");
					Thread.sleep(150); // I want this down to 50.... HOW!!!!!!
					p = Runtime.getRuntime().exec("find \"Block\" x:\\\\");
					//p = Runtime.getRuntime().exec("if exist x:\\\\ (echo \"success\")");
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String output = stdError.readLine();
					//if (!output.equals("File not found - X:\\\\") || !output.equals("Access denied - X:\\\\")) { // Error output will differ depending on drive letter, this should be an option!
					//if (!output.equals("success")) {
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
					String command = "truecrypt " + filePath + " /media/tc -p=" + password + " -k= --protect-hidden=no --non-interactive -v --mount-options=nokernelcrypto";
					p = Runtime.getRuntime().exec(command);
					Thread.sleep(150); // I want this down to 50.... HOW!!!!!!
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String output = stdError.readLine();
					System.out.println(output);
					if (output != null) { // Error output will differ depending on drive letter, this should be an option!
						System.out.println("Password: " + password);
					} else {
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
			}
		}
		
		return ""; // Add recovery return statement to signal to Central to ask for another attack set.
	}
	
	public static String sendHealth() throws JSONException, IOException
	{
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("password", "test"));
        sending.add(new PostKey("deviceid", JobPollThread.macAddress));
        String hCheck = TransmissionController.sendToServer(sending, "healthCheck");
        if(hCheck.equals("abort"))
        	return "abort";
        return "";
	}

	//From the generated password list retrieve the associated Integer array and translate into characters.
	public static String getPassword(int position) {
		String ret = "";
		ArrayList<Integer> passwordSet = testingSet.get(position);
		for (int i = 0; i < passwordSet.size(); i++) {
			ret = ret + masterCharSet[passwordSet.get(i)];
		}
		return ret;
	}
	
	//Using the passwordMasterCount as a controller, generate a series of Integer arrays in incrementing values from right to left with max 98 to be 
	//used as password keys.
	public static ArrayList<ArrayList<Integer>> generatePasswords() {
		System.out.println(masterCharSet);
		ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();

		int startPoint = passwordMasterCounter * 980; // Start value
		startPoint = startPoint / 98;

		ArrayList<Integer> startArray = new ArrayList<Integer>();
		startArray = getArrayPoint(startPoint);

		int endPoint = (passwordMasterCounter + 500) * 980; // End value
		endPoint = endPoint / 98;
		ArrayList<Integer> endArray = new ArrayList<Integer>();
		endArray = getArrayPoint(endPoint);

		ArrayList<Integer> test = new ArrayList<Integer>();

		int counter = startArray.size() - 1;

		while (!startArray.toString().equals(endArray.toString())) {
			System.out.println(startArray);
			System.out.println(endArray);

			while (startArray.get(counter) != 99) {
				ArrayList<Integer> temp = new ArrayList<Integer>(startArray);
				Integer testNumber = new Integer(ret.size());
				ret.add(temp);
				ArrayList<ArrayList<Integer>> checkDebug = ret;
				startArray.set(counter, startArray.get(counter) + 1);
			}

			startArray.set(counter, 0);

			if(counter - 1 == -1)
			{
					startArray.set(counter, startArray.get(counter) + 1);
					counter++;
					startArray.add(counter, 0);
			}
			else{
				for (int placement = counter - 1; placement != -1; placement--) {
					if (startArray.get(placement) == 98) {
						startArray.set(placement, 0);
						if(placement == 0)
						{
							counter++;
							startArray.add(counter, 0);
						}
					} else {
						startArray.set(placement, startArray.get(placement) + 1);
						break;
					}
				}
			}
		}

		return ret;
	}

	//From the masterKey number generate a Integer array, these are used as start and end points for the current numerical value.
	private static ArrayList<Integer> getArrayPoint(int passwordsUsed) {
		ArrayList<Integer> columns = new ArrayList<Integer>();
		int limit = 1;
		int columnCount = 0;
		boolean endReached = false;
		columns.add(columnCount, 0); // Will always have at least one column
		columnCount++;

		if (passwordsUsed > limit) {
			columns.add(columnCount, 0); // If above one run it has moved onto the next columns such as 1a
			columnCount++;
			passwordsUsed--;
			limit = limit * 98;

			while (endReached == false) {

				if (passwordsUsed > limit) // While the number of password runs is above the limit which is a multiple of 98 keep running
				{
					columns.add(columnCount, 0); // If count is above the limit this means another column
					columnCount++;
					passwordsUsed = passwordsUsed - limit;
					limit = limit * 98; // Each multiplication is a column to the left, then must be checked left to right dividing limit by 98
					System.out.println(limit);
				}

				else {
					int finalCounter = 0;
					limit = limit / 98; // Divide limit by 98 from limit that caused entry to else statement
					while (limit != 1) {
						if (passwordsUsed > limit) // If the password count is greater than the new limit value then count into appropriate column
						{
							int tally = passwordsUsed / limit; // Find divisible value ignoring remainders, result is how many runs in that column
							passwordsUsed = passwordsUsed - (tally * limit);
							columns.set(finalCounter, tally); // First column (0) counted from limit first
							System.out.println(tally);
							finalCounter++; // Increment counter to next column
							if (limit == 98) {
								tally = passwordsUsed % limit;
								System.out.println(tally);
								columns.set(finalCounter, tally);
								break;
							}
							limit = limit / 98;
						} else {
							limit = limit / 98;
							columns.set(finalCounter, 0);
							finalCounter++;
						}
					}

					endReached = true; // Exit while loop
				}

				// Down here should be some sort of array relation to stored
				// tally values to find appropriate characters from store
			}
			System.out.println(columns);
		}
		return columns;
	}

}
