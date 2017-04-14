package Threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.json.JSONException;

import controllers.CentralController;
import controllers.TransmissionController;
import objects.PostKey;

/*	Created by:		Connor Morley
 * 	Title:			Job Poll Thread
 *  Version update:	2.3
 *  Notes:			Initially a parallel thread, this class controls the polling of the control server by the attack node. The class
 *  				identifies the nodes MAC and IP information and transmits this to the server when polling for attack jobs. This is 
 *  				conducted periodically. If an error occurs appropriate action is taken either by this class or the transmission
 *  				controller class to handle the issue gracefully or as expected.  
 *  
 *  References:		N/A
 */

public class JobPollThread {

	public static boolean terminationSwitch = false;
	public static String macAddress = "";
	public static String interfaceID = "";
	
	public static int pollJobs() throws JSONException, IOException, InterruptedException
	{
		macAddress = getMac(); // Get unique identifier for the pc, this can be spoofed but in this case it's unlikely.
		int retRes = 0;
		TransmissionController.jobCheck = true;
		TransmissionController.firstFailedCheck = true;
		while(terminationSwitch == false)
		{
			
			ArrayList<PostKey> sending = new ArrayList<PostKey>();
	        sending.add(new PostKey("password", "test"));
	        sending.add(new PostKey("deviceid", macAddress));
	        try{
	        String result = TransmissionController.sendToServer(sending, "attackCheck");
	        if(!result.equals("no"))
	        {
	        	retRes = Integer.parseInt(result);
        		break;
	        }
	        Thread.currentThread().sleep(5000); // Poll every 5 seconds, this can be an options in properties file or options panel.
	        } catch(IOException e)
	        {
	        	System.out.println("Connection error!");
	        }
		}
		TransmissionController.jobCheck = false;
		return retRes;
	}
	
	private static String getMac() throws SocketException
	{
        InetAddress ip;
        String macAddress = "";
        try {
        	ip = NetworkInterface.getByName(interfaceID).getInetAddresses().nextElement();
            System.out.println("Current IP address : " + ip.getHostAddress());
            NetworkInterface network = NetworkInterface.getByName(interfaceID);
            byte[] mac = network.getHardwareAddress();
            System.out.print("Current MAC address : ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
            }
            System.out.println(sb.toString());
            macAddress = sb.toString();
        } catch (SocketException e){

            e.printStackTrace();

        }
        return macAddress;
	}
}
