package org.yellcorp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

public class Globber
{
	private HashSet<File> result;

	
	public Globber()
	{
		result = new HashSet<File>();
	}
	
	
	public Set<File> getResult()
	{
		return Collections.unmodifiableSet(result);
	}


	public void find(String string, String glob)
	{
		find(new File(string), glob);
	}
	
	
	public void find(File folder, String glob)
	{
		if (File.separatorChar != '/')
		{
			glob = glob.replace(File.separatorChar, '/');
		}
		int slash = glob.indexOf('/');
		if (slash >= 0)
		{
			String immediate = glob.substring(0, slash);
			String remainder = glob.substring(slash + 1);
			
			if (remainder.length() == 0) remainder = "**";
			
			findNode(folder, immediate, remainder);
		}
		else
		{
			findLeaf(folder, glob);
		}
	}

	
	private void findNode(File folder, String immediateGlob, String remainderGlob)
	{
		List<File> matchingChildren = new ArrayList<File>(); 
		
		if (immediateGlob.equals("**"))
		{
			addRecursive(folder, matchingChildren, false);
		}
		else
		{
			Pattern regex = globToRegex(immediateGlob);
			for (File query : folder.listFiles())
			{
				if (query.isDirectory() && regex.matcher(query.getName()).matches())
				{
					matchingChildren.add(query);
				}
			}
		}
		
		for (File subfolder : matchingChildren)
		{
			find(subfolder, remainderGlob);
		}
	}


	private void findLeaf(File folder, String glob)
	{
		if (glob.equals("**"))
		{
			addRecursive(folder, result, true);
		}
		else
		{
			Pattern regex = globToRegex(glob);
			for (File file : folder.listFiles())
			{
				if (regex.matcher(file.getName()).matches())
					result.add(file);
			}
		}
	}

	
	private static void addRecursive(File folder, Collection<File> target, boolean includeFiles)
	{
		Stack<File> pending = new Stack<File>();
		pending.push(folder);
		
		while (pending.size() > 0)
		{
			File subfolder = pending.pop();
			target.add(subfolder);
			for (File file : subfolder.listFiles())
			{
				if (file.isDirectory())
				{
					pending.push(file);
				}
				else if (includeFiles)
				{
					target.add(file);
				}
			}
		}
	}


	public static Pattern globToRegex(String glob)
	{
		StringBuilder regex = new StringBuilder("^");
		
		for (int i = 0; i < glob.length(); i++)
		{
			char c = glob.charAt(i);
			switch (c)
			{
			case '*':
				regex.append(".*");
				break;
				
			case '?':
				regex.append(".");
				break;
				
			case '(': case ')': case '[': case ']':
			case '{': case '}': case '.': case '^':
			case '-': case '$': case '|': case '+':
			case '\\':
				regex.append("\\");
			default:
				regex.append(c);
				break;
			}
		}
		regex.append('$');
		return Pattern.compile(regex.toString());
	}
}
