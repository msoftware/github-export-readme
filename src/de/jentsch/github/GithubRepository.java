package de.jentsch.github;

import java.util.Arrays;

public class GithubRepository {

	public String name = null;
	public String[] languages = null;
	public String description = null;
	public boolean fork = false;
	public String url = null;

	public void printOut (String title)
	{
		System.out.println("# GithubRepository (" + title + ")");
		System.out.println("name:" + name);
		System.out.println("description:" + description);
		System.out.println("fork:" + fork);
		System.out.println("url:" + url);
	}
}
