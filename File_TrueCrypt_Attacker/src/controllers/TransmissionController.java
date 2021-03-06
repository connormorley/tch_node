package controllers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import objects.PostKey;


/*	Created by:		Connor Morley
 * 	Title:			TCrunch Node Transmissions Controller
 *  Version update:	1.8
 *  Notes:			Class is used to conduct communication via HTTPURL protocol between node and control server. Class is templated across
 *  				all components within the system. Class conducts POST requests by default with timeout constraints. Return values are 
 *  				checked and errors handles appropriately under special circumstances where expected. HTTPS controls have been included
 *  				but commented in order for easy conversion in later versions. Further testing and configuration options will be required
 *  				for this feature.
 *  
 *  References:		N/A
 */

public class TransmissionController {

    public static String ipAddress = "";
    public static boolean jobCheck = false;
    public static boolean firstFailedCheck = true;
    //public static SSLContext sslcontex;
	
	public static String sendToServer(List<PostKey> sending, String command) throws IOException, JSONException {
        // Add custom implementation, as needed.
        String ret = "";
        try {
            //HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            //SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            //HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            //conn.setSSLSocketFactory(sslcontex.getSocketFactory());
            URL url = new URL("http://" + ipAddress + "/" + command);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            for(PostKey inTransit : sending)
            {
                params.add(new BasicNameValuePair(inTransit.getKey(), inTransit.getValue()));
            }

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();

            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            String tester = response.toString();
            if (tester.equals(""))
                tester = "Success";

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ret = tester;
            }else if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            {
                System.out.println("server not found");
                ret = "Server not found";
            }
            else {
                ret = "Error in data tranmission, please check your network connection " + conn.getHeaderFields().toString();
            }
        } catch (MalformedURLException e) {
            System.out.println(e);
            System.out.println("url");
            return "Incorrect server address format, please check and try again.";
        } catch (IOException e) {
        	if(jobCheck)
        	{
        		if(firstFailedCheck)
        		{
        			System.out.println("Server has disconnected.");
        			firstFailedCheck = false;
        		}
        		return "no";
        	}
        	if(command.equals("issueBenchmark"))
        	{
        		System.out.println("Server unavailable at boot, please check connection and re-try. \nTERMINATING");
        		System.exit(0);
        	}
        	else
        	{
	            System.out.println(e);
	            System.out.println("io");
	            throw new IOException();
        	}
        } catch (Exception e) {
            System.out.println("say something");
            e.printStackTrace();
        }
        if(jobCheck && !firstFailedCheck)
        {
        	firstFailedCheck = true;
        	System.out.println("Server has connected.");
        }
        return ret;
    }
	
	public static String sendGet(String command) throws Exception {
        String url = "http://"+ipAddress+"/" + command;
        //SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        //HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
        //con.setSSLSocketFactory(sslcontex.getSocketFactory());
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String ret = response.toString();
        //print result
        System.out.println(response.toString());
        return ret;

    }
	
	 private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
	        StringBuilder result = new StringBuilder();
	        boolean first = true;
	        for (NameValuePair pair : params) {
	            if (first) {
	                first = false;
	            } else {
	                result.append("&");
	            }

	            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	            result.append("=");
	            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	        }
	        return result.toString();
	    }
	 
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	 
/*	 public static void setCerts() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
	        // Load CAs from an InputStream
	// (could be from a resource or ByteArrayInputStream or ...)
	        CertificateFactory cf = CertificateFactory.getInstance("X.509");
	// From https://www.washington.edu/itconnect/security/ca/load-der.crt
	        //String uri= Environment.getExternalStorageDirectory().toString();
	        String uri= "./server.cer";
	        //uri=uri+"/server.cer";
	        File cer = new File(uri);
	        InputStream caInput = new BufferedInputStream(new FileInputStream(cer));
	        Certificate ca;
	        try {
	            ca = cf.generateCertificate(caInput);
	            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
	        } finally {
	            caInput.close();
	        }

	// Create a KeyStore containing our trusted CAs
	        String keyStoreType = KeyStore.getDefaultType();
	        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
	        keyStore.load(null, null);
	        keyStore.setCertificateEntry("ca", ca);

	// Create a TrustManager that trusts the CAs in our KeyStore
	        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
	        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
	        tmf.init(keyStore);

	// Create an SSLContext that uses our TrustManager
	        sslcontex = SSLContext.getInstance("TLS");
	        sslcontex.init(null, tmf.getTrustManagers(), null);
	    }*/
}
