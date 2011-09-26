package org.yellcorp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileSetResolver
{
	private Set<File> visitedListFiles;
	private Set<File> resolvedSet;

	public FileSetResolver()
	{
		visitedListFiles = new HashSet<File>();
		resolvedSet = new HashSet<File>();
	}
	

	public void add(File baseFolder, String arg) throws IOException
	{
		if (arg.startsWith("@"))
		{
			addFromListFile(new File(baseFolder, arg.substring(1)));
		}
		else if (arg.indexOf('*') >= 0 || arg.indexOf('?') >= 0)
		{
			addPattern(baseFolder, arg);
		}
		else
		{
			add(new File(baseFolder, arg));
		}
	}
	
	public Set<File> getFiles()
	{
		return Collections.unmodifiableSet(resolvedSet);
	}

	private void add(File file) throws IOException
	{
		resolvedSet.add(file.getCanonicalFile());
	}
	

	private void addFromListFile(File listFile) throws IOException
	{
		if (visitedListFiles.contains(listFile.getCanonicalFile())) return;
		visitedListFiles.add(listFile.getCanonicalFile());
		
		InputStream byteStream = new FileInputStream(listFile);
		Reader decoder = new InputStreamReader(byteStream, "UTF-8");
		BufferedReader lineReader = new BufferedReader(decoder);
		
		String line;
		List<String> contents = new ArrayList<String>();
		while ((line = filterLine(lineReader.readLine())) != null)
		{
			if (line.length() > 0) contents.add(line);
		}
		lineReader.close(); decoder.close(); byteStream.close();
		
		File listFileParent = listFile.getParentFile();
		for (String spec : contents)
		{
			add(listFileParent, spec);
		}
	}
	

	private void addPattern(File baseFolder, String pattern) 
	throws IOException
	{
		Globber globber = new Globber();
		globber.find(baseFolder, pattern);
		for (File file : globber.getResult())
		{
			add(file);
		}
	}
	
	
	private static String filterLine(String line)
	{
		if (line == null) return null;
		
		int hash = line.indexOf('#');
		if (hash >= 0)
		{
			line = line.substring(0, hash);
		}
		return line.trim();
	}
}
