/*
 * LinkNode.java
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.swing.tree.TreeNode;

/**
 * Class for wrapping a node as a referenced node
 * @author Parallan
 *
 */
public class LinkNode extends AbstractNode implements ILinkNode
{
	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The wrapped node. The node is loaded when it is used
	 */
	protected transient INode _wrappedNode;
	
	/**
	 * Uuid of the wrapped node. Identifies the node
	 */
	protected final UUID _wrappedUuid;
	
	/**
	 * If true, the wrapped node could not be found
	 */
	private transient boolean _wrappedNodeInvalid = false;

	/**
	 * List of wrapped subnodes
	 */
	private transient List<INode> _children;

	/**
	 * Creates a link node from an existing node
	 * 
	 * @param wrappedNode source node
	 */
	public LinkNode(final INode wrappedNode)
	{
		super(null);
		_wrappedNode = wrappedNode;
		_wrappedUuid = wrappedNode.getUUID();
		initWrappedNode();
	}
	
	/**
	 * Creates a link node from an uuid
	 * 
	 * @param wrappedUuid uuid of the wrapped node
	 */
	public LinkNode(final UUID wrappedUuid)
	{
		super(null);
		_wrappedUuid = wrappedUuid;
	}

	@Override
	public boolean getAllowsChildren() //NOPMD
	{
		if(getWrappedNode() == null)
		{
			return false;
		}
		return getWrappedNode().getAllowsChildren();
	}

	@Override
	public boolean isLeaf()
	{
		if(getWrappedNode() == null)
		{
			return true;
		}
		return getWrappedNode().isLeaf();
	}

	@Override
	public String getName()
	{
		if(getWrappedNode() != null)
		{
			return getWrappedNode().getName();
		}
		return _wrappedUuid.toString();
	}

	@Override
	public void setName(final String name)
	{
		if(getWrappedNode() != null)
		{
			getWrappedNode().setName(name);
		}
	}

	@Override
	public void addNode(final int index, final INode node)
	{
		getWrappedNode().addNode(index, node);
	}

	@Override
	public void removeNode(final INode node)
	{
		if(node instanceof LinkReferenceNode)
		{
			getWrappedNode().removeNode(((LinkReferenceNode)node).getWrappedNode());
		}
		else
		{
			throw new IllegalStateException("must only contain LinkReferenceNodes");
		}
	}

	@Override
	public INode deepClone()
	{
		if(_wrappedNode != null)
		{
			return new LinkNode(_wrappedNode);
		}
		else
		{
			return new LinkNode(_wrappedUuid);
		}
	}

	@Override
	public INode findNode(final UUID uuid)
	{
		// No searching here, must search in real node
		return null;
	}

	@Override
	public TreeNode getChildAt(final int index)
	{
		return getChildren().get(index);
	}

	@Override
	public int getChildCount()
	{
		return getChildren().size();
	}

	@Override
	public int getIndex(final TreeNode node)
	{
		return getChildren().indexOf(node);
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
				return _index < LinkNode.this.getChildren().size();
			}

			@Override
			public INode nextElement()
			{
				final INode value = LinkNode.this.getChildren().get(_index);
				_index++;
				return value;
			}
		};
	}

	@Override
	public Iterator<INode> iterator()
	{
		return getChildren().iterator();
	}

	@Override
	public void removeNode(final int index)
	{
		getWrappedNode().removeNode(index);
	}

	@Override
	public String toString()
	{
		if(getWrappedNode() == null)
		{
			return "{" + _wrappedUuid.toString() + "}";
		}
		else
		{
			return getWrappedNode().toString();
		}
	}
	
	@Override
	public INode getRealNode()
	{
		if(getWrappedNode() == null)
		{
			return this;
		}
		return getWrappedNode().getRealNode();
	}

	/**
	 * Event.
	 * 
	 * @param index index
	 * @param node node
	 */
	private void wrappedNodeAdding(final int index, final INode node)
	{
		final INode newWrappedNode = new LinkReferenceNode(node);
		int realIndex = index;
		if (realIndex == -1)
		{
			realIndex = getChildren().size();
		}
		
		// Check for recursion
		
		fireNodeAdding(realIndex, newWrappedNode);
		
		newWrappedNode.setParent(this);
		getChildren().add(index, newWrappedNode);
		fireNodeAdded(index, newWrappedNode);
	}

	/**
	 * Event.
	 * 
	 * @param property property
	 */
	private void wrappedNodeChanged(final String property)
	{
		fireNodeChanged(property);
	}

	/**
	 * Event.
	 * 
	 * @param property property
	 */
	private void wrappedNodeChanging(final String property)
	{
		fireNodeChanging(property);
	}

	/**
	 * Event.
	 * 
	 * @param index index
	 * @param node node
	 */
	private void wrappedNodeRemoving(final int index)
	{
		final INode node = _children.get(index);
		fireNodeRemoving(index, node);
		
		node.setParent(null);
		getChildren().remove(index);
		fireNodeRemoved(index, node);
	}
	
	/**
	 * Internal getter for children list
	 * 
	 * @return safe list of children
	 */
	private List<INode> getChildren()
	{
		if(_children == null)
		{
			_children = new ArrayList<INode>();
			if(getWrappedNode() != null)
			{
				for (final INode subNode : getWrappedNode())
				{
					final INode newWrappedNode = new LinkReferenceNode(subNode);
					newWrappedNode.setParent(this);
					_children.add(newWrappedNode);
				}
			}
		}
		return _children;
	}
	
	@Override
	public boolean isValid()
	{
		// Always validate here (fast enough)
		return getWrappedNode() != null;
	}
	
	@Override
	public void validate(final boolean recursive)
	{
		// do nothing
	}

	/**
	 * initialized the wrapped node
	 */
	private void initWrappedNode()
	{
		_wrappedNode.addNodeListener(new NodeAdapter()
		{
			@Override
			public void nodeChanged(final INode node, final String property)
			{
				LinkNode.this.wrappedNodeChanged(property);
			}

			@Override
			public void nodeAdding(final INode parentNode, final int index, final INode node)
			{
				LinkNode.this.wrappedNodeAdding(index, node);
			}

			@Override
			public void nodeRemoving(final INode parentNode, final int index, final INode node)
			{
				LinkNode.this.wrappedNodeRemoving(index);
			}

			@Override
			public void nodeChanging(final INode node, final String property)
			{
				LinkNode.this.wrappedNodeChanging(property);
			}
		});
	}

	/**
	 * returns the wrapped node (if not loaded, it will be loaded now)
	 * 
	 * @return wrapped node or null on error
	 */
	@Override
	public INode getWrappedNode()
	{
		if ((_wrappedNode == null) && !_wrappedNodeInvalid)
		{
			INode rootNode = this;
			while (rootNode.getParent() != null)
			{
				rootNode = rootNode.getParent();
			}
			_wrappedNode = rootNode.findNode(_wrappedUuid);
			if (_wrappedNode == null)
			{
				_wrappedNodeInvalid = true;
			}
			else
			{
				initWrappedNode();
			}
		}
		return _wrappedNode;
	}

	/**
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		_wrappedNodeInvalid = false;
	}
}
