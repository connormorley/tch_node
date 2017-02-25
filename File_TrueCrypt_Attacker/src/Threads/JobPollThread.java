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

public class JobPollThread {

	public static boolean terminationSwitch = false; // I think this can be altered outside thread, will force loop termination and return.
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
	        /*if(!result.equals("no") && !result.equals(Integer.toString(CentralController.ignoreID))) // If return from server is positive, indicating waiting job, break loop and start analysis or something
	        	{
	        		retRes = Integer.parseInt(result);
	        		break;
	        	}*/
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
/*		String mac = "";
		Enumeration<NetworkInterface> adapters = NetworkInterface.getNetworkInterfaces();
        byte[] macBytes = adapters.nextElement().getHardwareAddress();
        for (int k = 0; k < macBytes.length; k++) {
           mac =  String.format("%02X%s", macBytes[k], (k < macBytes.length - 1) ? "-" : "");
        }
        return mac;
        */
        InetAddress ip;
        String macAddress = "";
        try {
        	
        	//TESTING!!! - Allocation of the desired interface by use of its name, specified within properties
          //ip = NetworkInterface.getByName("wlan1").getInetAddresses().nextElement();
        	ip = NetworkInterface.getByName(interfaceID).getInetAddresses().nextElement();
            System.out.println("Current IP address : " + ip.getHostAddress());
            NetworkInterface network = NetworkInterface.getByName(interfaceID);
        	
/*            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip)*/;

            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
            }
            System.out.println(sb.toString());
            macAddress = sb.toString();
        } /*catch (UnknownHostException e) {

            e.printStackTrace();

        }*/ catch (SocketException e){

            e.printStackTrace();

        }
        return macAddress;
	}
}
