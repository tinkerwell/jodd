// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.file;

import jodd.io.findfile.ClasspathScanner;
import jodd.io.StreamUtil;

import java.io.InputStream;

public class fc {

	public static void main(String args[]) throws Exception {
		System.out.println("start");

		ClasspathScanner cs = new ClasspathScanner() {
			@Override
			protected void onEntry(EntryData entryData) throws Exception {
				InputStream inputStream = entryData.openInputStream();
				byte[] bytes = StreamUtil.readAvailableBytes(inputStream);
				System.out.println("---> " + entryData.getName() + ':' + entryData.getArchiveName() + "\t\t" + bytes.length);
			}
		};
		cs.includeResources(true).ignoreException(true).scan("foo.jar", "d:\\Projects\\java\\apps\\jarminator\\out");
		System.out.println("end");
	}

}