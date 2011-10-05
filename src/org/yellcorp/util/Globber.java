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
	private static final Pattern WINDOWS_DRIVE_ABSOLUTE_PATH =
			Pattern.compile("[A-Za-z]:[/\\\\]");
	
	private static final Pattern WINDOWS_DRIVE_RELATIVE_PATH =
			Pattern.compile("[A-Za-z]:");
	
	private HashSet<File> result;

	
	public Globber()
	{
		result = new HashSet<File>();
	}
	
	
	public Set<File> getResult()
	{
		return Collections.unmodifiableSet(result);
	}


	public void find(String basePath, String glob)
	{
		find(new File(basePath), glob);
	}
	
	
	public void find(File basePath, String glob)
	{
		// check for root in glob
		if (File.separatorChar == '/' && glob.startsWith("/"))
		{
			basePath = new File("/");
			glob = glob.substring(1);
		}
		else if (File.separatorChar == '\\')
		{
			if (glob.startsWith("\\\\"))
			{
				int serverNameSlash = glob.indexOf('\\', 2);
				if (serverNameSlash > -1)
				{
					basePath = new File(glob.substring(0, serverNameSlash + 1));
					glob = glob.substring(serverNameSlash + 1);
				}
			}
			else if (WINDOWS_DRIVE_ABSOLUTE_PATH.matcher(glob).lookingAt())
			{
				// absolute drive & path
				basePath = new File(glob.substring(0, 2) + "\\");
				glob = glob.substring(3);
			}
			else if (WINDOWS_DRIVE_RELATIVE_PATH.matcher(glob).lookingAt())
			{
				// absolute drive, relative path
				basePath = new File(glob.substring(0, 2));
				glob = glob.substring(2);
			}
		}
		findBase(basePath, glob);
	}
	
	private void findBase(File basePath, String glob)
	{
		int sep = glob.indexOf(File.separatorChar);
		if (sep >= 0)
		{
			String immediate = glob.substring(0, sep);
			String remainder = glob.substring(sep + 1);
			findNode(basePath, immediate, remainder);
		}
		else
		{
			findLeaf(basePath, glob);
		}
	}

	
	private void findNode(File folder, String immediateGlob, String remainderGlob)
	{
		List<File> matchingChildren = new ArrayList<File>(); 
		
		if (remainderGlob.length() == 0)
		{
			remainderGlob = "**";
		}
		
		if (immediateGlob.equals("**"))
		{
			addRecursive(folder, matchingChildren, false);
		}
		else if (containsGlobWildcards(immediateGlob))
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
		else
		{
			File testSubdir = new File(folder, immediateGlob);
			if (testSubdir.exists() && testSubdir.isDirectory())
				matchingChildren.add(testSubdir);
		}
		
		for (File subfolder : matchingChildren)
		{
			findBase(subfolder, remainderGlob);
		}
	}


	private void findLeaf(File folder, String glob)
	{
		if (glob.length() == 0 || glob.equals("**"))
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
	
	
	public static boolean containsGlobWildcards(String glob)
	{
		return glob.indexOf('*') >= 0 || glob.indexOf('?') >= 0;
	}
}
