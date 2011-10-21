package org.yellcorp.app.jsfl.core;

import org.yellcorp.util.StringFormatTemplate;

public class ScriptRunner
{
	private static StringFormatTemplate scriptWrapper = new StringFormatTemplate(
			"(function() { \n" +
			"  fl.outputPanel.clear(); \n" +
			"  var args=@ARGS@; \n" +
			"  (function() { \n" +
			"@SOURCE@ \n" +
			"  }()); \n" +
			"  FLfile.remove(FLfile.platformPathToURI(@LOCK@)); \n" +
			"}()); \n" );
	
	private static StringFormatTemplate outputWriter = new StringFormatTemplate(
			"(function() { \n" +
			"  fl.outputPanel.save(FLfile.platformPathToURI(@LOG@)); \n" +
			"}()); \n" );
	
	public void run(String jsflSource, String arg0, Iterable<String> args)
	{
		
	}
}
