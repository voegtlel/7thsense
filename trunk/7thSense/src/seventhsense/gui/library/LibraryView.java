/*
 * LibraryView.java
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
package seventhsense.gui.library;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;

import seventhsense.data.FolderNode;
import seventhsense.data.INode;
import seventhsense.data.LinkNode;
import seventhsense.data.scenario.basicscenario.BasicScenarioNode;
import seventhsense.gui.ModelView;
import seventhsense.gui.basicscenario.BasicScenarioView;
import seventhsense.gui.database.DatabaseView;
import seventhsense.gui.database.INodeSelectionListener;
import seventhsense.gui.file.NodeFile;
import seventhsense.sound.engine.PlayerMixer;

/**
 * Panel for viewing the library inclusive a editor
 * 
 * @author Parallan
 *
 */
public class LibraryView extends ModelView<INode>
{

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private final DatabaseView _databaseView;
	private final BasicScenarioView _basicScenarioView;
	private final JPopupMenu _popupMenuAdd;
	private final JButton _buttonAdd;

	/**
	 * Creates a view for the library
	 */
	public LibraryView(final PlayerMixer mixer)
	{
		super();

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		
		final JToolBar toolBarFile = new JToolBar();
		toolBarFile.setFloatable(false);
		final GridBagConstraints gbc_toolBarFile = new GridBagConstraints();
		gbc_toolBarFile.fill = GridBagConstraints.BOTH;
		gbc_toolBarFile.insets = new Insets(0, 0, 5, 0);
		gbc_toolBarFile.gridx = 0;
		gbc_toolBarFile.gridy = 0;
		add(toolBarFile, gbc_toolBarFile);
		
		final JButton buttonSave = new JButton("");
		buttonSave.setToolTipText("Save library (automatically done on exit)");
		buttonSave.setIcon(new ImageIcon(LibraryView.class.getResource("/seventhsense/resources/Save_20.png")));
		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				LibraryView.this.onSaveAction();
			}
		});
		toolBarFile.add(buttonSave);
		
		final Separator separator1 = new Separator();
		toolBarFile.add(separator1);
		
		final JButton buttonRevalidate = new JButton("");
		buttonRevalidate.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				LibraryView.this.onRevalidateAction();
			}
		});
		buttonRevalidate.setIcon(new ImageIcon(LibraryView.class.getResource("/seventhsense/resources/Warning_20.png")));
		buttonRevalidate.setToolTipText("Validate all nodes: Check if everything is fine.");
		toolBarFile.add(buttonRevalidate);

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		splitPane.setResizeWeight(0.5);
		final GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		add(splitPane, gbc_splitPane);

		final JPanel panelDatabase = new JPanel();
		final GridBagLayout gbl_panelDatabase = new GridBagLayout();
		gbl_panelDatabase.columnWidths = new int[] { 0, 0 };
		gbl_panelDatabase.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelDatabase.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelDatabase.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panelDatabase.setLayout(gbl_panelDatabase);
		
		_basicScenarioView = new BasicScenarioView(mixer);
		splitPane.setRightComponent(_basicScenarioView);
		splitPane.setLeftComponent(panelDatabase);

		final JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		final GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.fill = GridBagConstraints.BOTH;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		panelDatabase.add(toolBar, gbc_toolBar);

		_buttonAdd = new JButton("");
		_buttonAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				LibraryView.this.onAddActionPerformed();
			}
		});
		_buttonAdd.setToolTipText("Add a new item");
		_buttonAdd.setIcon(new ImageIcon(LibraryView.class.getResource("/seventhsense/resources/Add_20.png")));
		toolBar.add(_buttonAdd);

		final JButton buttonDelete = new JButton("");
		buttonDelete.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				LibraryView.this.onDeleteActionPerformed();
			}
		});
		buttonDelete.setToolTipText("Delete the selected item");
		buttonDelete.setIcon(new ImageIcon(LibraryView.class.getResource("/seventhsense/resources/Delete_20.png")));
		toolBar.add(buttonDelete);

		final JButton buttonUp = new JButton("");
		buttonUp.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				LibraryView.this.onMoveUpActionPerformed();
			}
		});
		buttonUp.setIcon(new ImageIcon(LibraryView.class.getResource("/seventhsense/resources/Up_20.png")));
		buttonUp.setToolTipText("Move selected item up");
		toolBar.add(buttonUp);

		final JButton buttonDown = new JButton("");
		buttonDown.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				LibraryView.this.onMoveDownActionPerformed();
			}
		});
		buttonDown.setIcon(new ImageIcon(LibraryView.class.getResource("/seventhsense/resources/Down_20.png")));
		buttonDown.setToolTipText("Move selected item down");
		toolBar.add(buttonDown);
		
		_databaseView = new DatabaseView();
		final GridBagConstraints gbc_databaseView = new GridBagConstraints();
		gbc_databaseView.fill = GridBagConstraints.BOTH;
		gbc_databaseView.gridx = 0;
		gbc_databaseView.gridy = 1;
		panelDatabase.add(_databaseView, gbc_databaseView);
		_databaseView.addSelectionChangedListener(new INodeSelectionListener()
		{
			public void selectionChanged(final INode newSelected)
			{
				LibraryView.this.onLibrarySelectionChanged();
			}
		});
		
		_popupMenuAdd = new JPopupMenu();
		
		final JMenuItem menuItemAddFolder = new JMenuItem("Add Folder");
		menuItemAddFolder.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				LibraryView.this.onAddFolderActionPerformed();
			}
		});
		_popupMenuAdd.add(menuItemAddFolder);
		
		final JMenuItem menuItemAddBasicScenario = new JMenuItem("Add Basic Scenario");
		menuItemAddBasicScenario.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				LibraryView.this.onAddBasicScenarioActionPerformed();
			}
		});
		_popupMenuAdd.add(menuItemAddBasicScenario);
		
		final JMenuItem menuItemAddLink = new JMenuItem("Add Link");
		menuItemAddLink.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				LibraryView.this.onAddLinkActionPerformed();
			}
		});
		_popupMenuAdd.add(menuItemAddLink);
	}
	
	/**
	 * Event.
	 */
	private void onSaveAction()
	{
		NodeFile.saveNode(getModel(), NodeFile.DefaultLibraryFile);
	}
	
	/**
	 * Event.
	 */
	private void onRevalidateAction()
	{
		getModel().validate(true);
	}

	/**
	 * Set the model
	 * 
	 * @param root model
	 */
	@Override
	public void setModel(final INode root)
	{
		_databaseView.setModel(root);
	}
	
	/**
	 * Get the model
	 * 
	 * @return model
	 */
	public INode getModel()
	{
		return _databaseView.getModel();
	}

	/**
	 * Event.
	 */
	private final void onLibrarySelectionChanged()
	{
		final INode selectedItem = _databaseView.getSelectedItem();
		if(selectedItem == null)
		{
			_basicScenarioView.setModel(null);
		}
		else
		{
			final INode realSelectedItem = selectedItem.getRealNode();
			if (realSelectedItem instanceof BasicScenarioNode)
			{
				_basicScenarioView.setModel((BasicScenarioNode) realSelectedItem);
			}
			else
			{
				_basicScenarioView.setModel(null);
			}
		}
	}
	
	/**
	 * Event.
	 */
	private void onAddActionPerformed()
	{
		_popupMenuAdd.show(this, _buttonAdd.getX(), _buttonAdd.getY() + _buttonAdd.getHeight());
	}
	
	/**
	 * Adds a node
	 * @param node node to add
	 */
	private void addNode(final INode node)
	{
		INode selectedItem = _databaseView.getSelectedItem();
		if(selectedItem == null)
		{
			selectedItem = _databaseView.getModel();
		}
		int newIndex = -1;
		if(!selectedItem.getAllowsChildren())
		{
			newIndex = selectedItem.getParent().getIndex(selectedItem) + 1;
			selectedItem = selectedItem.getParent();
		}
		selectedItem.addNode(newIndex, node);
		_databaseView.setSelectedItem(node);
	}
	
	/**
	 * Event
	 */
	private void onAddFolderActionPerformed()
	{
		addNode(new FolderNode("New Folder"));
	}
	
	/**
	 * Event
	 */
	private void onAddBasicScenarioActionPerformed()
	{
		addNode(new BasicScenarioNode("New Basic Scenario"));
	}
	
	/**
	 * Event.
	 */
	private void onAddLinkActionPerformed()
	{
		final INode selectedItem = _databaseView.getSelectedItem();
		if(selectedItem != null)
		{
			final INode newNode = new LinkNode(selectedItem);
			final int newIndex = selectedItem.getParent().getIndex(selectedItem) + 1;
			selectedItem.getParent().addNode(newIndex, newNode);
		}
	}

	/**
	 * Event
	 */
	private void onDeleteActionPerformed()
	{
		final INode selectedItem = _databaseView.getSelectedItem();
		if(selectedItem != null)
		{
			selectedItem.getParent().removeNode(selectedItem);
		}
	}

	/**
	 * Event
	 */
	private void onMoveUpActionPerformed()
	{
		final INode selectedItem = _databaseView.getSelectedItem();
		if(selectedItem != null)
		{
			final INode parentNode = selectedItem.getParent();
			final int lastIndex = parentNode.getIndex(selectedItem);
			if(lastIndex > 0)
			{
				parentNode.removeNode(selectedItem);
				parentNode.addNode(lastIndex - 1, selectedItem);
				_databaseView.setSelectedItem(selectedItem);
			}
		}
	}

	/**
	 * Event
	 */
	private void onMoveDownActionPerformed()
	{
		final INode selectedItem = _databaseView.getSelectedItem();
		if(selectedItem != null)
		{
			final INode parentNode = selectedItem.getParent();
			final int lastIndex = parentNode.getIndex(selectedItem);
			if(lastIndex < parentNode.getChildCount() - 1)
			{
				parentNode.removeNode(selectedItem);
				parentNode.addNode(lastIndex + 1, selectedItem);
				_databaseView.setSelectedItem(selectedItem);
			}
		}
	}
}
