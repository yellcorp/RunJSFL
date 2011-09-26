package org.yellcorp.app.jsfl.publish;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.yellcorp.app.cli.CommandLineApplication;
import org.yellcorp.app.cli.errors.CommandLineException;
import org.yellcorp.app.cli.errors.RunException;
import org.yellcorp.app.jsfl.core.Common;
import org.yellcorp.app.jsfl.core.OptionFactory;
import org.yellcorp.app.jsfl.core.ScriptBridge;
import org.yellcorp.util.FileSetResolver;

public class PublishApplication implements CommandLineApplication
{
	private ScriptBridge bridge;

	@Override
	public String getApplicationName()
	{
		return "publish";
	}
	
	@Override
	public Options getOptions()
	{
		Options options = Common.getCommonOptions();
		
		OptionFactory optionFactory = new OptionFactory(
				ResourceBundle.getBundle("PublishUsageStrings"));
		
		options.addOption(optionFactory.createOption("move", "m", true, false));
		
		return options;
	}
	
	@Override
	public void run(CommandLine commandLine) throws CommandLineException,
			RunException
	{
		bridge = Common.createBridgeFromOptions(commandLine);
		
		String[] args = commandLine.getArgs();
		FileSetResolver resolver = new FileSetResolver();
		File currentFolder = new File(".");
		for (String arg : args)
		{
			try	{
				resolver.add(currentFolder, arg);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (File fla : resolver.getFiles())
		{
			publish(fla);
		}
	}

	private void publish(File fla)
	{
		output = bridge.run(jsflFile, Arrays.asList(fla.getCanonicalPath()))
	}
}
