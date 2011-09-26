package org.yellcorp.util;

import java.util.Arrays;

public class ArrayUtil
{
	public static <T> T[] concat(T[] first, T[]... rest)
	{
		int totalLength = first.length;
		
		for (T[] array : rest)
		{
			totalLength += array.length;
		}
		
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest)
		{
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
	
	public static String join(Object[] array)
	{
		return join(array, "");
	}
	
	public static String join(Object[] array, char separator)
	{
		return join(array, Character.toString(separator));
	}

	public static String join(Object[] array, String separator)
	{
		if (array == null)
		{
			return null;
		}
		else if (array.length == 0)
		{
			return "";
		}
		else if (array.length == 1)
		{
			return array[0].toString();
		}
		else
		{
			StringBuilder result = new StringBuilder(array[0].toString());
			for (int i = 1; i < array.length; i++)
			{
				result.append(separator);
				result.append(array[i].toString());
			}
			return result.toString();
		}
	}
}
