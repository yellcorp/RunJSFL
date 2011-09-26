package org.yellcorp.app.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.yellcorp.app.cli.errors.CommandLineException;
import org.yellcorp.app.cli.errors.RunException;

public class CommandLineRunner
{
	private final CommandLineApplication app;

	public CommandLineRunner(CommandLineApplication app)
	{
		this.app = app;
	}
	
	public void run(String[] args)
	{
		Options optionSet = app.getOptions();
		CommandLineParser parser = new PosixParser();
		CommandLine commandLine;
		
		try {
			commandLine = parser.parse(optionSet, args);
		}
		catch (ParseException pe)
		{
			printHelp(optionSet, pe);
			return;
		}
		
		if (commandLine.hasOption("help"))
		{
			printHelp(optionSet);
			return;
		}
		
		try {
			int exitCode = app.run(commandLine);
			System.exit(exitCode);
		}
		catch (CommandLineException cle)
		{
			printHelp(optionSet, cle);
			System.exit(2);
		}
		catch (RunException re)
		{
			printRunError(re);
			System.exit(1);
		}
	}

	private void printHelp(Options optionSet)
	{
		printHelp(optionSet, null);
	}
	
	private void printHelp(Options optionSet, Throwable cause)
	{
		HelpFormatter hf = new HelpFormatter();
		
		hf.printHelp(app.getApplicationName(), optionSet, true);
		
		if (cause != null)
		{
			System.out.println();
			while (cause != null)
			{
				System.out.println(cause.getMessage());
				cause = cause.getCause();
			}
		}
	}

	private void printRunError(RunException re)
	{
		Throwable current = re;
		
		while (current != null)
		{
			System.out.println(current.getLocalizedMessage());
			current = current.getCause();
		}
	}
}
