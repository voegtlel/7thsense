/*
 * BasicScenarioView.java
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import seventhsense.data.scenario.basicscenario.BasicScenarioNode;
import seventhsense.gui.music.MusicView;
import seventhsense.gui.soundfx.SoundFxView;

/**
 * Editor panel for Basic Scenario
 * 
 * @author Parallan, Drag-On
 *
 */
public class BasicScenarioView extends JPanel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	private final BasicScenarioMusicManagerPanel _musicManagerPanel;
	private final BasicScenarioSoundFxManagerPanel _soundFxManagerPanel;
	private boolean _performSplitterChangeEvent = true;

	/**
	 * Creates the basic scenario panel
	 */
	public BasicScenarioView()
	{
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		final GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		add(tabbedPane, gbc_tabbedPane);

		_musicManagerPanel = new BasicScenarioMusicManagerPanel();
		_musicManagerPanel.addSplitterPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent event) {
				if(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY.equals(event.getPropertyName()))
				{
					onMusicSplitterChanged();
				}
			}
		});
		tabbedPane.addTab("Music", null, _musicManagerPanel, null);
		
		_soundFxManagerPanel = new BasicScenarioSoundFxManagerPanel();
		_soundFxManagerPanel.addSplitterPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent event) {
				if(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY.equals(event.getPropertyName()))
				{
					onSoundFxSplitterChanged();
				}
			}
		});
		tabbedPane.addTab("Sound Effects", null, _soundFxManagerPanel, null);
	}

	/**
	 * Event.
	 */
	private void onSoundFxSplitterChanged()
	{
		if(_performSplitterChangeEvent)
		{
			_performSplitterChangeEvent = false;
			_musicManagerPanel.setDividerLocation(_soundFxManagerPanel.getDividerLocation());
			_performSplitterChangeEvent = true;
		}
	}

	/**
	 * Event.
	 */
	private void onMusicSplitterChanged()
	{
		if(_performSplitterChangeEvent)
		{
			_performSplitterChangeEvent = false;
			_soundFxManagerPanel.setDividerLocation(_musicManagerPanel.getDividerLocation());
			_performSplitterChangeEvent = true;
		}
	}

	/**
	 * Sets the displayed model
	 * 
	 * @param data model
	 */
	public void setModel(final BasicScenarioNode data)
	{
		if(data == null)
		{
			_musicManagerPanel.setModel(null);
			_soundFxManagerPanel.setModel(null);
		}
		else
		{
			_musicManagerPanel.setModel(data.getMusicManager());
			_soundFxManagerPanel.setModel(data.getSoundFxManager());
		}
	}
}
