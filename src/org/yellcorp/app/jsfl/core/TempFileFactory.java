package org.yellcorp.app.jsfl.core;

import java.io.File;
import java.io.IOException;

public class TempFileFactory
{
	private File directory;
	private boolean debug;

	public TempFileFactory()
	{
		this((File)null, false);
	}
	
	public TempFileFactory(String directory, boolean debug)
	{
		this(new File(directory), debug);
	}
	
	public TempFileFactory(File directory, boolean debug)
	{
		this.directory = directory;
		this.debug = debug;
	}
	
	public File createFile(String prefix, String suffix) throws IOException
	{
		File file = File.createTempFile(prefix, suffix, directory);
		if (!debug) file.deleteOnExit();
		return file;
	}
}
