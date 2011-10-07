/*
 * TreeViewTreeModel.java
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
package seventhsense.gui.treeview;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import seventhsense.data.INode;
import seventhsense.data.INodeListener;
import seventhsense.data.NodeAdapter;

/**
 * Model for the tree
 * 
 * @author Parallan
 *
 */
public class TreeViewTreeModel extends DefaultTreeModel
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(TreeViewTreeModel.class.getName());

	/**
	 * Default serial version for serializable
	 */
	private static final long serialVersionUID = 1L;

	private transient final INodeListener _nodeListener;

	/**
	 * Constructor for the tree model
	 * 
	 * @param root root node which will be viewed and observed
	 */
	public TreeViewTreeModel(final INode root)
	{
		super(root);
		_nodeListener = new NodeAdapter()
		{
			@Override
			public void nodeAdded(final INode parentNode, final int index, final INode node)
			{
				TreeViewTreeModel.this.nodeAdded(parentNode, index, node);
			}

			@Override
			public void nodeRemoved(final INode parentNode, final int index, final INode node)
			{
				TreeViewTreeModel.this.nodeRemoved(parentNode, index, node);
			}

			@Override
			public void nodeChanged(final INode node, final String property)
			{
				TreeViewTreeModel.this.nodeChanged(node);
			}
		};

		if(root != null)
		{
			addDefaultNodeListeners(root);
		}
	}
	
	@Override
	public void setRoot(final TreeNode root)
	{
		if(!(root instanceof INode))
		{
			throw new IllegalArgumentException();
		}
		
		if(this.root != null)
		{
			removeDefaultNodeListeners((INode) root);
		}
		super.setRoot(root);
		if(root != null)
		{
			addDefaultNodeListeners((INode) root);
		}
	}
	
	@Override
	public void valueForPathChanged(final TreePath path, final Object newValue)
	{
		final INode node = (INode)path.getLastPathComponent();
		node.setName(newValue.toString()); // fires node changed event
	}

	/**
	 * adds the observation-events for the node and all subnodes
	 * 
	 * @param node node to observe
	 */
	private void addDefaultNodeListeners(final INode node)
	{
		node.addNodeListener(_nodeListener);
		for (INode subNode : node)
		{
			addDefaultNodeListeners(subNode);
		}
	}

	/**
	 * removes the observation-events for the node and all subnodes
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
	 * Gets the tree path for an INode
	 * 
	 * @param node node to get the TreePath for
	 * 
	 * @return TreePath for the node
	 */
	public TreePath getNodePath(final INode node)
	{
		TreeNode curNode = node;
		final List<TreeNode> nodes = new LinkedList<TreeNode>();
		while (curNode != this.root)
		{
			nodes.add(0, curNode);
			curNode = curNode.getParent();
			if (curNode == null)
			{
				throw new IllegalArgumentException("node is not in tree");
			}
		}
		nodes.add(0, this.root);
		return new TreePath(nodes.toArray());
	}

	/**
	 * Event Listener (see {@link TreeViewTreeModel#TreeViewTreeModel(INode)})
	 * 
	 * @param parentNode parentNode
	 * @param index index
	 * @param node node
	 */
	private void nodeAdded(final INode parentNode, final int index, final INode node)
	{
		addDefaultNodeListeners(node);
		super.nodesWereInserted(parentNode, new int[] { index });
		LOGGER.log(Level.FINER, "nodeAdded: " + parentNode + " -> " + node.toString() + " (" + index + ")");
	}

	/**
	 * Event Listener (see {@link TreeViewTreeModel#TreeViewTreeModel(INode)})
	 * 
	 * @param parentNode parentNode
	 * @param index index
	 * @param node node
	 */
	private void nodeRemoved(final INode parentNode, final int index, final INode node)
	{
		super.nodesWereRemoved(parentNode, new int[] { index }, new TreeNode[] { node });
		removeDefaultNodeListeners(node);
		LOGGER.log(Level.FINER, "nodeRemoved: " + parentNode + " -> " + node.toString() + " (" + index + ")");
	}

	/**
	 * Event Listener (see {@link TreeViewTreeModel#TreeViewTreeModel(INode)})
	 * 
	 * @param node node
	 */
	private void nodeChanged(final INode node)
	{
		super.nodeChanged((TreeNode)node);
		LOGGER.log(Level.FINER, "nodeChanged: " + node);
	}
}
