package org.yellcorp.app.jsfl;

import org.yellcorp.app.cli.CommandLineApplication;
import org.yellcorp.app.cli.CommandLineRunner;
import org.yellcorp.app.jsfl.core.RunJSFLApplication;

public class RunJSFL
{
	public static void main(String[] args)
	{
		CommandLineApplication app = new RunJSFLApplication();
		new CommandLineRunner(app).run(args);
	}
}
