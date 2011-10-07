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

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import seventhsense.data.INode;

/**
 * Represents the "flat" (list-) view of a tree
 * 
 * @author Drag-On, Parallan
 *
 */
public class FlatView extends JList
{

	/**
	 * Default, required by Serializable (JList)
	 */
	private static final long serialVersionUID = 1L;
	
	private final FlatViewListModel _listModel;
	
	/**
	 * Creates the flat view
	 */
	public FlatView()
	{
		super();
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		_listModel = new FlatViewListModel();
		super.setModel(_listModel);
		setDragEnabled(true);
		setTransferHandler(new FlatViewTransferHandler(this));
	}
	
	/**
	 * Sets the model by an INode
	 * 
	 * @param node node to use as root for the model
	 */
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
			this.getSelectionModel().clearSelection();
		}
		else
		{
			final int nodeIndex = _listModel.getListNodeIndex(selectedNode);
			this.setSelectedIndex(nodeIndex);
		}
	}
	
	/**
	 * Gets the selected node
	 * 
	 * @return selected node
	 */
	public INode getSelectedItem()
	{
		final int selectedIndex = getSelectedIndex();
		if(selectedIndex == -1)
		{
			return null;
		}
		else
		{
			return ((FlatViewEntry)_listModel.get(selectedIndex)).getNode();
		}
	}
}
