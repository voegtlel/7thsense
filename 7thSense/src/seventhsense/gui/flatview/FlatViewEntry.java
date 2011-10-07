/*
 * FlatViewEntry.java
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
package seventhsense.gui.flatview;

import seventhsense.data.INode;

/**
 * This class represents a single entry in a Flat View
 * 
 * @author Drag-On
 *
 */
public class FlatViewEntry
{
	/**
	 * Node of this entry
	 */
	private final INode _node;
	/**
	 * Root node of model, used for generating the displayed path
	 */
	private final INode _root;

	/**
	 * Creates a flat view entry
	 * 
	 * @param node node to wrap
	 * @param root root node for wrapping (used for determining the displayed path to root)
	 */
	public FlatViewEntry(final INode node, final INode root)
	{
		_node = node;
		_root = root;
	}
	
	/**
	 * Gets the whole path for the passed node as a string
	 * 
	 * @return node path as a string
	 */
	private String getNodePath()
	{
		INode curNode = _node;
		final StringBuilder nodePath = new StringBuilder();
		while (!curNode.equals(_root))
		{
			nodePath.insert(0, "/" + curNode.toString());
			curNode = curNode.getParent();
			if (curNode == null)
			{
				// Node is not in tree -> may be deleting
				return _node.toString();
			}
		}
		return nodePath.substring(1);
	}

	@Override
	public String toString()
	{
		return getNodePath();
	}

	/**
	 * Gets the wrapped node
	 * 
	 * @return wrapped node
	 */
	public INode getNode()
	{
		return _node;
	}
}
