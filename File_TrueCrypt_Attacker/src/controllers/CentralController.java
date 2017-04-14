package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONException;

import Threads.JobPollThread;
import objects.PostKey;

/*	Created by:		Connor Morley
 * 	Title:			TCrunch Node Central Controller
 *  Version update:	2.4
 *  Notes:			Main class that involves application initiation. Class handles primary functions and is responsible for calling other
 *  				functions. Class handles initiation of polling server, retrieving attack information and processing attack results
 *  				appropriately.
 *  
 *  References:		http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
 */

public class CentralController {
	
	public static int attackID = 1;

	public static void main(String[] args) throws JSONException, IOException, InterruptedException
	{	
		System.out.println("System initiated");
		StartController.readSettingsFile();
		speedTest();
		while (1 == 1) {
			attackID = JobPollThread.pollJobs();
			String filePath = getFile();
			String attackMethod = getAttackMethod();
			int balance = getBalanceNumber();
			AttackController.balanceNumber = balance;
			while (attackID == getAttackID()) {
				int arn = getARN();
				if(arn == -1)
					break;
				int something = attackID;
				int somethingorother = getAttackID();
				String res = AttackController.attack(filePath, arn, attackMethod);
				if(res.equals("disconnect")) //If during a health check the server has been unreachable, cancel attack.
				{
					System.out.println("Server disconnect occured. Check server status and connection.");
					attackID = 1;
					break;
				}
				else if(res.equals("exhausted") && attackMethod.equals("Dictionary")) //If during dictionary attack wordlist is exhausted
				{
					System.out.println("Wordlist exhausted.");
					wordlistExhausted();
					attackID = 1;
				}
				else if (!res.equals("")) { //If during attack the correct password is found (which is an unknown output)
					System.out.println("The password is : " + res);
					sendCorrectPassword(res);
					attackID = 1; // Informs server of retrieved password and sets attackID to 1 to terminate loop, awaits new attack in job poll.
				}
				DatabaseController.removeARNCheck(arn);
			}
			//}
		}
	}
	
	public static void speedTest() throws JSONException, IOException, InterruptedException{
		long benchmark = 0;
		AttackController.speedTest = true;
		AttackController.balanceNumber = 100;
		System.out.println("Speed test start - ");
		String res = AttackController.attack("speedTest", 0, "Brute Force");
		AttackController.speedTest = false;
		AttackController.balanceNumber = 0;
		benchmark = (Long.parseLong(res) / 10);
		System.out.println("Character list length : " + PasswordGenerator.masterCharSet.length );
		System.out.println("Average response speed : " + (benchmark));
		System.out.println("The benchmark throughput for this machine is : " + (60000 / benchmark));
		benchmark = 60000 / benchmark;
		if(benchmark < 35) //Anything lower than this and the system will trigger a downed node due to being too slow. Plus would cause excessive polling.
		{
			System.out.println("This system does not have sufficient throughput and cannot be used with this system.");
			System.exit(0);
		}
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("password", "test"));
        sending.add(new PostKey("benchmark", Long.toString(benchmark)));
        TransmissionController.sendToServer(sending, "issueBenchmark");
	}
	
	public static int getBalanceNumber() throws NumberFormatException, JSONException, IOException
	{
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("password", "test"));
        int balance = Integer.parseInt(TransmissionController.sendToServer(sending, "getBalance"));	    
        return balance;
	}
	
	public static void wordlistExhausted() throws NumberFormatException, JSONException, IOException
	{
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("password", "test"));
        TransmissionController.sendToServer(sending, "wordlistExhausted");	    
        return;
	}
	
	//Relay password back to server to inform client, terminate any other attacking nodes by setting attackRunning to false.
	public static void sendCorrectPassword(String correctPassword) throws NumberFormatException, JSONException, IOException
	{
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("password", "test"));
        sending.add(new PostKey("result", correctPassword));
        TransmissionController.sendToServer(sending, "passwordFound");	    
        return;
	}
	
	public static String getAttackMethod() throws NumberFormatException, JSONException, IOException
	{
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("password", "test"));
        String attackMethod = TransmissionController.sendToServer(sending, "getAttackMethod");	    
        return attackMethod;
	}
	
	public static int getAttackID() throws NumberFormatException, JSONException, IOException
	{
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("password", "test"));
        int attackID = Integer.parseInt(TransmissionController.sendToServer(sending, "attackID"));	    
        return attackID;
	}
	
	public static String getFile() throws JSONException, IOException
	{
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("password", "test"));
        String byteString = TransmissionController.sendToServer(sending, "getJobBlock");
        
        // Hex to to character methodology source = http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
		int len = byteString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(byteString.charAt(i), 16) << 4)
					+ Character.digit(byteString.charAt(i + 1), 16));
		}
	    
		File outputFile = new File("testingTCFile");
	    try ( FileOutputStream outputStream = new FileOutputStream(outputFile); ) {
	        outputStream.write(data, 0, data.length);
	        outputStream.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
        return "testingTCFile";
	}
	
	public static int getARN() throws NumberFormatException, JSONException, IOException
	{
		int ret = 0;
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
		sending.add(new PostKey("deviceid", JobPollThread.macAddress));
        sending.add(new PostKey("password", "test"));
        ret = Integer.parseInt(TransmissionController.sendToServer(sending, "getARN"));
		return ret;
	}
	
}
