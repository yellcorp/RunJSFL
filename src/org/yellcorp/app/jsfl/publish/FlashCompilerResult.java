package org.yellcorp.app.jsfl.publish;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yellcorp.app.jsfl.publish.FlashCompilerMessage.Type;

public class FlashCompilerResult implements Iterable<FlashCompilerMessage>
{
	private static Pattern messagePattern = Pattern.compile(
			"\\*\\*(Error|Warning)\\*\\* ([^\\t]+)\\t(.+)");
	
	private static Pattern tallyPattern = Pattern.compile(
			"(\\d+) Error\\(s\\), (\\d+) Warning\\(s\\)");
	
	private List<FlashCompilerMessage> messages;
	private int errorCount = 0;
	private int warningCount = 0;
	
	public FlashCompilerResult()
	{
		messages = new ArrayList<FlashCompilerMessage>();
	}
	
	public List<FlashCompilerMessage> getMessages()
	{
		return Collections.unmodifiableList(messages);
	}

	@Override
	public Iterator<FlashCompilerMessage> iterator()
	{
		return getMessages().iterator();
	}
	
	public void parse(BufferedReader stream) throws IOException, ParseError
	{
		String line;
		Matcher matcher;
		boolean expectEOF = false;
		
		while ((line = stream.readLine()) != null)
		{
			if (line.length() == 0)
				continue;
			
			if (expectEOF)
				throw new ParseError("Expected end of file");
			
			matcher = messagePattern.matcher(line);
			if (matcher.matches())
			{
				FlashCompilerMessage message = new FlashCompilerMessage();
				String messageType = matcher.group(1);
				
				if (messageType.equals("Error"))
				{
					message.type = Type.ERROR;
					errorCount++;
				}
				else if (messageType.equals("Warning"))
				{
					message.type = Type.WARNING;
					warningCount++;
				}
				else
				{
					throw new ParseError("Unrecognized message type: " + messageType);
				}
				message.location = matcher.group(2);
				message.description = matcher.group(3);
				messages.add(message);
			}
			else
			{
				matcher = tallyPattern.matcher(line);
				if (matcher.matches())
				{
					int verifyErrorCount = Integer.parseInt(matcher.group(1), 10);
					int verifyWarningCount = Integer.parseInt(matcher.group(2), 10);
					
					if (verifyErrorCount != errorCount)
					{
						throw new ParseError("Mismatched error count");
					}
					else if (verifyWarningCount != warningCount)
					{
						throw new ParseError("Mismatched warning count");
					}
					expectEOF = true;
				}
				else
				{
					throw new ParseError("Line doesn't conform to a known pattern");
				}
			}
		}
		if (!expectEOF)
			throw new ParseError("Early end of file");
	}
	
	public int getErrorCount()
	{
		return errorCount;
	}

	public int getWarningCount()
	{
		return warningCount;
	}
	
	public int getMessageCount()
	{
		return messages.size();
	}

	public static class ParseError extends Exception
	{
		private static final long serialVersionUID = 7204217467220864581L;
		public ParseError(String message)
		{
			super(message);
		}
	}
}
