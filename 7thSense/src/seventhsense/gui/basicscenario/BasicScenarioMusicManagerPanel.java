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

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import seventhsense.data.IPropertyChangedListener;
import seventhsense.data.scenario.basicscenario.AbstractScenarioManager;
import seventhsense.data.scenario.basicscenario.MusicManager;
import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.gui.music.MusicView;
import seventhsense.sound.engine.PlayerMixer;

/**
 * Panel for BasicScenarioMusicManager
 * 
 * @author Parallan
 *
 */
public class BasicScenarioMusicManagerPanel extends AbstractBasicScenarioManagerPanel<MusicItem, MusicManager>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private final JCheckBox _checkBoxRandomize;
	
	private final transient IPropertyChangedListener<AbstractScenarioManager<MusicItem>> _dataListener;
	
	private boolean _performChangeEvents = true;
	
	private MusicManager _data;

	/**
	 * 
	 */
	public BasicScenarioMusicManagerPanel()
	{
		super(new BasicScenarioMusicTablePanel(), new MusicView());
		
		_checkBoxRandomize = new JCheckBox("Shuffle Playing");
		_checkBoxRandomize.setToolTipText("When checked, the music items will be shuffled on playing. Their playing order is randomized.");
		final GridBagConstraints gbc__checkboxShuffle = new GridBagConstraints();
		gbc__checkboxShuffle.fill = GridBagConstraints.BOTH;
		gbc__checkboxShuffle.gridx = 0;
		gbc__checkboxShuffle.gridy = 1;
		_checkBoxRandomize.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				BasicScenarioMusicManagerPanel.this.isRandomizedChanged();
			}
		});
		add(_checkBoxRandomize, gbc__checkboxShuffle);
		
		_dataListener = new IPropertyChangedListener<AbstractScenarioManager<MusicItem>>()
		{
			@Override
			public void propertyChanged(final AbstractScenarioManager<MusicItem> caller, final String property)
			{
				BasicScenarioMusicManagerPanel.this.dataChanged(property);
			}
		};
	}
	
	/**
	 * Event.
	 */
	private void isRandomizedChanged()
	{
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			_data.setRandomized(_checkBoxRandomize.isSelected());
			_performChangeEvents = true;
		}
	}

	/**
	 * Event.
	 * 
	 * @param property property
	 */
	private void dataChanged(final String property)
	{
		if (_performChangeEvents)
		{
			_performChangeEvents = false;
			if ((property == null) || MusicManager.PROPERTY_RANDOMIZED.equals(property))
			{
				_checkBoxRandomize.setSelected(_data.isRandomized());
			}
			_performChangeEvents = true;
		}
	}

	/**
	 * Sets the model
	 * 
	 * @param data model
	 */
	public void setModel(final MusicManager data)
	{
		if (_data != data)
		{
			if (_data != null)
			{
				_data.removeListener(_dataListener);

				final MusicManager oldData = _data;
				final boolean randomize = _checkBoxRandomize.isSelected();
				oldData.setRandomized(randomize);
			}
			_data = data;
			super.setModel(_data);
			setEnabled(_data != null);
			if (_data != null)
			{
				dataChanged(null);
				_data.addListener(_dataListener);
			}
		}
	}
}
