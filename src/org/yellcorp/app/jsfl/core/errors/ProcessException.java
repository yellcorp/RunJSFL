package org.yellcorp.app.jsfl.core.errors;

import org.yellcorp.util.ArrayUtil;

public class ProcessException extends Exception
{
	private static final long serialVersionUID = -5951883413620632443L;

	public ProcessException(String string)
	{
		super(string);
	}
	
	public ProcessException(String[] cmd, int exitCode)
	{
		this("Command returned " + exitCode + ": " + ArrayUtil.join(cmd, " "));
	}
}
