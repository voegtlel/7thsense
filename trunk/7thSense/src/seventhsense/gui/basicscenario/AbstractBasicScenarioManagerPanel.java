/*
 * AbstractBasicScenarioManagerPanel.java
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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import seventhsense.data.scenario.basicscenario.AbstractScenarioManager;
import seventhsense.data.scenario.sound.AbstractSoundItem;
import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.gui.ModelView;

/**
 * @author Parallan
 *
 */
public abstract class AbstractBasicScenarioManagerPanel<E extends AbstractSoundItem<E>, F extends AbstractScenarioManager<E>> extends ModelView<F>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private final JSplitPane _splitPane;
	private final AbstractBasicScenarioTablePanel<E> _tablePanel;
	private final ModelView<E> _modelView;
	
	/**
	 * 
	 */
	public AbstractBasicScenarioManagerPanel(final AbstractBasicScenarioTablePanel<E> tablePanel, final ModelView<E> modelView)
	{
		super();
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0};
		setLayout(gridBagLayout);
		
		_splitPane = new JSplitPane();
		_splitPane.setOneTouchExpandable(true);
		_splitPane.setDividerSize(10);
		_splitPane.setResizeWeight(0.5);
		final GridBagConstraints gbc__splitPane = new GridBagConstraints();
		gbc__splitPane.fill = GridBagConstraints.BOTH;
		gbc__splitPane.insets = new Insets(0, 0, 5, 0);
		gbc__splitPane.gridx = 0;
		gbc__splitPane.gridy = 0;
		add(_splitPane, gbc__splitPane);
		
		_tablePanel = tablePanel;
		_tablePanel.addTableSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent event)
			{
				AbstractBasicScenarioManagerPanel.this.onTableSelectionChanged();
			}
		});
		_splitPane.setLeftComponent(_tablePanel);
		
		_modelView = modelView;
		_splitPane.setRightComponent(_modelView);
	}
	
	@Override
	public void setModel(final F model)
	{
		_tablePanel.setModel(model.getList());
	}

	/**
	 * Event.
	 */
	private void onTableSelectionChanged()
	{
		_modelView.setModel(_tablePanel.getSelectedItem());
	}
	
	@Override
	public void setEnabled(final boolean enabled)
	{
		super.setEnabled(enabled);
		for (Component c : this.getComponents())
		{
			c.setEnabled(enabled);
		}
	}

	/**
	 * Sets the splitter divider location
	 * 
	 * @param dividerLocation dividerLocation
	 */
	public void setDividerLocation(final int dividerLocation)
	{
		_splitPane.setDividerLocation(dividerLocation);
	}

	/**
	 * Gets the splitter divider location
	 * 
	 * @return splitter divider location
	 */
	public int getDividerLocation()
	{
		return _splitPane.getDividerLocation();
	}

	/**
	 * Adds a property change listener to the splitter
	 * 
	 * @param listener listener
	 */
	public void addSplitterPropertyChangeListener(final PropertyChangeListener listener)
	{
		_splitPane.addPropertyChangeListener(listener);
	}
}