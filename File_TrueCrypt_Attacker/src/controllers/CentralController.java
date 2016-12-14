package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONException;

import Threads.JobPollThread;
import objects.PostKey;

public class CentralController {
	
	public static int attackID = 1;

	public static void main(String[] args) throws JSONException, IOException, InterruptedException
	{
		while (1 == 1) {
			JobPollThread.pollJobs();
			attackID = getAttackID();
			String filePath = getFile();
			while (attackID == getAttackID()) {
				int attackSequence = getAttackSequence();
				int something = attackID;
				int somethingorother = getAttackID();
				String res = AttackController.attack(filePath, attackSequence);
				if (!res.equals("")) {
					System.out.println("The password is : " + res);
					sendCorrectPassword(res);
					attackID = 1; // Informs server of retrieved password and sets attackID to 1 to terminate loop, awaits new attack in job poll.
				}
			}
		}
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
	
	public static int getAttackSequence() throws NumberFormatException, JSONException, IOException
	{
		int ret = 0;
		ArrayList<PostKey> sending = new ArrayList<PostKey>();
		sending.add(new PostKey("deviceid", JobPollThread.macAddress));
        sending.add(new PostKey("password", "test"));
        ret = Integer.parseInt(TransmissionController.sendToServer(sending, "getAttackSequence"));
		return ret;
	}
	
}
