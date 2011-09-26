package org.yellcorp.app.jsfl.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.yellcorp.app.jsfl.core.errors.ProcessException;
import org.yellcorp.app.jsfl.core.errors.UnsupportedOSException;
import org.yellcorp.util.ArrayUtil;
import org.yellcorp.util.FileUtil;
import org.yellcorp.util.JavascriptUtil;
import org.yellcorp.util.StringFormatTemplate;

public class ScriptBridge
{
	private static StringFormatTemplate wrapperTemplate = new StringFormatTemplate(
		"(function() { \n" +
		"  fl.outputPanel.clear(); \n" +
		"  var args=@ARGS@; \n" +
		"  (function() { \n" +
		"@SOURCE@ \n" +
		"  }()); \n" +
		"  fl.outputPanel.save(FLfile.platformPathToURI(@LOG@)); \n" +
		"  FLfile.remove(FLfile.platformPathToURI(@LOCK@)); \n" +
		"}()); \n" );
	
	private static String[] cmdPrefix;
	
	private final String flashPath;
	private final long timeout;
	private final long pollTime;
	private TempFileFactory tempFileFactory;
	
	
	{
		String osName = System.getProperty("os.name");
		
		if (osName.startsWith("Windows"))
		{
			cmdPrefix = new String[] { "cmd", "/c", "start" };
		}
		else if (osName.startsWith("Mac OS X"))
		{
			cmdPrefix = new String[] { "open", "-a" };
		}
		else
		{
			cmdPrefix = null;
		}
	}
	
	
	public ScriptBridge(String flashPath, long timeout, long pollTime)
	{
		this(flashPath, timeout, pollTime, null);
	}
	
	public ScriptBridge(String flashPath, long timeout, long pollTime, TempFileFactory tempFileFactory) 
	{
		this.flashPath = flashPath;
		this.timeout = timeout;
		this.pollTime = pollTime;
		
		if (tempFileFactory == null)
		{
			this.tempFileFactory = new TempFileFactory();
		}
		else
		{
			this.tempFileFactory = tempFileFactory;
		}
		
	}
	
	
	public BufferedReader run(File jsflFile, Iterable<String> args) 
	throws IOException, InterruptedException, TimeoutException, UnsupportedOSException, ProcessException 
	{
		return run(FileUtil.readTextFile(jsflFile), jsflFile.getPath(), args);
	}
	
	
	public BufferedReader run(String jsflSource, String arg0, Iterable<String> args) 
	throws IOException, InterruptedException, TimeoutException, UnsupportedOSException, ProcessException
	{
		if (cmdPrefix == null)
		{
			throw new UnsupportedOSException("Operation not supported on this operating system");
		}
		
		File wrappedSourceFile = tempFileFactory.createFile("src", ".jsfl");
		File logFile = tempFileFactory.createFile("log", ".txt");
		File lockFile = tempFileFactory.createFile("bridge", ".lock");
		
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("ARGS", formatScriptArgs(arg0, args));
		fields.put("LOCK", JavascriptUtil.formatStringLiteral(lockFile.getCanonicalPath()));
		fields.put("LOG", JavascriptUtil.formatStringLiteral(logFile.getCanonicalPath()));
		fields.put("SOURCE", jsflSource);
		
		// do source substitution
		String wrappedSourceText = wrapperTemplate.fill(fields);
		
		// write source to source path
		FileUtil.writeTextFile(wrappedSourceFile, wrappedSourceText, "UTF-8");
		
		String[] cmd = ArrayUtil.concat(cmdPrefix, new String[] { 
				flashPath, wrappedSourceFile.getAbsolutePath() });
		
		Process process = Runtime.getRuntime().exec(cmd);
		int exitCode = process.waitFor();
		
		if (exitCode != 0)
		{
			throw new ProcessException(cmd, exitCode);
		}
		
		long waitedTime = 0;
		while (lockFile.exists())
		{
			if (waitedTime > timeout)
			{
				throw new TimeoutException("Timed out waiting for deletion of " + lockFile.getCanonicalPath());
			}
			Thread.sleep(pollTime);
			waitedTime += pollTime;
		}
		
		return new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8"));
	}
	

	private static String formatScriptArgs(String arg0, Iterable<String> args)
	{
		StringBuilder jsArray = new StringBuilder("[ ");
		jsArray.append(JavascriptUtil.formatStringLiteral(arg0));
		for (String arg : args)
		{
			jsArray.append(", ");
			jsArray.append(JavascriptUtil.formatStringLiteral(arg));
		}
		jsArray.append(" ]");
		return jsArray.toString();
	}
}
