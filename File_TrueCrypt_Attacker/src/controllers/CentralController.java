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

public class CentralController {
	
	public static int attackID = 1;
	//public static int ignoreID;

	public static void main(String[] args) throws JSONException, IOException, InterruptedException
	{	
		System.out.println("System initiated");
		StartController.readSettingsFile();
		speedTest();
		while (1 == 1) {
			attackID = JobPollThread.pollJobs();
			//attackID = getAttackID();
			//if(attackID != ignoreID)
			//{
			String filePath = getFile();
			String attackMethod = getAttackMethod();
			int balance = getBalanceNumber();
			AttackController.balanceNumber = balance;
			while (attackID == getAttackID()) {
				int arn = getARN();
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
					//ignoreID = attackID; //Sets the ignoreID to the current attackID, this means it will be ignored when returned as a result of running attack by the server.
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
		/*Process p;
		String file = "";
		long benchmark = 0;
		//long endTime = 0;
		//ArrayList<Long> times = new ArrayList<Long>();
		long totalTime = 0;
		int count = 0;
		while( count != 20 )
		{
		try {
			if (System.getProperty("os.name").contains("Windows")) //If the host system in windows
			{
				File speedTest = new File(".\\speedTest");
				if(!speedTest.exists())
				{
					System.out.println("missing speed test file");
					System.exit(0);
				}
				file = ".\\truecrypt";
				long startTime = System.currentTimeMillis();
				//This try block is the inclusion of the external TC executable that must be used as the interface when testing password
				p = Runtime.getRuntime().exec(file + " /s /l x /v .\\speedTest /p X /q");
				BufferedReader stdError1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String output1 = stdError1.readLine();
				//Thread.sleep(150);
				p = Runtime.getRuntime().exec("find \"Block\" x:\\\\");
				//p = Runtime.getRuntime().exec("if exist x:\\\\ (echo \"success\")");
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String output = stdError.readLine();
				long endTime = System.currentTimeMillis();
				totalTime = totalTime + (endTime - startTime);
				//times.add((endTime - startTime));
				//benchmark = endTime - startTime;
				//System.out.println("Response speed : " + (endTime - startTime));
				//if (!output.equals("File not found - X:\\\\") || !output.equals("Access denied - X:\\\\")) { // Error output will differ depending on drive letter, this should be an option!
				//if (!output.equals("success")) {
			}
			else if (System.getProperty("os.name").contains("Linux")) //If the host system in LNX
			{
				File speedTest = new File("./speedTest");
				if(!speedTest.exists())
				{
					System.out.println("missing speed test file");
					System.exit(0);
				}
				file = "./truecrypt";
				long startTime = System.currentTimeMillis();
				String command = "truecrypt ./speedTest /media/tc -p=X -k= --protect-hidden=no --non-interactive -v";
				p = Runtime.getRuntime().exec(command);
				//Thread.sleep(150); 
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String output = stdError.readLine();
				long endTime = System.currentTimeMillis();
				totalTime = totalTime + (endTime - startTime);
				//times.add((endTime - startTime));
				//benchmark = endTime - startTime;
				//System.out.println("Response speed : " + (endTime - startTime));
				//System.out.println(output);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//benchmark = benchmark; // This addition is made for system usage variations, allows system to operate around attack procedure
		count++;
		}
		//endTime = System.currentTimeMillis();
		benchmark = ((totalTime / 20));*/
		long benchmark = 0;
		AttackController.speedTest = true;
		AttackController.balanceNumber = 100;
		System.out.println("Speed test start - ");
		String res = AttackController.attack("speedTest", 0, "Brute Force");
		AttackController.speedTest = false;
		AttackController.balanceNumber = 0;
		benchmark = (Long.parseLong(res) / 10);
		//benchmark = (benchmark / 61);
		System.out.println("Character list length : " + PasswordGenerator.masterCharSet.length );
		System.out.println("Average response speed : " + (benchmark));
		//benchmark = benchmark + (benchmark / 2);
		//System.out.println("Benchmark : " + (benchmark));
		//AttackController.stall = ((int)benchmark);
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
        
        
		int len = byteString.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(byteString.charAt(i), 16) << 4)
	                             + Character.digit(byteString.charAt(i+1), 16));
	    }
         
	   // byte[] finalbytes = byteString.getBytes("ISO-8859-1");
	    File outputFile = new File("testingTCFile");
	    try ( FileOutputStream outputStream = new FileOutputStream(outputFile); ) {
	        outputStream.write(data, 0, data.length);  //write the bytes and your done. 
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
