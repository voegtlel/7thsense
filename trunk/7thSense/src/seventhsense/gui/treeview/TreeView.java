/*
 * TreeView.java
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import seventhsense.data.INode;

/**
 * This class handles the TreeView window
 * 
 * @author Parallan, Drag-On
 *
 */
public class TreeView extends JPanel
{
	/**
	 * Default, required by Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * holds the tree data
	 */
	private final JTree _tree;
	private final TreeViewTreeModel _treeModel;

	/**
	 * Checkbox indicating if auto-collapse is turned on
	 */
	private final JCheckBox _checkboxAutocollapse;

	/**
	 * 
	 */
	private final JButton _buttonExpandAll;

	/**
	 * 
	 */
	private final JButton _buttonCollapseAll;

	private final TreeViewTransferHandler _treeTransferHandler;

	private final TreeViewRenderer _treeViewRenderer;

	/**
	 * Construcotr
	 */
	public TreeView()
	{
		super();

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final JScrollPane scrollPaneTree = new JScrollPane();
		final GridBagConstraints gbc_scrollPaneTree = new GridBagConstraints();
		gbc_scrollPaneTree.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPaneTree.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneTree.gridx = 0;
		gbc_scrollPaneTree.gridy = 0;
		add(scrollPaneTree, gbc_scrollPaneTree);

		_tree = new JTree();
		_tree.setEditable(true);
		scrollPaneTree.setViewportView(_tree);
		_tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(final TreeSelectionEvent e)
			{
				onTreeSelectionChanged(e);
			}
		});
		_tree.setShowsRootHandles(true);
		_tree.setRootVisible(false);
		_tree.setDragEnabled(true);
		_treeTransferHandler = new TreeViewTransferHandler(this);
		_tree.setTransferHandler(_treeTransferHandler);
		_tree.setDropMode(DropMode.ON_OR_INSERT);
		_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		_treeModel = new TreeViewTreeModel(null);
		_tree.setModel(_treeModel);

		final JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		final GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.fill = GridBagConstraints.BOTH;
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 1;
		add(toolBar, gbc_toolBar);

		_checkboxAutocollapse = new JCheckBox("Auto collapse non-active nodes");
		toolBar.add(_checkboxAutocollapse);
		_checkboxAutocollapse.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				autocollapseChanged();
			}
		});
		_checkboxAutocollapse.setToolTipText("Automatically collapse all nodes, that are not selected when selecting a node.");

		_buttonCollapseAll = new JButton("");
		_buttonCollapseAll.setIcon(new ImageIcon(TreeView.class.getResource("/seventhsense/resources/CollapseAll_20.png")));
		toolBar.add(_buttonCollapseAll);
		_buttonCollapseAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				collapseAll();
			}
		});
		_buttonCollapseAll.setToolTipText("Collapses the entire tree");

		_buttonExpandAll = new JButton("");
		_buttonExpandAll.setIcon(new ImageIcon(TreeView.class.getResource("/seventhsense/resources/ExpandAll_20.png")));
		toolBar.add(_buttonExpandAll);
		_buttonExpandAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				expandAll();
			}
		});
		_buttonExpandAll.setToolTipText("Expands the entire tree");

		//Enable tool tips.
	    ToolTipManager.sharedInstance().registerComponent(_tree);
		// Set own cell renderer
	    _treeViewRenderer = new TreeViewRenderer();
		_tree.setCellRenderer(_treeViewRenderer);
	}
	
	/**
	 * Sets, if the tree is editable
	 * 
	 * @param editable editable
	 */
	public void setEditable(final boolean editable)
	{
		_tree.setEditable(editable);
		_treeTransferHandler.setEditable(editable);
	}
	
	/**
	 * Sets, if only the root is editable (nodes in the root)
	 * 
	 * @param onlyRootEditable only the root is editable (nodes in the root)
	 */
	public void setOnylRootEditable(final boolean onlyRootEditable)
	{
		_tree.setEditable(!onlyRootEditable);
		_treeTransferHandler.setOnlyRootEditable(onlyRootEditable);
	}
	
	/**
	 * Sets, if only links should be used
	 * 
	 * @param onlyLinks only links should be used
	 */
	public void setOnlyLinks(final boolean onlyLinks)
	{
		_treeTransferHandler.setOnlyLinks(onlyLinks);
		_treeViewRenderer.setUseLinkNodeIcon(!onlyLinks);
	}

	/**
	 * Returns the selected INodes
	 * 
	 * @return selected INodes
	 */
	public INode getSelectedItem()
	{
		final TreePath selectionPath = _tree.getSelectionPath();
		if (selectionPath == null)
		{
			return null;
		}
		else
		{
			return (INode) selectionPath.getLastPathComponent();
		}
	}

	/**
	 * Sets the selected item
	 * 
	 * @param selectedItem selected item
	 */
	public void setSelectedItem(final INode selectedItem)
	{
		if (selectedItem == null)
		{
			_tree.getSelectionModel().clearSelection();
		}
		else
		{
			final TreePath nodePath = _treeModel.getNodePath(selectedItem);
			_tree.setSelectionPath(nodePath);
		}
	}

	/**
	 * Sets the model by an INode
	 * 
	 * @param node node to use as root for the model
	 */
	public void setModel(final INode node)
	{
		_treeModel.setRoot(node);
	}
	
	/**
	 * Gets the model
	 * 
	 * @return model
	 */
	public INode getModel()
	{
		return (INode) _treeModel.getRoot();
	}

	/**
	 * Collapses all tree nodes
	 */
	public void collapseAll()
	{
		final INode root = (INode) _tree.getModel().getRoot();
		expandAll(new TreePath(root), false, null);
	}

	/**
	 * Expandes all tree nodes
	 */
	public void expandAll()
	{
		final INode root = (INode) _tree.getModel().getRoot();
		expandAll(new TreePath(root), true, null);
	}

	/**
	 * Expandes all tree nodes
	 * 
	 * @param path TreePath to parent node
	 * @param expand Indicates if node should be expanded or collapsed
	 * @param except A node that shall not be expanded / collapsed
	 */
	private void expandAll(final TreePath path, final boolean expand, final TreePath except)
	{
		final INode node = (INode) path.getLastPathComponent();
		if (!node.isLeaf())
		{
			for (INode subNode : node)
			{
				final TreePath subPath = path.pathByAddingChild(subNode);
				expandAll(subPath, expand, except);
			}
		}

		if ((except == null) || !(path.isDescendant(except) || path.equals(except)))
		{
			if (expand)
			{
				_tree.expandPath(path);
			}
			else
			{
				if (path.getParentPath() != null)
				{
					_tree.collapsePath(path);
				}
			}
		}
	}

	/**
	 * Event
	 * 
	 * @param event event
	 */
	private void onTreeSelectionChanged(final TreeSelectionEvent event)
	{
		if (_checkboxAutocollapse.isSelected())
		{
			expandAll(new TreePath(_tree.getModel().getRoot()), false, event.getPath());
		}
	}

	/**
	 * Event
	 */
	private void autocollapseChanged()
	{
		if (_checkboxAutocollapse.isSelected())
		{
			// Fire selection event
			_tree.setSelectionPaths(_tree.getSelectionPaths());
		}
		_buttonExpandAll.setEnabled(!_checkboxAutocollapse.isSelected());
		_buttonCollapseAll.setEnabled(!_checkboxAutocollapse.isSelected());
	}

	/**
	 * Add a listener
	 * 
	 * @param listener listener
	 */
	public void addSelectionListener(final TreeSelectionListener listener)
	{
		_tree.getSelectionModel().addTreeSelectionListener(listener);
	}

	/**
	 * Remove a listener
	 * 
	 * @param listener listener
	 */
	public void removeSelectionListener(final TreeSelectionListener listener)
	{
		_tree.getSelectionModel().removeTreeSelectionListener(listener);
	}
}
