/*
 * FolderNode.java
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
package seventhsense.data;


/**
 * Class for Folders.
 * Folders can contain further folders or scenarios (or other types).
 * 
 * @author Parallan
 *
 */
public class FolderNode extends AbstractContainerNode
{
	/**
	 * Default serial version for serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a simple folder container node
	 * 
	 * @param name name of the node
	 */
	public FolderNode(final String name)
	{
		super(name);
	}

	@Override
	public INode deepClone()
	{
		final FolderNode clone = new FolderNode(_name);
		for(INode node : _children)
		{
			clone.addNode(-1, node.deepClone());
		}
		return clone;
	}
	
	@Override
	public boolean isLeaf()
	{
		return false;
	}
}
