/*
 * AbstractBasicScenarioTablePanel.java
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
package seventhsense.gui.basicscenario;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import seventhsense.data.FileReference;
import seventhsense.data.IItem;
import seventhsense.data.scenario.basicscenario.BasicScenarioNode;
import seventhsense.gui.table.DisabledCheckboxCellRenderer;
import seventhsense.gui.table.FileReferenceCellRenderer;
import seventhsense.gui.table.TristateTableRowSorter;

/**
 * Abstract class for basic scenario table showing subitems
 * @author Parallan, Drag-On
 *
 * @param <E> type of item
 */
public abstract class AbstractBasicScenarioTablePanel<E extends IItem> extends JPanel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	protected final JTable _table;
	protected final TristateTableRowSorter<TableModel> _rowSorter;
	private final JButton _buttonAdd;
	private final JButton _buttonDelete;
	private final JButton _buttonUp;
	private final JButton _buttonDown;

	/**
	 * Creates the basic scenario table panel
	 */
	public AbstractBasicScenarioTablePanel()
	{
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		final GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.fill = GridBagConstraints.BOTH;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		add(toolBar, gbc_toolBar);

		_buttonAdd = new JButton("");
		_buttonAdd.setEnabled(false);
		_buttonAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				AbstractBasicScenarioTablePanel.this.onAddAction();
			}
		});
		_buttonAdd.setToolTipText("Add new item");
		toolBar.add(_buttonAdd);
		_buttonAdd.setIcon(new ImageIcon(AbstractBasicScenarioTablePanel.class.getResource("/seventhsense/resources/Add_20.png")));

		_buttonDelete = new JButton("");
		_buttonDelete.setEnabled(false);
		_buttonDelete.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				AbstractBasicScenarioTablePanel.this.onDeleteAction();
			}
		});
		_buttonDelete.setToolTipText("Delete selected item");
		_buttonDelete.setIcon(new ImageIcon(AbstractBasicScenarioTablePanel.class.getResource("/seventhsense/resources/Delete_20.png")));
		toolBar.add(_buttonDelete);

		_buttonUp = new JButton("");
		_buttonUp.setEnabled(false);
		_buttonUp.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				AbstractBasicScenarioTablePanel.this.onUpAction();
			}
		});
		_buttonUp.setIcon(new ImageIcon(AbstractBasicScenarioTablePanel.class.getResource("/seventhsense/resources/Up_20.png")));
		_buttonUp.setToolTipText("Move selected item up");
		toolBar.add(_buttonUp);

		_buttonDown = new JButton("");
		_buttonDown.setEnabled(false);
		_buttonDown.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				AbstractBasicScenarioTablePanel.this.onDownAction();
			}
		});
		_buttonDown.setIcon(new ImageIcon(AbstractBasicScenarioTablePanel.class.getResource("/seventhsense/resources/Down_20.png")));
		_buttonDown.setToolTipText("Move selected item down");
		toolBar.add(_buttonDown);

		final JScrollPane scrollPaneTable = new JScrollPane();
		final GridBagConstraints gbc_scrollPaneTable = new GridBagConstraints();
		gbc_scrollPaneTable.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneTable.gridx = 0;
		gbc_scrollPaneTable.gridy = 1;
		add(scrollPaneTable, gbc_scrollPaneTable);

		_table = new JTable();
		_table.setDefaultRenderer(Boolean.class, new DisabledCheckboxCellRenderer());
		_table.setDefaultRenderer(FileReference.class, new FileReferenceCellRenderer());
		_table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent e)
			{
				AbstractBasicScenarioTablePanel.this.tableSelectionChanged();
			}
		});
		_rowSorter = new TristateTableRowSorter<TableModel>();
		_table.setRowSorter(_rowSorter);
		scrollPaneTable.setViewportView(_table);
	}
	
	@Override
	public void setEnabled(final boolean enabled)
	{
		super.setEnabled(enabled);
		_buttonAdd.setEnabled(enabled);
		_buttonDelete.setEnabled(enabled && (_table.getSelectedRowCount() != 0));
		_buttonUp.setEnabled(enabled && (_table.getSelectedRowCount() != 0));
		_buttonDown.setEnabled(enabled && (_table.getSelectedRowCount() != 0));
	}

	/**
	 * sets the model
	 * @param data model data
	 */
	public abstract void setModel(final BasicScenarioNode data);

	/**
	 * adds a table listener
	 * @param listener listener to add
	 */
	public void addTableSelectionListener(final ListSelectionListener listener)
	{
		_table.getSelectionModel().addListSelectionListener(listener);
	}

	/**
	 * removes a table listener
	 * @param listener listener to remove
	 */
	public void removeTableSelectionListener(final ListSelectionListener listener)
	{
		_table.getSelectionModel().removeListSelectionListener(listener);
	}
	
	/**
	 * Event.
	 */
	protected void tableSelectionChanged()
	{
		_buttonDelete.setEnabled(this.isEnabled() && (_table.getSelectedRowCount() != 0));
		_buttonUp.setEnabled(this.isEnabled() && (_table.getSelectedRowCount() != 0));
		_buttonDown.setEnabled(this.isEnabled() && (_table.getSelectedRowCount() != 0));
	}

	/**
	 * Gets the selected item
	 * 
	 * @return selected item
	 */
	public abstract E getSelectedItem();
	
	/**
	 * Sets the selected item
	 * 
	 * @param item item to select
	 */
	public abstract void setSelectedItem(E item);

	/**
	 * Event.
	 */
	protected abstract void onAddAction();

	/**
	 * Event.
	 */
	protected abstract void onDeleteAction();

	/**
	 * Event.
	 */
	protected abstract void onUpAction();

	/**
	 * Event.
	 */
	protected abstract void onDownAction();
}
