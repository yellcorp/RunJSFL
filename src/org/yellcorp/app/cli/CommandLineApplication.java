package org.yellcorp.app.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.yellcorp.app.cli.errors.CommandLineException;
import org.yellcorp.app.cli.errors.RunException;

public interface CommandLineApplication
{
	String getApplicationName();
	Options getOptions();
	int run(CommandLine commandLine) throws CommandLineException, RunException;
}
