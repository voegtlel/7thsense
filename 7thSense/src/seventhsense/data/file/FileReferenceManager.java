/*
 * FileReferenceManager.java
 * 
 * Copyright (c) 2011 L.Voegtle, J. Moeller. All rights reserved.
 * 
 * This file is part of 7th Sense (
 * <a href="http://code.google.com/p/7thsense">
 * http://code.google.com/p/7thsense</a>) and therefore released
 * under the LGPL license:
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA
 * 
 * For more information check <a href="http://www.gnu.org/licenses/lgpl.html">http://www.gnu.org/licenses/lgpl.html</a>
 */
package seventhsense.data.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A singleton class for referenced files
 * 
 * @author Parallan
 *
 */
public final class FileReferenceManager
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(FileReferenceManager.class.getName());
	
	/**
	 * The singleton instance
	 */
	private static final FileReferenceManager __fileReferenceManager = new FileReferenceManager();
	
	private final FileReference _emptyReference;
	
	/**
	 * List of file references
	 */
	private final Map<String, FileReference> _fileReferences = new HashMap<String, FileReference>();
	
	/**
	 * Singleton Constructor
	 */
	private FileReferenceManager()
	{
		_emptyReference = new FileReference("");
	}
	
	/**
	 * Static method to get the singleton instance.
	 * 
	 * @return singleton instance
	 */
	public static FileReferenceManager get()
	{
		return __fileReferenceManager;
	}
	
	/**
	 * trys to get a relative path for the given path
	 * 
	 * @param path path to make relative
	 * @return relative path if possible, otherwise the given absolute path
	 */
	private String tryGetRelativePath(final File path)
	{
		final String localAbsolutePath = new File ("").getAbsolutePath();
		final String absolutePath = path.getAbsolutePath();
		if(absolutePath.startsWith(localAbsolutePath))
		{
			return absolutePath.substring(localAbsolutePath.length() + 1);
		}
		return path.getPath();
	}
	
	/**
	 * returns a file reference for the given path
	 * 
	 * @param path path to get a file reference for
	 * @return file reference
	 */
	public FileReference getFileReference(final String path)
	{
		final String filePath = tryGetRelativePath(new File(path));
		if(_fileReferences.containsKey(filePath))
		{
			return _fileReferences.get(filePath);
		}
		final FileReference fileReference = new FileReference(filePath);
		_fileReferences.put(fileReference.getPath(), fileReference);
		return fileReference;
	}
	
	/**
	 * returns the correct file reference for the given file reference
	 * 
	 * @param fileReference instanciated file reference to replace by the reference stored here
	 * @return used file reference
	 */
	public FileReference getFileReference(final FileReference fileReference)
	{
		if(_fileReferences.containsKey(fileReference.getPath()))
		{
			return _fileReferences.get(fileReference.getPath());
		}
		_fileReferences.put(fileReference.getPath(), fileReference);
		return fileReference;
	}

	/**
	 * @return
	 */
	public FileReference getEmptyReference()
	{
		return _emptyReference;
	}
}
