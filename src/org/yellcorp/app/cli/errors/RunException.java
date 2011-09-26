package org.yellcorp.app.cli.errors;

public class RunException extends Exception
{
	private static final long serialVersionUID = -746398124675646864L;

	public RunException()
	{
		super();
	}

	public RunException(String message)
	{
		super(message);
	}

	public RunException(Throwable cause)
	{
		super(cause);
	}

	public RunException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
