// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload.impl;

import jodd.JoddDefault;
import jodd.io.FastByteArrayOutputStream;
import jodd.io.FileUtil;
import jodd.io.FileNameUtil;
import jodd.io.StreamUtil;
import jodd.servlet.upload.FileUpload;
import jodd.servlet.upload.MultipartRequestInputStream;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

/**
 * Smart {@link FileUpload} implementation that defer the action of what to do with uploaded file
 * for later. Internally, it stores uploaded file either in memory if it is small, or, in all
 * other cases, it stores them in TEMP folder.
 */
public class AdaptiveFileUpload extends FileUpload {

	protected static final String TMP_FILE_SUFFIX = ".upload.tmp";

	protected final int memoryThreshold;
	protected final File uploadPath;
	protected final boolean breakOnError;
	protected final String[] fileExtensions;
	protected final boolean allowFileExtensions;

	AdaptiveFileUpload(MultipartRequestInputStream input, int memoryThreshold, File uploadPath, int maxFileSize, boolean breakOnError, String[] extensions, boolean allowed) {
		super(input, maxFileSize);
		this.memoryThreshold = memoryThreshold;
		this.uploadPath = uploadPath;
		this.breakOnError = breakOnError;
		this.fileExtensions = extensions;
		this.allowFileExtensions = allowed;
	}

	// ---------------------------------------------------------------- settings

	public int getMemoryThreshold() {
		return memoryThreshold;
	}

	public File getUploadPath() {
		return uploadPath;
	}

	public boolean isBreakOnError() {
		return breakOnError;
	}

	public String[] getFileExtensions() {
		return fileExtensions;
	}

	public boolean isAllowFileExtensions() {
		return allowFileExtensions;
	}

	// ---------------------------------------------------------------- properties

	protected File tempFile;
	protected byte[] data;

	/**
	 * Returns <code>true</code> if file upload resides in memory.
	 */
	@Override
	public boolean isInMemory() {
		return data != null;
	}

	// ---------------------------------------------------------------- process


	protected boolean matchFileExtension() throws IOException {
		String fileNameExtension = FileNameUtil.getExtension(getHeader().getFileName());
		for (String fileExtension : fileExtensions) {
			if (fileNameExtension.equalsIgnoreCase(fileExtension) == true) {
				if (allowFileExtensions == false) {	// extension matched and it is not allowed
					if (breakOnError == true) {
						throw new IOException("Upload filename extension not allowed: " + fileNameExtension);
					}
					size = input.skipToBoundary();
					return false;
				}
				return true;		// extension matched and it is allowed.
			}
		}
		if (allowFileExtensions == true) {	// extension is not one of the allowed ones.
			if (breakOnError == true) {
				throw new IOException("Upload filename extension not allowed: " + fileNameExtension);
			}
			size = input.skipToBoundary();
			return false;
		}
		return true;
	}

	/**
	 * Determines if upload is allowed.
	 */
	protected boolean checkUpload() throws IOException {
		if (fileExtensions != null) {
			if (matchFileExtension() == false) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void processStream() throws IOException {
		if (checkUpload() == false) {
			return;
		}
		size = 0;
		if (memoryThreshold > 0) {
			FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream(memoryThreshold + 1);
			int written = input.copyMax(fbaos, memoryThreshold + 1);
			data = fbaos.toByteArray();
			if (written <= memoryThreshold) {
				size = data.length;
				valid = true;
				return;
			}
		}

		tempFile = FileUtil.createTempFile(JoddDefault.tempFilePrefix, TMP_FILE_SUFFIX, uploadPath);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
		if (data != null) {
			size = data.length;
			out.write(data);
			data = null;	// not needed anymore
		}
		boolean deleteTempFile = false;
		try {
			if (maxFileSize == -1) {
				size += input.copyAll(out);
			} else {
				size += input.copyMax(out, maxFileSize - size + 1);		// one more byte to detect larger files
				if (size > maxFileSize) {
					deleteTempFile = true;
					fileTooBig = true;
					valid = false;
					if (breakOnError == true) {
						throw new IOException("File upload (" + header.getFileName() + ") too big, > " + maxFileSize);
					}
					input.skipToBoundary();
					return;
				}
			}
			valid = true;
		} finally {
			StreamUtil.close(out);
			if (deleteTempFile) {
				tempFile.delete();
				tempFile = null;
			}
		}
	}

	// ---------------------------------------------------------------- operations


	/**
	 * Deletes file uploaded item from disk or memory.
	 */
	public void delete() {
		if (tempFile != null) {
			tempFile.delete();
		}
		if (data != null) {
			data = null;
		}
	}

	/**
	 * Writes file uploaded item.
	 */
	public File write(String destination) throws IOException {
		return write(new File(destination));
	}

	/**
	 * Writes file upload item to destination folder or to destination file.
	 * Returns the destination file.
	 */
	public File write(File destination) throws IOException {
		if (destination.isDirectory() == true) {
			destination = new File(destination, this.header.getFileName());
		}
		if (data != null) {
			FileUtil.writeBytes(destination,  data);
		} else {
			if (tempFile != null) {
				FileUtil.move(tempFile, destination);
			}
		}
		return destination;
	}

	/**
	 * Returns the content of file upload item.
	 */
	@Override
	public byte[] getFileContent() throws IOException {
		if (data != null) {
			return data;
		}
		if (tempFile != null) {
			return FileUtil.readBytes(tempFile);
		}
		return null;
	}

	@Override
	public InputStream getFileInputStream() throws IOException {
		if (data != null) {
			return new BufferedInputStream(new ByteArrayInputStream(data));
		}
		if (tempFile != null) {
			return new BufferedInputStream(new FileInputStream(tempFile));
		}
		return null;
	}



}