package org.yellcorp.app.jsfl.script;

import java.util.HashMap;

import org.yellcorp.app.jsfl.publish.FlashCompilerResult;
import org.yellcorp.util.JavascriptUtil;
import org.yellcorp.util.StringFormatTemplate;

public class JSFLScript
{
	private static StringFormatTemplate argWrapperTemplate = new StringFormatTemplate(
			"var args=@ARGS@; \n" +
			"(function() { \n" +
			"@SOURCE@ \n" +
			"}()); \n" );
	
	private final String source;
	private Iterable<String> args;
	private final String name;
	
	private String output;
	private FlashCompilerResult compilerResult;


	public JSFLScript(String source, String name, Iterable<String> args)
	{
		this.source = source;
		this.name = name;
		this.args = args;
	}
	
	public void run()
	{
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("ARGS", formatScriptArgs(name, args));
		fields.put("SOURCE", source);
	}
	
	public String getOutput()
	{
		return output;
	}
	
	public FlashCompilerResult getCompilerResult()
	{
		return compilerResult;
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
