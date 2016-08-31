package Threads;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.json.JSONException;

import controllers.TransmissionController;
import objects.PostKey;

public class JobPollThread {

	public static boolean terminationSwitch = false; // I think this can be altered outside thread, will force loop termination and return.
	private static String macAddress = "";
	
	public void pollJobs() throws JSONException, IOException, InterruptedException
	{
		getMac(); // Get unique identifier for the pc, this can be spoofed but in this case it's unlikely.
		while(terminationSwitch == false)
		{
			
			ArrayList<PostKey> sending = new ArrayList<PostKey>();
	        sending.add(new PostKey("password", "test"));
	        sending.add(new PostKey("deviceid", macAddress));
	        String result = TransmissionController.sendToServer(sending, "jobCheck");
	        if(Integer.parseInt(result) == 1) // If return from server is positive, indicating waiting job, break loop and start analysis or something
	        	break;
	        Thread.currentThread().sleep(5000); // Poll every 5 seconds, this can be an options in properties file or options panel.
		}
		return;
	}
	
	private String getMac() throws SocketException
	{
		String mac = "";
		Enumeration<NetworkInterface> adapters = NetworkInterface.getNetworkInterfaces();
        byte[] macBytes = adapters.nextElement().getHardwareAddress();
        for (int k = 0; k < macBytes.length; k++) {
           mac =  String.format("%02X%s", macBytes[k], (k < macBytes.length - 1) ? "-" : "");
        }
        return mac;
	}
}
