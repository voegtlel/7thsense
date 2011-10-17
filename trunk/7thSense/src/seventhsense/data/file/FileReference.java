/*
 * FileReference.java
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * File reference for media files.
 * On deserialization, the file must be sent to the FileReferenceManager
 * 
 * @author Parallan
 *
 */
public class FileReference implements Serializable
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(FileReference.class.getName());
	
	/**
	 * Path to the file
	 */
	private final String _path;
	
	/**
	 * File hash value
	 */
	private byte[] _hash;
	
	/**
	 * Create a new file reference
	 * 
	 * @param path path to the file
	 */
	public FileReference(final String path)
	{
		_path = path;
	}
	
	/**
	 * Calculates the hash for the file and saves it
	 * 
	 * @throws IOException
	 */
	public void generateHash() throws IOException
	{
		final FileInputStream fileInputStream = new FileInputStream(_path);
		final FileChannel fileChannel = fileInputStream.getChannel();
		final ByteBuffer fileData = ByteBuffer.allocate((int) fileChannel.size());
		if(fileChannel.read(fileData) != fileChannel.size())
		{
			throw new IOException("can't read entire file");
		}
		fileInputStream.close();
		
		try
		{
			final MessageDigest md = MessageDigest.getInstance("MD5");
			_hash = md.digest(fileData.array());
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Getter for file hash, if one was calculated (else null)
	 * 
	 * @return hash generated hash or null
	 */
	public byte[] getHash()
	{
		return _hash;
	}

	/**
	 * Gets the path
	 * 
	 * @return path path
	 */
	public String getPath()
	{
		return _path;
	}
	
	@Override
	public String toString()
	{
		return _path;
	}
}
