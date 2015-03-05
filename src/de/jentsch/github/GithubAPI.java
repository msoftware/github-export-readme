package de.jentsch.github;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import org.json.*;
import org.apache.commons.codec.binary.Base64;

public class GithubAPI extends MyHttpClient {

	/* 
	Read more about the accessToken at:
	https://help.github.com/articles/creating-an-access-token-for-command-line-use/
	*/
	private static String accessToken = "YOUR ACCES TOKEN";

	public static Response fetchContent (String url)
		throws Exception
	{
		String authString = accessToken + ":x-oauth-basic";
		Response response = fetchContent (url, authString);
		try { Thread.sleep (2000); }catch (Exception e) {};
		return response;
	}

	public static Hashtable<String,String> getPagination (String linkheader)
	{
		Hashtable<String,String> ret = new Hashtable<String,String>();
		// TODO
		String tmp = linkheader.substring (1, linkheader.length()-1);
		String[] links = tmp.split (",");
		for (int i = 0; i < links.length; i++)
		{
			String link = links[i].trim();
			String linkdata[] = link.split ("; rel=");
			String linkname = linkdata[1].replaceAll("\"", "");
			String linkvalue = linkdata[0].substring (1, linkdata[0].length()-1);
			// System.out.println (linkname);
			// System.out.println (linkvalue);
			ret.put (linkname, linkvalue);
		}
		return ret;
	}

	public static String fetchRepositoryFileContents (String url)
		throws Exception
	{
		Response response = fetchContent (url);
		try {
			String content = response.content;
			JSONObject jsonReadme = new JSONObject(content);
			String readme = jsonReadme.getString ("content");
			readme = new String( Base64.decodeBase64(readme.getBytes("utf-8")));
			return readme;
		} catch (Exception e)
		{
			return "## No readme file in this project";
		}
	}

	public static ArrayList<GithubRepository> fetchRepositories (String url, boolean recursive)
		throws Exception
	{
		ArrayList<GithubRepository> ret = new ArrayList<GithubRepository>();
		return fetchRepositories (ret, url, recursive);
	}
	
	public static ArrayList<GithubRepository> fetchRepositories (ArrayList<GithubRepository> ret, String url, boolean recursive)
		throws Exception
	{
		Response response = fetchContent (url);
		String content = response.content;
		JSONArray arr = new JSONArray(content);
		Hashtable<String,String> header = response.header;
		String linkheader = header.get("Link");
		// System.out.println (linkheader);
		Hashtable<String,String> pagination = getPagination (linkheader);

		for (int i = 0; i < arr.length(); i++)
		{
		    GithubRepository gr = new GithubRepository();
		    JSONObject repo = arr.getJSONObject(i); //.toString (1));
		    String login = repo.getJSONObject("owner").getString ("login");
		    gr.name = repo.getString("name");
		    try {
		    	gr.description = repo.getString("description");
		    } catch (Exception e) {
			gr.description = "";
		    }
		    gr.url = repo.getString("url");
		    gr.fork = repo.getBoolean("fork");
		    // gr.printOut("Nr. " + i);
		    // if (i > 2) System.exit (0); // TODO entfernen
		    ret.add(gr);
		}
		if (pagination.get("next") != null && recursive)
		{
			// System.out.println ("Next: " + pagination.get("next"));
			return fetchRepositories (ret, pagination.get("next"), recursive);
		}
		return ret;
	}
}
