package de.jentsch.github;

import java.io.*;
import java.util.*;
import org.markdown4j.Markdown4jProcessor;

public class GithubRepositories 
{

	public ArrayList<GithubRepository> fetchAllGithubRepositories (String username, boolean recursive)
		throws Exception
	{
		String url = "https://api.github.com/users/" + username + "/repos?per_page=100";
		return GithubAPI.fetchRepositories (url, recursive);
	}

	public String fetchRepositoryReadme (String username, String repository)
		throws Exception
	{
		String ret = "";
		String url = "https://api.github.com/repos/" + username + "/" + repository + "/readme";
		return GithubAPI.fetchRepositoryFileContents (url);
	}

	public static void main (String[] arg)
		throws Exception
	{	
		String username;
		if (arg.length == 0) 
		{
			System.out.println ("Error: \r\nMissing github login");
			System.out.println ("");
			System.out.println ("Example: \r\njava de.jentsch.github.GithubRepositories msoftware");
			System.out.println ("");
			System.exit (-1);
		} else {
			username = arg[0].trim ();
			boolean recursive = true; // false; // Max. the first 100 repos
			PrintWriter writer = new PrintWriter(username + "-github-repositories.html", "UTF-8");
			GithubRepositories gr = new GithubRepositories();
			ArrayList<GithubRepository> repos = gr.fetchAllGithubRepositories(username, recursive);
			System.out.println ("Found " + repos.size() + " repositories for user " + username);
			Iterator<GithubRepository> i = repos.iterator();
			int count = 1;
			while (i.hasNext())
			{
				GithubRepository repo = i.next();
				System.out.println ( count + " Process " + repo.name);
				writer.println ("<h1>" +
						"<a href='https://github.com/" + username + "'>" + username + "</a>" +
						" / <a href='https://github.com/" + username + "/" + repo.name + 
						"'>" + repo.name + "</a></h1>" +
						"<h3>Description</h3><p>" + repo.description + "</p>");
				String readme = gr.fetchRepositoryReadme (username, repo.name);
				readme = readme.replaceAll("#", "##");
				String readmeHTML = new Markdown4jProcessor().process(readme);
				// System.out.println (readmeHTML);
				writer.println (readmeHTML);
				count++;
			}
			writer.close ();
		}

	}
}
