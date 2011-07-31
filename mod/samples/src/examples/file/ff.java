// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.io.findfile.FileScanner;
import jodd.io.filter.WildcardFileFilter;
import jodd.util.SystemUtil;

import java.io.File;
import java.util.Iterator;

public class ff {
	public static void main(String args[]) {
		System.out.println("---start---");

		File f = new File(".");
		String[] list = f.list(new WildcardFileFilter("*"));
		for (String l : list) {
			System.out.println(l);
		}

		System.out.println("\n\n\nsearching classpath");

		FileScanner fs = new FileScanner() {
			@Override
			protected void onFile(File file) {
				System.out.println("***" + file.getName());
			}
		};
		fs.setIncludeDirs(true);
		fs.setRecursive(true);
		fs.setIncludeFiles(false);
		fs.scan("d:\\temp\\temp\\");

		System.out.println("\n\n\nsearching classpath");

		// ----------------------------------------------------------------

		FindFile ff = new WildcardFindFile("*").setRecursive(true).setIncludeDirs(true).searchPath(SystemUtil.getClassPath());
//		FindFile ff = new WildcardFindFile("c:/temp/temp","*").includeDirs().recursive();

		int i = 1;

		while ((f = ff.nextFile()) != null) {
			if (f.isDirectory() == true) {
				System.out.println(i + ". >" + f.getName());
			} else {
				System.out.println(i + ". " + f.getName());
			}
			i++;
		}
		// ----------------------------------------------------------------

		ff = new WildcardFindFile("*").setRecursive(true).setIncludeDirs(true).searchPath(SystemUtil.getClassPath());
		Iterator<File> iterator = ff.iterator();
		i = 1;

		while (iterator.hasNext()) {
			f = iterator.next();
			if (f.isDirectory() == true) {
				System.out.println(i + ". >" + f.getName());
			} else {
				System.out.println(i + ". " + f.getName());
			}
			i++;
		}


		System.out.println("---end---");
	}
}
