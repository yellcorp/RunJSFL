package org.yellcorp.app.cli.errors;

public class CommandLineException extends Exception
{
	private static final long serialVersionUID = -3164163356729192296L;

	public CommandLineException()
	{
		super();
	}

	public CommandLineException(String message)
	{
		super(message);
	}

	public CommandLineException(Throwable cause)
	{
		super(cause);
	}

	public CommandLineException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
