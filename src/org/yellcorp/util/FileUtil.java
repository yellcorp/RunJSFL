package org.yellcorp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class FileUtil
{
	static public String readTextFile(String path) throws IOException
	{
		return readTextFile(new File(path), "UTF-8");
	}
	
	static public String readTextFile(String path, String encoding) throws IOException
	{
		return readTextFile(new File(path), encoding);
	}
	
	static public String readTextFile(File path) throws IOException
	{
		return readTextFile(path, "UTF-8");
	}
	
	static public String readTextFile(File path, String encoding) throws IOException
	{
		InputStream byteStream = new FileInputStream(path);
		Reader decoder = new InputStreamReader(byteStream, encoding);
		BufferedReader lineReader = new BufferedReader(decoder);
		
		StringBuilder contents = new StringBuilder();
		String line;
		
		while ((line = lineReader.readLine()) != null)
		{
			contents.append(line);
		}
		lineReader.close();
		decoder.close();
		byteStream.close();
		
		return contents.toString();
	}
	
	static public void writeTextFile(String path, String contents) throws IOException
	{
		writeTextFile(new File(path), contents, "UTF-8");
	}
	
	static public void writeTextFile(String path, String contents, String encoding) throws IOException
	{
		writeTextFile(new File(path), contents, encoding);
	}
	
	static public void writeTextFile(File path, String contents) throws IOException
	{
		writeTextFile(path, contents, "UTF-8");
	}
	
	static public void writeTextFile(File path, String contents, String encoding) throws IOException
	{
		OutputStream byteStream = new FileOutputStream(path);
		Writer encoder = new OutputStreamWriter(byteStream, encoding);
		
		encoder.write(contents);
		encoder.close();
		byteStream.close();
	}
}
