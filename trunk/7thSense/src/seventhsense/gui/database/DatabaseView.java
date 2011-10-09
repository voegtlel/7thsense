/*
 * DatabaseView.java
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
package seventhsense.gui.database;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import seventhsense.data.INode;
import seventhsense.data.eventlist.EventList;
import seventhsense.gui.ModelView;
import seventhsense.gui.flatview.FlatView;
import seventhsense.gui.treeview.TreeView;

/**
 * View of the database
 * 
 * @author Parallan
 *
 */
public class DatabaseView extends ModelView<INode>
{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * listeners
	 */
	private final EventList<INodeSelectionListener> _nodeSelectionListeners = new EventList<INodeSelectionListener>();
	
	private final TreeView _treeView;
	private final FlatView _flatView;
	
	private boolean _performEvents = true;

	/**
	 * Creates the database view
	 */
	public DatabaseView()
	{
		super();
		
		setLayout(new BorderLayout(0, 0));
		
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		add(tabbedPane, BorderLayout.CENTER);
		
		_treeView = new TreeView();
		_treeView.addSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(final TreeSelectionEvent event)
			{
				onTreeSelectionChanged();
			}
		});
		tabbedPane.addTab("Tree", null, _treeView, "Shows the scenarios as a tree");
		
		final JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Flat", null, scrollPane, "Shows the scenarios as a flat list");
		
		_flatView = new FlatView();
		_flatView.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent e)
			{
				onFlatSelectionChanged();
			}
		});
		scrollPane.setViewportView(_flatView);
	}
	
	/**
	 * Event.
	 */
	private void onTreeSelectionChanged()
	{
		if(_performEvents)
		{
			_performEvents = false;
			final INode selection = _treeView.getSelectedItem();
			_flatView.setSelectedItem(selection);
			_performEvents = true;
			fireSelectionChanged(selection);
		}
	}
	
	/**
	 * Event.
	 */
	private void onFlatSelectionChanged()
	{
		if(_performEvents)
		{
			final INode selection = _flatView.getSelectedItem();
			_performEvents = false;
			_treeView.setSelectedItem(selection);
			_performEvents = true;
			fireSelectionChanged(selection);
		}
	}
	
	/**
	 * Returns the selected item
	 * 
	 * @return selected item
	 */
	public INode getSelectedItem()
	{
		return _treeView.getSelectedItem();
	}
	
	/**
	 * Sets the selected item
	 * 
	 * @param selectedNode selected item
	 */
	public void setSelectedItem(final INode selectedNode)
	{
		_treeView.setSelectedItem(selectedNode);
	}

	/**
	 * Adds a listener
	 * 
	 * @param listener listener
	 */
	public void addSelectionChangedListener(final INodeSelectionListener listener)
	{
		_nodeSelectionListeners.add(listener);
	}
	
	/**
	 * Removes a listener
	 * 
	 * @param listener listener
	 */
	public void removeSelectionChangedListener(final INodeSelectionListener listener)
	{
		_nodeSelectionListeners.remove(listener);
	}
	
	/**
	 * Fires the event.
	 * 
	 * @param newSelected new selected item
	 */
	protected void fireSelectionChanged(final INode newSelected)
	{
		for(INodeSelectionListener listener : _nodeSelectionListeners.iterateEvents())
		{
			listener.selectionChanged(newSelected);
		}
	}
	
	/**
	 * Makes the view editable
	 * 
	 * @param editable editable
	 */
	public void setEditable(final boolean editable)
	{
		_treeView.setEditable(editable);
	}
	
	/**
	 * Sets the tree to edit only items on top level
	 * 
	 * @param onlyRootEditable only top level items are editable if true
	 */
	public void setOnlyRootEditable(final boolean onlyRootEditable)
	{
		_treeView.setOnylRootEditable(onlyRootEditable);
	}
	
	/**
	 * Sets the tree to enable only allow linked items for transfer
	 * 
	 * @param onlyLinks only links are allowed
	 */
	public void setOnlyLinks(final boolean onlyLinks)
	{
		_treeView.setOnlyLinks(onlyLinks);
	}

	/**
	 * Gets the model
	 * 
	 * @return model
	 */
	public INode getModel()
	{
		return _treeView.getModel();
	}

	@Override
	public void setModel(final INode root)
	{
		_treeView.setModel(root);
		_flatView.setModel(root);
	}
}
