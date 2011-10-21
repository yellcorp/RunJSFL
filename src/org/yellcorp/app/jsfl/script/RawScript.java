package org.yellcorp.app.jsfl.script;

import java.util.HashMap;

import org.yellcorp.util.JavascriptUtil;
import org.yellcorp.util.StringFormatTemplate;

public class RawScript
{
	private static StringFormatTemplate runTemplate = new StringFormatTemplate(
			"(function() { \n" +
			"  fl.outputPanel.clear(); \n" +
			"  fl.compilerErrors.clear(); \n" +
			"@SOURCE@ \n" +
			"  FLfile.write(FLfile.platformPathToURI(@SUCCESS@), '1'); \n" +
			"}()); \n" );
	
	private static StringFormatTemplate signalTemplate = new StringFormatTemplate(
			"(function() { \n" +
			"  fl.outputPanel.save(FLfile.platformPathToURI(@OUTPUT@)); \n" +
			"  fl.compilerErrors.save(FLfile.platformPathToURI(@ERRORS@)); \n" +
			"  FLfile.remove(FLfile.platformPathToURI(@LOCK@)); \n" +
			"}()); \n" );

	private String source;
	
	public RawScript(String source)
	{
		this.source = source;
	}
	
	public void run()
	{
		runScript();
		runSignal();
		
		long waitedTime = 0;
		while (lockFile.exists())
		{
			if (waitedTime > timeout)
			{
				//
			}
			Thread.sleep(pollTime);
			waitedTime += pollTime;
		}
		
		// check 'success' file
		// if success
		//   parse errors
		// else
		//   parse output for js errors
	}

	private void runScript()
	{
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("SOURCE", source);
		fields.put("SUCCESS", JavascriptUtil.formatStringLiteral(successFile.getCanonicalPath()));
	}

	private void runSignal()
	{
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("OUTPUT", JavascriptUtil.formatStringLiteral(outputFile.getCanonicalPath()));
		fields.put("ERRORS", JavascriptUtil.formatStringLiteral(errorsFile.getCanonicalPath()));
		fields.put("LOCK", JavascriptUtil.formatStringLiteral(lockFile.getCanonicalPath()));
	}
}
