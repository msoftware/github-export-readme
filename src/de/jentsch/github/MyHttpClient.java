package de.jentsch.github;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.codec.binary.Base64;

public class MyHttpClient {

	public static class Response {
		public String content;
		public Hashtable<String,String> header;
	}

	public static MyHttpClient.Response fetchContent (String url, String authString )
                throws Exception
        {
		System.out.println ("Fetch: " + url);
                String content = "";
		Response response = new Response();
                URL urlObj = new URL(url);
                URLConnection conn = urlObj.openConnection();
		if (authString != null)
		{
			String basicAuth = "Basic " + new String( Base64.encodeBase64(authString.getBytes("utf-8")));
			conn.setRequestProperty ("Authorization", basicAuth);
		}

                Map<String, List<String>> map = conn.getHeaderFields();
		Hashtable<String,String> header = new Hashtable<String,String>();
                for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue());
			header.put (entry.getKey() + "", entry.getValue() + "");
                }
		response.header = header;

		int code = 0;
		if ( conn instanceof HttpURLConnection)
		{
			HttpURLConnection httpConnection = (HttpURLConnection) conn;
			code = httpConnection.getResponseCode();
		}
		if (code == 200)
		{
			// Get Content
			BufferedReader in = new BufferedReader(new InputStreamReader( conn.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) content += inputLine + "\r\n";
			in.close();
			try { Thread.sleep (1500); }catch (Exception e) {};
			response.content = content;
		} else {
			response.content = "";
		}
                return response;
        }
}
