package org.yellcorp.app.jsfl.publish;

public class FlashCompilerMessage
{
	public static enum Type { ERROR, WARNING }

	public Type type;
	public String location;
	public String description;
}
