package test;

import java.io.File;

import org.yellcorp.util.Globber;

public class TestGlobber
{
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage: TestGlobber <folder> <glob>");
			System.exit(2);
			return;
		}
		
		Globber g = new Globber();
		g.find(args[0], args[1]);
		for (File f : g.getResult())
		{
			System.out.println(f.getPath());
		}
	}
}
