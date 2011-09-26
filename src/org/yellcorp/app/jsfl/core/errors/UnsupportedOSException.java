package org.yellcorp.app.jsfl.core.errors;

public class UnsupportedOSException extends Exception
{
	private static final long serialVersionUID = -9118829627651387260L;
	
	public UnsupportedOSException()
	{
		super();
	}
	
	public UnsupportedOSException(String string)
	{
		super(string);
	}
	
	public UnsupportedOSException(Throwable cause)
	{
		super(cause);
	}
	
	public UnsupportedOSException(String string, Throwable cause)
	{
		super(string, cause);
	}
}
