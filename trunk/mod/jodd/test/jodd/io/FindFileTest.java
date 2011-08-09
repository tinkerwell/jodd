// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.io.findfile.FindFile;
import jodd.io.findfile.RegExpFindFile;
import jodd.io.findfile.WildcardFileScanner;
import jodd.io.findfile.WildcardFindFile;
import junit.framework.TestCase;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class FindFileTest extends TestCase {

	protected String dataRoot;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (dataRoot != null) {
			return;
		}
		URL data = FileUtilTest.class.getResource("data");
		dataRoot = data.getFile().substring(1);
	}

	public void testWildcardFile() {
		FindFile ff = new WildcardFindFile("*file/a*")
				.setRecursive(true)
				.setIncludeDirs(true)
				.searchPath(dataRoot);

		int countDirs = 0;
		int countFiles = 0;

		File f;
		while ((f = ff.nextFile()) != null) {
			if (f.isDirectory() == true) {
				countDirs++;
			} else {
				countFiles++;
				String path = f.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				boolean matched =
						path.equals(dataRoot + "/file/a.png") ||
						path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);

			}
		}

		assertEquals(0, countDirs);
		assertEquals(2, countFiles);

		ff.searchPath(dataRoot);

		countDirs = 0;
		countFiles = 0;

		Iterator<File> iterator = ff.iterator();

		while (iterator.hasNext()) {
			f = iterator.next();

			if (f.isDirectory() == true) {
				countDirs++;
			} else {
				countFiles++;
				String path = f.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				boolean matched =
						path.equals(dataRoot + "/file/a.png") ||
						path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);
			}
		}

		assertEquals(0, countDirs);
		assertEquals(2, countFiles);

	}


	public void testWildcardPath() {
		FindFile ff = new WildcardFindFile("**/file/*", true)
				.setRecursive(true)
				.setIncludeDirs(true)
				.searchPath(dataRoot);

		int countDirs = 0;
		int countFiles = 0;

		File f;
		while ((f = ff.nextFile()) != null) {
			if (f.isDirectory() == true) {
				countDirs++;
			} else {
				countFiles++;
				String path = f.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				boolean matched =
						path.equals(dataRoot + "/file/a.png") ||
						path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);

			}
		}

		assertEquals(0, countDirs);
		assertEquals(2, countFiles);
	}

	public void testWildcardFileScanner() {
		WildcardFileScanner wfs = new WildcardFileScanner("**/file/**", true);

		List<File> files = wfs.list(dataRoot);
		assertEquals(1, files.size());

		wfs.setRecursive(true);

		wfs.setIncludeDirs(true);
		files = wfs.list(dataRoot);
		assertEquals(3, files.size());

		wfs.setIncludeDirs(false);
		files = wfs.list(dataRoot);
		assertEquals(2, files.size());

		assertTrue(files.contains(new File(dataRoot + "/file/a.png")));
		assertTrue(files.contains(new File(dataRoot + "/file/a.txt")));
	}

	public void testRegexp() {
		FindFile ff = new RegExpFindFile(".*/a[.].*")
				.setRecursive(true)
				.setIncludeDirs(true)
				.searchPath(dataRoot);

		int countDirs = 0;
		int countFiles = 0;

		File f;
		while ((f = ff.nextFile()) != null) {
			if (f.isDirectory() == true) {
				countDirs++;
			} else {
				countFiles++;
				String path = f.getAbsolutePath();
				path = FileNameUtil.separatorsToUnix(path);
				boolean matched =
						path.equals(dataRoot + "/file/a.png") ||
						path.equals(dataRoot + "/file/a.txt");

				assertTrue(matched);

			}
		}

		assertEquals(0, countDirs);
		assertEquals(2, countFiles);
	}

}