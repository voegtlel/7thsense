/*
 * NodeFile.java
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
package seventhsense.gui.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import seventhsense.data.INode;

/**
 * Class for a node files (static, final)
 * 
 * @author Parallan
 *
 */
public final class NodeFile
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(NodeFile.class.getName());
	
	/**
	 * Default file for playlist file
	 */
	public static final File DefaultPlaylistFile = new File("playlist.7pf");
	
	/**
	 * Default file for library file
	 */
	public static final File DefaultLibraryFile = new File("library.7lf");
	
	/**
	 * No Ctor
	 */
	private NodeFile()
	{
		//No Ctor
	}
	
	/**
	 * Saved the given node to the given file
	 * 
	 * @param node node to save
	 * @param file destination file
	 */
	public static void saveNode(final INode node, final File file)
	{
		final INode originalParent = node.getParent();
		try
		{
			node.setParent(null);
			final FileOutputStream fos = new FileOutputStream(file);
			final ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(node);
			oos.close();
			fos.close();
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		node.setParent(originalParent);
	}
	
	/**
	 * Loads the node from the given file
	 * 
	 * @param file file to load from
	 * @return loaded node or null on error
	 */
	public static INode loadNode(final File file)
	{
		try
		{
			final FileInputStream fis = new FileInputStream(file);
			final ObjectInputStream ois = new ObjectInputStream(fis);
			final INode retval = (INode) ois.readObject();
			ois.close();
			fis.close();
			return retval;
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		return null;
	}
}
