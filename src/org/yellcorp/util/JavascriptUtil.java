package org.yellcorp.util;

public class JavascriptUtil
{
	static private String lowEscapes[] = {
		"\\u0000", "\\u0001", "\\u0002", "\\u0003", 
		"\\u0004", "\\u0005", "\\u0006", "\\u0007", 
		"\\b",     "\\t",     "\\n",     "\\v", 
		"\\f",     "\\r",     "\\u000e", "\\u000f",

		"\\u0010", "\\u0011", "\\u0012", "\\u0013", 
		"\\u0014", "\\u0015", "\\u0016", "\\u0017", 
		"\\u0018", "\\u0019", "\\u001a", "\\u001b", 
		"\\u001c", "\\u001d", "\\u001e", "\\u001f"
	};
	
	static public String formatStringLiteral(String str)
	{
		return formatStringLiteral(str, '"');
	}
	
	static public String formatStringLiteral(String str, char quoteChar)
	{
		StringBuilder literal = new StringBuilder();
		char c;
		
		literal.append(quoteChar);
		for (int i = 0; i < str.length(); i++)
		{
			c = str.charAt(i);
			if (c == quoteChar)
			{
				literal.append('\\');
				literal.append(c);
			}
			else if (c == '\\')
			{
				literal.append("\\\\");
			}
			else if (c < ' ')
			{
				literal.append(lowEscapes[c]);
			}
			else if (c > '~')
			{
				literal.append(String.format("\\u%04x", (int)c));
			}
			else 
			{
				literal.append(c);
			}
		}
		literal.append(quoteChar);
		return literal.toString();
	}
}
