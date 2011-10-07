/*
 * FlatViewListModel.java
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;

import seventhsense.data.INode;
import seventhsense.data.INodeListener;
import seventhsense.data.NodeAdapter;
import seventhsense.gui.treeview.TreeViewTreeModel;

/**
 * Listmodel for the Flat view
 * 
 * @author Drag-On, Parallan
 *
 */
public class FlatViewListModel extends DefaultListModel
{
	/**
	 * Default serial version for serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger (for debugging purposes)
	 */
	private static final Logger LOGGER = Logger.getLogger(TreeViewTreeModel.class.getName());

	/**
	 * Listener for the basic (root) node
	 */
	private transient final INodeListener _nodeListener;

	/**
	 * Root node
	 */
	private INode _root;

	/**
	 * Constructor
	 */
	public FlatViewListModel()
	{
		super();
		_nodeListener = new NodeAdapter()
		{
			@Override
			public void nodeAdded(final INode parentNode, final int index, final INode node)
			{
				FlatViewListModel.this.onNodeAdded(parentNode, index, node);
			}

			@Override
			public void nodeRemoved(final INode parentNode, final int index, final INode node)
			{
				FlatViewListModel.this.onNodeRemoved(parentNode, index, node);
			}

			@Override
			public void nodeChanged(final INode node, final String property)
			{
				FlatViewListModel.this.onNodeChanged(node);
			}
		};
	}
	
	/**
	 * Sets the model
	 * 
	 * @param root model
	 */
	public void setModel(final INode root)
	{
		if(_root != null)
		{
			removeDefaultNodeListeners(_root);
			clear();
		}
		_root = root;
		if(root != null)
		{
			insertItem(0, root);
		}
	}

	/**
	 * Gets the ID of the passed node inside of the list
	 * 
	 * @param node node to get the ID from
	 * @return ID of the node
	 */
	public int getListNodeIndex(final INode node)
	{
		for (int i = 0; i < this.getSize(); i++)
		{
			if (((FlatViewEntry) this.get(i)).getNode().equals(node))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Searches for the node and returns true, if found and the index as reference
	 * 
	 * @param rootNode used for recursive call: should be _root at first call
	 * @param node node to search
	 * @param getEndIndex if true, the last index of the node is also determined
	 * @param args returns the found index
	 * @return returns true, if node was found
	 */
	private boolean getNodeIndex(final INode rootNode, final INode node, final boolean getEndIndex, final NodeIndexArgs args)
	{
		for (INode subNode : rootNode)
		{
			if (subNode == node)
			{
				if(getEndIndex)
				{
					final int startIndex = args.getNodeIndex();
					// search all subnodes to count them
					getNodeIndex(rootNode, subNode, false, args);
					args.setNodeIndex(startIndex);
					args.setNodeIndexEnd(args.getNodeIndex());
				}
				return true;
			}
			if (subNode.isLeaf())
			{
				args.setNodeIndex(args.getNodeIndex()+1);
			}
			else
			{
				if (getNodeIndex(subNode, node, getEndIndex, args))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes the observation-events for the node and all subnodes
	 * 
	 * @param node node to remove observation
	 */
	private void removeDefaultNodeListeners(final INode node)
	{
		node.removeNodeListener(_nodeListener);
		for (INode subNode : node)
		{
			removeDefaultNodeListeners(subNode);
		}
	}

	/**
	 * Inserts a new item at the specified index into the list
	 * If the passed INode is not a leaf will add all leaves of the passed node
	 * 
	 * @param index Index where the new Item will be inserted
	 * @param node Node to add
	 * @return index after the inserted item
	 */
	private int insertItem(final int index, final INode node)
	{
		int id = index;
		node.addNodeListener(_nodeListener);
		if (node.isLeaf())
		{
			final FlatViewEntry entry = new FlatViewEntry(node, _root);
			this.add(id, entry);
			return id+1;
		}
		else
		{
			for (INode subnode : node)
			{
				id = insertItem(id, subnode);
			}
		}
		return id;
	}

	/**
	 * Event Listener (see {@link TreeViewTreeModel#TreeViewTreeModel(INode)})
	 * 
	 * @param parentNode parentNode
	 * @param index index
	 * @param node node
	 */
	private void onNodeAdded(final INode parentNode, final int index, final INode node)
	{
		final NodeIndexArgs args = new NodeIndexArgs();
		getNodeIndex(_root, node, false, args);
		final int id = args.getNodeIndex();
		insertItem(id, node);
		LOGGER.log(Level.FINER, "nodeAdded: " + parentNode + " -> " + node.toString() + " (" + index + ")");
	}

	/**
	 * Event Listener (see {@link TreeViewTreeModel#TreeViewTreeModel(INode)})
	 * 
	 * @param parentNode parentNode
	 * @param index index
	 * @param node node
	 */
	private void onNodeRemoved(final INode parentNode, final int index, final INode node)
	{
		removeDefaultNodeListeners(node);
		if(node.isLeaf())
		{
			final int id = getListNodeIndex(node);
			this.remove(id);
		}
		else
		{
			for(INode subnode : node)
			{
				onNodeRemoved(node, index, subnode);
			}
		}
		LOGGER.log(Level.FINER, "nodeRemoved: " + parentNode + " -> " + node.toString() + " (" + index + ")");
	}

	/**
	 * Event Listener (see {@link TreeViewTreeModel#TreeViewTreeModel(INode)})
	 * 
	 * @param node node
	 */
	private void onNodeChanged(final INode node)
	{
		final NodeIndexArgs args = new NodeIndexArgs();
		
		if(getNodeIndex(_root, node, true, args))
		{
			final int id1 = args.getNodeIndex();
			final int id2 = args.getNodeIndexEnd();
			this.fireContentsChanged(this, id1, id2);
			LOGGER.log(Level.FINER, "nodeChanged: " + node);
		}
	}

	/**
	 * Used in getNodeIndex and nodeAdded methods as a way to return a second value
	 * 
	 * @author Parallan, Drag-On
	 *
	 */
	private static final class NodeIndexArgs
	{
		private int _nodeIndex = 0;
		private int _nodeIndexEnd = 0;

		/**
		 * Gets the node index
		 * 
		 * @return the nodeIndex
		 */
		public int getNodeIndex()
		{
			return _nodeIndex;
		}
		
		/**
		 * Gets the node end index
		 * 
		 * @return the nodeIndex
		 */
		public int getNodeIndexEnd()
		{
			return _nodeIndexEnd;
		}

		/**
		 * Sets the node index
		 * 
		 * @param nodeIndex the nodeIndex to set
		 */
		public void setNodeIndex(final int nodeIndex)
		{
			_nodeIndex = nodeIndex;
		}
		
		/**
		 * Sets the node end index
		 * 
		 * @param nodeIndexEnd the nodeIndexEnd to set
		 */
		public void setNodeIndexEnd(final int nodeIndexEnd)
		{
			_nodeIndexEnd = nodeIndexEnd;
		}
	}
}
