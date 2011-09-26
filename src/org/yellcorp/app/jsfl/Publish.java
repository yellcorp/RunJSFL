package org.yellcorp.app.jsfl;

import org.yellcorp.app.cli.CommandLineApplication;
import org.yellcorp.app.cli.CommandLineRunner;
import org.yellcorp.app.jsfl.publish.PublishApplication;

public class Publish
{
	public static void main(String[] args)
	{
		CommandLineApplication app = new PublishApplication();
		new CommandLineRunner(app).run(args);
	}
}
