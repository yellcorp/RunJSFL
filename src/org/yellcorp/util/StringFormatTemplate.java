package org.yellcorp.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatTemplate
{
	private static final Pattern tokenizePattern = Pattern.compile("@[^@]+@");
	
	private Queue<Renderer> renderers;
	private Set<String> fields;

	private final String template;
	
	public StringFormatTemplate(String template)
	{
		this.template = template;
		parse();
	}

	public String fill(Map<String, ? extends Object> fields)
	{
		StringBuilder result = new StringBuilder();
		for (Renderer r : renderers)
		{
			result.append(r.render(fields));
		}
		return result.toString();
	}
	
	public String getTemplate()
	{
		return template;
	}
	
	public Set<String> getFields()
	{
		return Collections.unmodifiableSet(fields);
	}
	
	public boolean hasField(String query)
	{
		return fields.contains(query);
	}
	
	/*
	@Override
	public String toString()
	{
		return "StringFormatTemplate [" +
		       StringUtils.join(renderers, ", ");
	}
	*/
	
	private void parse()
	{
		Matcher matcher = tokenizePattern.matcher(template);
		renderers = new LinkedList<Renderer>();
		fields = new HashSet<String>();
		int cursor = 0;
		
		String text;
		String field;
		
		while (matcher.find())
		{
			if (matcher.start() > cursor)
			{
				text = template.substring(cursor, matcher.start());
				renderers.add(new Literal(text));
			}
			
			text = template.substring(matcher.start(), matcher.end());
			field = text.substring(1, text.length() - 1);
			
			renderers.add(new Field(field, text));
			fields.add(field);
			
			cursor = matcher.end();
		}
		if (cursor < template.length())
		{
			renderers.add(new Literal(template.substring(cursor)));
		}
	}
	
	private static interface Renderer
	{
		public String render(Map<String, ? extends Object> fields);
	}
	
	private static class Literal implements Renderer
	{
		private String text;

		public Literal(String text)
		{
			this.text = text;
		}

		@Override
		public String render(Map<String, ? extends Object> fields)
		{
			return text;
		}
		
		@Override
		public String toString()
		{
			return "Literal(\"" + text + "\")";
		}
	}
	
	private static class Field implements Renderer
	{
		private final String fieldName;
		private final String token;

		public Field(String fieldName, String originalToken)
		{
			this.fieldName = fieldName;
			this.token = originalToken;
		}

		@Override
		public String render(Map<String, ? extends Object> fields)
		{
			return fields.containsKey(fieldName) 
				? fields.get(fieldName).toString()
				: token;
		}
		
		@Override
		public String toString()
		{
			return "Field(" + fieldName + ", \"" + token + "\")";
		}
	}
}
