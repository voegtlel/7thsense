/*
 * AbstractContainerNode.java
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.swing.tree.TreeNode;

/**
 * Abstract class for a container node.
 * Can contain any type of INodes
 * 
 * @author Parallan
 *
 */
public abstract class AbstractContainerNode extends AbstractNode
{
	/**
	 * Default serial version for serializable.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Children in this node
	 */
	protected final List<INode> _children = new ArrayList<INode>();

	/**
	 * Creates a abstract container node with a name
	 * 
	 * @param name name
	 */
	public AbstractContainerNode(final String name)
	{
		super(name);
	}
	
	@Override
	public TreeNode getChildAt(final int index)
	{
		return _children.get(index);
	}

	@Override
	public int getChildCount()
	{
		return _children.size();
	}

	@Override
	public int getIndex(final TreeNode node)
	{
		return _children.indexOf(node);
	}

	@Override
	public boolean getAllowsChildren() //NOPMD
	{
		return true;
	}

	@Override
	public Enumeration<INode> children()
	{
		return new Enumeration<INode>()
		{
			private int _index = 0;
			
			@Override
			public boolean hasMoreElements()
			{
				return _index < AbstractContainerNode.this._children.size();
			}

			@Override
			public INode nextElement()
			{
				final INode value = AbstractContainerNode.this._children.get(_index);
				_index++;
				return value;
			}
		};
	}

	@Override
	public Iterator<INode> iterator()
	{
		return _children.iterator();
	}
	
	@Override
	public void addNode(final int index, final INode node)
	{
		int realIndex = index;
		if(realIndex == -1)
		{
			realIndex = _children.size();
		}
		fireNodeAdding(realIndex, node);
		node.setParent(this);
		_children.add(realIndex, node);
		fireNodeAdded(realIndex, node);
	}
	
	@Override
	public void removeNode(final INode node)
	{
		final int index = _children.indexOf(node);
		fireNodeRemoving(index, node);
		node.setParent(null);
		_children.remove(node);
		fireNodeRemoved(index, node);
	}
	
	@Override
	public void removeNode(final int index)
	{
		final INode node = _children.get(index);
		fireNodeRemoving(index, node);
		node.setParent(null);
		_children.remove(node);
		fireNodeRemoved(index, node);
	}

	/**
	 * Searches this node and all subnodes for an INode with the UUID.
	 * 
	 * @param uuid uuid to find
	 * @return found object or null if not found
	 */
	@Override
	public INode findNode(final UUID uuid)
	{
		for(INode subNode : this)
		{
			if(subNode.getUUID().equals(uuid))
			{
				return subNode;
			}
			if(subNode instanceof AbstractContainerNode)
			{
				final INode foundNode = ((AbstractContainerNode) subNode).findNode(uuid);
				if(foundNode != null)
				{
					return foundNode;
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean isValid() // NOPMD
	{
		return true;
	}
	
	@Override
	public void validate(final boolean recursive)
	{
		if(recursive)
		{
			for(INode subItem : this)
			{
				subItem.validate(recursive);
			}
		}
	}
}
