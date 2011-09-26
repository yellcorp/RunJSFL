package org.yellcorp.app.jsfl.core;

import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.yellcorp.app.cli.errors.CommandLineException;

public class Common
{
	public static Options getCommonOptions()
	{
		Options options = new Options();
		OptionFactory optionFactory = new OptionFactory(
				ResourceBundle.getBundle("CommonUsageStrings"));
		
		options.addOption(optionFactory.createOption("help", false, false));
		options.addOption(optionFactory.createOption("debug", false, false));
		options.addOption(optionFactory.createOption("flash", true, true));
		options.addOption(optionFactory.createOption("timeout", true, false));
		options.addOption(optionFactory.createOption("poll", true, false));
		
		return options;
	}
	
	
	public static ScriptBridge createBridgeFromOptions(CommandLine commandLine)
	throws CommandLineException
	{
		long timeout = (long) (parseDouble(commandLine, "timeout", "120") * 1000);
		long poll = (long) (parseDouble(commandLine, "poll", "1") * 1000);
		
		TempFileFactory tempFileFactory = null;
		if (commandLine.hasOption("debug"))
		{
			tempFileFactory = new TempFileFactory(".", true);
		}
		return new ScriptBridge(commandLine.getOptionValue("flash"), timeout,
				poll, tempFileFactory);
	}
	
	
	private static double parseDouble(CommandLine commandLine,
			String optionName, String defaultValue) 
	throws CommandLineException
	{
		double value;
		try
		{
			value = Double.parseDouble(commandLine.getOptionValue(optionName,
					defaultValue));
		}
		catch (NumberFormatException nfe)
		{
			throw new CommandLineException("Value for option " + optionName
					+ " must be a number", nfe);
		}
		
		if (Double.isInfinite(value) || Double.isNaN(value))
		{
			throw new CommandLineException("Value for option " + optionName
					+ " must be a finite number");
		}
		else if (value < 0)
		{
			throw new CommandLineException("Value for option " + optionName
					+ " must be positive");
		}
		return value;
	}
}
