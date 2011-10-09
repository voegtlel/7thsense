/*
 * BasicScenarioMusicManagerPanel.java
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

import javax.swing.JPanel;

import seventhsense.data.scenario.basicscenario.IScenarioManagerListener;
import seventhsense.data.scenario.basicscenario.MusicManager;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JCheckBox;
import java.awt.Insets;

/**
 * Panel for BasicScenarioMusicManager
 * 
 * @author Parallan
 *
 */
public class BasicScenarioMusicManagerPanel extends JPanel
{
	/**
	 * Default serial version
	 */
	
	private static final long serialVersionUID = 1L;
	private final BasicScenarioMusicTablePanel _basicScenarioMusicPanel;
	private final JCheckBox _checkboxShuffle;
	
	private final IScenarioManagerListener _scenarioManagerListener;
	
	private MusicManager _data;

	/**
	 * 
	 */
	public BasicScenarioMusicManagerPanel()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		_basicScenarioMusicPanel = new BasicScenarioMusicTablePanel();
		GridBagConstraints gbc__basicScenarioMusicPanel = new GridBagConstraints();
		gbc__basicScenarioMusicPanel.insets = new Insets(0, 0, 5, 0);
		gbc__basicScenarioMusicPanel.fill = GridBagConstraints.BOTH;
		gbc__basicScenarioMusicPanel.gridx = 0;
		gbc__basicScenarioMusicPanel.gridy = 0;
		add(_basicScenarioMusicPanel, gbc__basicScenarioMusicPanel);
		
		_checkboxShuffle = new JCheckBox("Shuffle Playing");
		_checkboxShuffle.setToolTipText("When checked, the music items will be shuffled on playing. Their playing order is randomized.");
		GridBagConstraints gbc__checkboxShuffle = new GridBagConstraints();
		gbc__checkboxShuffle.fill = GridBagConstraints.BOTH;
		gbc__checkboxShuffle.gridx = 0;
		gbc__checkboxShuffle.gridy = 1;
		add(_checkboxShuffle, gbc__checkboxShuffle);
		
		_scenarioManagerListener = new IScenarioManagerListener()
		{
			@Override
			public void changed(final String property)
			{
				BasicScenarioMusicManagerPanel.this.onScenarioManagerChanged(property);
			}
		};
	}

	/**
	 * Event.
	 * 
	 * @param property property
	 */
	private void onScenarioManagerChanged(final String property)
	{
		if(MusicManager.PROPERTY_RANDOMIZED.equals(property))
		{
			//TODO: Changed
		}
	}

	public void setModel(final MusicManager data)
	{
		if(_data != null)
		{
			_data.removeListener(_scenarioManagerListener);
		}
		_data = data;
		setEnabled(_data != null);
		if(_data != null)
		{
			_data.addListener(_scenarioManagerListener);
		}
	}
}
