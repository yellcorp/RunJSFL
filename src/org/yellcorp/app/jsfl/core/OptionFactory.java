package org.yellcorp.app.jsfl.core;

import java.util.ResourceBundle;

import org.apache.commons.cli.Option;

public class OptionFactory
{
	private ResourceBundle bundle;

	public OptionFactory(ResourceBundle bundle)
	{
		this.bundle = bundle;
	}

	public Option createOption(String longName, boolean hasArg, boolean required)
	{
		return createOption(longName, null, hasArg, required);
	}

	public Option createOption(String longName, String shortName, boolean hasArg, boolean required)
	{
		String keyPrefix = "usage." + longName + ".";
		
		Option option = new Option(shortName, longName, hasArg, 
				bundle.getString(keyPrefix + "description"));
		option.setRequired(required);
		
		if (hasArg)
		{
			option.setArgName(bundle.getString(keyPrefix + "arg"));
		}
		return option;
	}
}
