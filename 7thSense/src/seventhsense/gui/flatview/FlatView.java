/*
 * FlatView.java
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import seventhsense.data.INode;
import seventhsense.gui.ModelView;

/**
 * Represents the "flat" (list-) view of a tree
 * 
 * @author Drag-On, Parallan
 *
 */
public class FlatView extends ModelView<INode>
{

	/**
	 * Default, required by Serializable (JList)
	 */
	private static final long serialVersionUID = 1L;
	
	private final JList _list;
	
	private final FlatViewListModel _listModel;
	
	/**
	 * Creates the flat view
	 */
	public FlatView()
	{
		super();
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		
		_list = new JList();
		_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_listModel = new FlatViewListModel();
		_list.setModel(_listModel);
		_list.setDragEnabled(true);
		_list.setTransferHandler(new FlatViewTransferHandler(this));
		final GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;
		add(_list, gbc_list);
	}
	
	/**
	 * Sets the model by an INode
	 * 
	 * @param node node to use as root for the model
	 */
	@Override
	public void setModel(final INode node)
	{
		_listModel.setModel(node);
	}
	
	/**
	 * Sets the selected node
	 * 
	 * @param selectedNode selected node
	 */
	public void setSelectedItem(final INode selectedNode)
	{
		if(selectedNode == null)
		{
			_list.getSelectionModel().clearSelection();
		}
		else
		{
			final int nodeIndex = _listModel.getListNodeIndex(selectedNode);
			_list.setSelectedIndex(nodeIndex);
		}
	}
	
	/**
	 * Gets the selected node
	 * 
	 * @return selected node
	 */
	public INode getSelectedItem()
	{
		final int selectedIndex = _list.getSelectedIndex();
		if(selectedIndex == -1)
		{
			return null;
		}
		else
		{
			return ((FlatViewEntry)_listModel.get(selectedIndex)).getNode();
		}
	}

	/**
	 * Adds a list selection listener
	 * 
	 * @param listener listener
	 */
	public void addListSelectionListener(final ListSelectionListener listener)
	{
		_list.getSelectionModel().addListSelectionListener(listener);
	}
}
