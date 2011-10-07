/*
 * TreeViewTransferHandler.java
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

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;

import seventhsense.data.INode;
import seventhsense.data.LinkNode;
import seventhsense.gui.transfer.NodeTransferable;

/**
 * Transfer handler for the tree view
 * 
 * @author Parallan
 *
 */
public class TreeViewTransferHandler extends TransferHandler
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(TreeViewTransferHandler.class.getName());

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private final TreeView _treeView;

	private boolean _editable = true;
	private boolean _onlyRootEditable = false;
	private boolean _onlyLinks = false;

	/**
	 * Creates a transfer handler
	 * 
	 * @param treeView TreeView to operate on
	 */
	public TreeViewTransferHandler(final TreeView treeView)
	{
		super();
		_treeView = treeView;
	}

	@Override
	public boolean canImport(final TransferSupport support)
	{
		if (!_editable)
		{
			return false;
		}
		if (support.isDataFlavorSupported(NodeTransferable.NODE_TRANSFERABLE_FLAVOR))
		{
			INode importDestinationNode;
			if (support.isDrop())
			{
				final JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
				importDestinationNode = (INode) dropLocation.getPath().getLastPathComponent();
			}
			else
			{
				importDestinationNode = _treeView.getSelectedItem();
				if(importDestinationNode != null)
				{
					importDestinationNode = importDestinationNode.getParent();
				}
			}
			if (importDestinationNode == null)
			{
				importDestinationNode = _treeView.getModel();
			}
			final Transferable transferable = support.getTransferable();
			if (!importDestinationNode.getAllowsChildren() || importDestinationNode.isLeaf())
			{
				return false;
			}
			if (_onlyRootEditable && (importDestinationNode != _treeView.getModel()))
			{
				return false;
			}
			if (transferable != null)
			{
				try
				{
					INode checkNode = importDestinationNode;
					final INode[] transferData = (INode[]) transferable.getTransferData(NodeTransferable.NODE_TRANSFERABLE_FLAVOR);
					// Check if data is descendant of destination
					for (INode transferNode : transferData)
					{
						transferNode = transferNode.getRealNode();
						while (checkNode != null)
						{
							checkNode = checkNode.getRealNode();
							if (checkNode.equals(transferNode))
							{
								return false;
							}
							checkNode = checkNode.getParent();
						}
					}
				}
				catch (UnsupportedFlavorException e) //NOPMD
				{
				}
				catch (IOException e) //NOPMD
				{
				}
			}
			if(_onlyLinks)
			{
				support.setDropAction(LINK);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean importData(final TransferSupport support)
	{
		if (!canImport(support))
		{
			return false;
		}
		LOGGER.log(Level.FINER, "import");
		if (support.isDataFlavorSupported(NodeTransferable.NODE_TRANSFERABLE_FLAVOR))
		{
			try
			{
				if (support.isDrop())
				{
					final JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
					final INode importDestinationNode = (INode) dropLocation.getPath().getLastPathComponent();
					if (!importDestinationNode.getAllowsChildren() || importDestinationNode.isLeaf())
					{
						LOGGER.log(Level.SEVERE, "import destination node should have been checked before!");
						return false;
					}
					final INode[] transferData = (INode[]) support.getTransferable().getTransferData(NodeTransferable.NODE_TRANSFERABLE_FLAVOR);
					for (INode importNode : transferData)
					{
						LOGGER.log(Level.FINER, "import node to " + importDestinationNode + " -> " + importNode);
						if((support.getDropAction() == LINK) || _onlyLinks)
						{
							LOGGER.log(Level.FINER, "link node " + importNode);
							importDestinationNode.addNode(dropLocation.getChildIndex(), new LinkNode(importNode.getRealNode().getUUID()));
						}
						else
						{
							LOGGER.log(Level.FINER, "copy node " + importNode);
							importDestinationNode.addNode(dropLocation.getChildIndex(), importNode.deepClone());
						}
					}
					return true;
				}
				else
				{
					final INode destinationNode = _treeView.getSelectedItem();
					if (destinationNode != null)
					{
						final INode[] transferData = (INode[]) support.getTransferable().getTransferData(NodeTransferable.NODE_TRANSFERABLE_FLAVOR);
						int nodeIndex = destinationNode.getParent().getIndex(destinationNode);
						for (INode importNode : transferData)
						{
							LOGGER.log(Level.FINER, "import node to " + destinationNode.getParent() + " -> " + importNode);
							if(_onlyLinks)
							{
								destinationNode.getParent().addNode(nodeIndex + 1, new LinkNode(importNode.getRealNode().getUUID()));
								nodeIndex++;
							}
							else
							{
								destinationNode.getParent().addNode(nodeIndex + 1, importNode.deepClone());
								nodeIndex++;
							}
						}
					}
				}
			}
			catch (UnsupportedFlavorException e)
			{
				throw new IllegalStateException("flavor exception should have been checked before!", e);
			}
			catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
				return false;
			}
		}
		return false;
	}

	@Override
	protected Transferable createTransferable(final JComponent c)
	{
		LOGGER.log(Level.FINER, "createTransferable");
		final INode selectedItem = _treeView.getSelectedItem();
		if (selectedItem == null)
		{
			return null;
		}
		else
		{
			return new NodeTransferable(new INode[] { selectedItem });
		}
	}

	@Override
	protected void exportDone(final JComponent source, final Transferable data, final int action)
	{
		if ((action == MOVE) && _editable)
		{
			LOGGER.log(Level.FINER, "exportDone");
			try
			{
				final INode[] transferData = (INode[]) data.getTransferData(NodeTransferable.NODE_TRANSFERABLE_FLAVOR);
				for (INode node : transferData)
				{
					if (_onlyRootEditable && (node.getParent() != _treeView.getModel()))
					{
						continue;
					}
					
					node.getParent().removeNode(node);
				}
			}
			catch (UnsupportedFlavorException e)
			{
				throw new IllegalStateException(e);
			}
			catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	@Override
	public int getSourceActions(final JComponent c)
	{
		if (!_editable)
		{
			return COPY | LINK;
		}
		final INode selectedItem = _treeView.getSelectedItem();
		if ((selectedItem == null) || (_onlyRootEditable && (selectedItem.getParent() != _treeView.getModel())))
		{
			return COPY | LINK;
		}
		return COPY | MOVE | LINK;
	}

	/**
	 * Sets if the tree may be edited
	 * 
	 * @param editable tree may be edited
	 */
	public void setEditable(final boolean editable)
	{
		_editable = editable;
	}
	
	/**
	 * Sets if only the tree root may be edited
	 * 
	 * @param onlyRootEditable only the tree root may be edited
	 */
	public void setOnlyRootEditable(final boolean onlyRootEditable)
	{
		_onlyRootEditable = onlyRootEditable;
	}

	/**
	 * Sets if only links are allowed to be dropped
	 * 
	 * @param onlyLinks only links are allowed to be dropped
	 */
	public void setOnlyLinks(boolean onlyLinks)
	{
		_onlyLinks = onlyLinks;
	}
}
