package Threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.json.JSONException;

import controllers.TransmissionController;
import objects.PostKey;

public class JobPollThread {

	public static boolean terminationSwitch = false; // I think this can be altered outside thread, will force loop termination and return.
	public static String macAddress = "";
	
	public static void pollJobs() throws JSONException, IOException, InterruptedException
	{
		getMac(); // Get unique identifier for the pc, this can be spoofed but in this case it's unlikely.
		while(terminationSwitch == false)
		{
			
			ArrayList<PostKey> sending = new ArrayList<PostKey>();
	        sending.add(new PostKey("password", "test"));
	        sending.add(new PostKey("deviceid", macAddress));
	        String result = TransmissionController.sendToServer(sending, "attackCheck");
	        if(result.equals("yes")) // If return from server is positive, indicating waiting job, break loop and start analysis or something
	        	break;
	        Thread.currentThread().sleep(5000); // Poll every 5 seconds, this can be an options in properties file or options panel.
		}
		return;
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

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
            }
            System.out.println(sb.toString());
            macAddress = sb.toString();
        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e){

            e.printStackTrace();

        }
        return macAddress;
	}
}
