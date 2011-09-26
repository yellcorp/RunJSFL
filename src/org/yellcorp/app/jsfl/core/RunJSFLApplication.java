package org.yellcorp.app.jsfl.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.yellcorp.app.cli.CommandLineApplication;
import org.yellcorp.app.cli.errors.CommandLineException;
import org.yellcorp.app.cli.errors.RunException;
import org.yellcorp.app.jsfl.core.errors.ProcessException;
import org.yellcorp.app.jsfl.core.errors.UnsupportedOSException;

public class RunJSFLApplication implements CommandLineApplication
{
	@Override
	public String getApplicationName()
	{
		return "runjsfl";
	}

	@Override
	public Options getOptions()
	{
		return Common.getCommonOptions();
	}

	@Override
	public void run(CommandLine commandLine) 
	throws CommandLineException, RunException
	{
		String[] args = commandLine.getArgs();
		
		if (args.length < 1)
		{
			throw new CommandLineException("No JSFL script specified");
		}
		ScriptBridge bridge = Common.createBridgeFromOptions(commandLine);
		File jsflFile = new File(args[0]);
		ArrayList<String> scriptArgs = new ArrayList<String>();
		for (int i = 1; i < args.length; i++)
			scriptArgs.add(args[i]);
		
		try	{
			BufferedReader scriptOut = bridge.run(jsflFile, scriptArgs);
			String line;
			while ((line = scriptOut.readLine()) != null)
			{
				System.out.println(line);
			}
		}
		catch (IOException e)
		{
			throw new RunException(e);
		}
		catch (InterruptedException e)
		{
			throw new RunException(e);
		}
		catch (TimeoutException e)
		{
			throw new RunException(e);
		}
		catch (UnsupportedOSException e)
		{
			throw new RunException(e);
		}
		catch (ProcessException e)
		{
			throw new RunException(e);
		}
	}
}
