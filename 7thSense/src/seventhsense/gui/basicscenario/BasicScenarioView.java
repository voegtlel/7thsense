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
	private final BasicScenarioMusicPanel _musicPanel;
	private final MusicView _musicView;
	private final BasicScenarioSoundFxPanel _soundFxPanel;
	private final SoundFxView _soundFxView;
	private final JSplitPane _splitPaneMusic;
	private final JSplitPane _splitPaneSoundFx;
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

		_splitPaneMusic = new JSplitPane();
		_splitPaneMusic.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent event) {
				if(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY.equals(event.getPropertyName()))
				{
					onSplitPaneMusicChanged();
				}
			}
		});
		tabbedPane.addTab("Music", null, _splitPaneMusic, "View and edit music properties");
		_splitPaneMusic.setDividerSize(10);
		_splitPaneMusic.setOneTouchExpandable(true);
		_splitPaneMusic.setResizeWeight(0.5);

		_musicPanel = new BasicScenarioMusicPanel();
		_musicPanel.addTableSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent event)
			{
				BasicScenarioView.this.musicTableSelectionChanged();
			}
		});
		_splitPaneMusic.setLeftComponent(_musicPanel);

		_musicView = new MusicView();
		_splitPaneMusic.setRightComponent(_musicView);

		_splitPaneSoundFx = new JSplitPane();
		_splitPaneSoundFx.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent event) {
				if(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY.equals(event.getPropertyName()))
				{
					onSplitPaneSoundFxChanged();
				}
			}
		});
		_splitPaneSoundFx.setResizeWeight(0.5);
		_splitPaneSoundFx.setOneTouchExpandable(true);
		_splitPaneSoundFx.setDividerSize(10);
		tabbedPane.addTab("Sound Effects", null, _splitPaneSoundFx, "View and edit sound effect properties");

		_soundFxPanel = new BasicScenarioSoundFxPanel();
		_soundFxPanel.addTableSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent event)
			{
				BasicScenarioView.this.soundFxTableSelectionChanged();
			}
		});
		_splitPaneSoundFx.setLeftComponent(_soundFxPanel);

		_soundFxView = new SoundFxView();
		_splitPaneSoundFx.setRightComponent(_soundFxView);
	}

	/**
	 * Event.
	 */
	private void onSplitPaneSoundFxChanged()
	{
		if(_performSplitterChangeEvent)
		{
			_performSplitterChangeEvent = false;
			_splitPaneMusic.setDividerLocation(_splitPaneSoundFx.getDividerLocation());
			_performSplitterChangeEvent = true;
		}
	}

	/**
	 * Event.
	 */
	private void onSplitPaneMusicChanged()
	{
		if(_performSplitterChangeEvent)
		{
			_performSplitterChangeEvent = false;
			_splitPaneSoundFx.setDividerLocation(_splitPaneMusic.getDividerLocation());
			_performSplitterChangeEvent = true;
		}
	}

	/**
	 * Event.
	 */
	private void musicTableSelectionChanged()
	{
		_musicView.setModel(_musicPanel.getSelectedItem());
	}

	/**
	 * Event.
	 */
	private void soundFxTableSelectionChanged()
	{
		_soundFxView.setModel(_soundFxPanel.getSelectedItem());
	}

	/**
	 * Sets the displayed model
	 * 
	 * @param data model
	 */
	public void setModel(final BasicScenarioNode data)
	{
		_musicPanel.setModel(data);
		_soundFxPanel.setModel(data);
	}
}
