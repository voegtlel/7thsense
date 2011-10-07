/*
 * SoundFxBasicPanel.java
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
package seventhsense.gui.soundfx;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import seventhsense.data.FileReference;
import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.SoundFxItem;
import seventhsense.data.scenario.sound.player.SoundEventType;
import seventhsense.gui.file.MediaFilePanel;
import seventhsense.gui.numberslider.NumberSlider;
import seventhsense.gui.player.SoundPlayer;
import seventhsense.gui.timeslider.TimeSlider;

/**
 * Panel for editing SoundFx
 * 
 * @author Parallan
 *
 */
public class SoundFxBasicPanel extends Panel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger(SoundFxBasicPanel.class.getName());

	private final TimeSlider _timeSliderMaximum;
	private final TimeSlider _timeSliderMinimum;
	private final NumberSlider _sliderVolume;
	private final MediaFilePanel _mediaFilePanel;
	private final SoundPlayer _player;

	private SoundFxItem _data;
	private transient final ISoundItemListener<SoundFxItem> _dataListener;

	/**
	 * 
	 */
	private boolean _performChangeEvents = true;

	/**
	 * Create the panel.
	 */
	public SoundFxBasicPanel()
	{
		super();

		_dataListener = new ISoundItemListener<SoundFxItem>()
		{
			@Override
			public void changed(final SoundFxItem item, final String property)
			{
				SoundFxBasicPanel.this.dataChanged(property);
			}

			@Override
			public void soundEvent(final SoundFxItem soundFxItem, final SoundEventType event)
			{
				// TODO Auto-generated method stub

			}
		};

		final GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[] { 0, 0 };
		gbl.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		this.setLayout(gbl);

		final JPanel panelMediaFile = new JPanel();
		panelMediaFile.setEnabled(false);
		panelMediaFile.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Media File", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		final GridBagConstraints gbc_panelMediaFile = new GridBagConstraints();
		gbc_panelMediaFile.fill = GridBagConstraints.BOTH;
		gbc_panelMediaFile.insets = new Insets(0, 0, 5, 0);
		gbc_panelMediaFile.gridx = 0;
		gbc_panelMediaFile.gridy = 0;
		add(panelMediaFile, gbc_panelMediaFile);
		final GridBagLayout gbl_panelMediaFile = new GridBagLayout();
		gbl_panelMediaFile.columnWidths = new int[] { 0, 0 };
		gbl_panelMediaFile.rowHeights = new int[] { 0, 0 };
		gbl_panelMediaFile.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelMediaFile.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelMediaFile.setLayout(gbl_panelMediaFile);

		_mediaFilePanel = new MediaFilePanel();
		_mediaFilePanel.setEnabled(false);
		final GridBagConstraints gbc_mediaFilePanel = new GridBagConstraints();
		gbc_mediaFilePanel.fill = GridBagConstraints.BOTH;
		gbc_mediaFilePanel.gridx = 0;
		gbc_mediaFilePanel.gridy = 0;
		panelMediaFile.add(_mediaFilePanel, gbc_mediaFilePanel);
		_mediaFilePanel.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				SoundFxBasicPanel.this.mediaFileChanged();
			}
		});

		final JPanel panel = new JPanel();
		panel.setEnabled(false);
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		this.add(panel, gbc_panel);
		final GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		final JLabel labelVolume = new JLabel("");
		labelVolume.setToolTipText("Volume for the sound");
		labelVolume.setIcon(new ImageIcon(SoundFxBasicPanel.class.getResource("/seventhsense/resources/Volume_20.png")));
		labelVolume.setEnabled(false);
		final GridBagConstraints gbc_labelVolume = new GridBagConstraints();
		gbc_labelVolume.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelVolume.insets = new Insets(0, 0, 0, 5);
		gbc_labelVolume.gridx = 0;
		gbc_labelVolume.gridy = 0;
		panel.add(labelVolume, gbc_labelVolume);

		_sliderVolume = new NumberSlider();
		_sliderVolume.setEnabled(false);
		_sliderVolume.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent event)
			{
				SoundFxBasicPanel.this.sliderVolumeChanged();
			}
		});
		labelVolume.setLabelFor(_sliderVolume);
		_sliderVolume.setToolTipText("Volume for the sound");
		_sliderVolume.setRange(0.0, 100.0, 1.0);
		final GridBagConstraints gbc__sliderVolume = new GridBagConstraints();
		gbc__sliderVolume.fill = GridBagConstraints.HORIZONTAL;
		gbc__sliderVolume.gridx = 1;
		gbc__sliderVolume.gridy = 0;
		panel.add(_sliderVolume, gbc__sliderVolume);

		final JPanel panelRandomizer = new JPanel();
		panelRandomizer.setEnabled(false);
		panelRandomizer.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Restart Randomizer Time Range", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_panelRandomizer = new GridBagConstraints();
		gbc_panelRandomizer.insets = new Insets(0, 0, 5, 0);
		gbc_panelRandomizer.fill = GridBagConstraints.BOTH;
		gbc_panelRandomizer.gridx = 0;
		gbc_panelRandomizer.gridy = 2;
		this.add(panelRandomizer, gbc_panelRandomizer);
		final GridBagLayout gbl_panelRandomizer = new GridBagLayout();
		gbl_panelRandomizer.columnWidths = new int[] { 0, 217, 0 };
		gbl_panelRandomizer.rowHeights = new int[] { 10, 0, 0 };
		gbl_panelRandomizer.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelRandomizer.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelRandomizer.setLayout(gbl_panelRandomizer);

		final JLabel labelTimeRangeMinimum = new JLabel("");
		labelTimeRangeMinimum.setToolTipText("Minimum time to wait for restarting the sound. Set to infinite to never restart or to 0 to loop");
		labelTimeRangeMinimum.setIcon(new ImageIcon(SoundFxBasicPanel.class.getResource("/seventhsense/resources/ReplayMinTime_20.png")));
		labelTimeRangeMinimum.setEnabled(false);
		final GridBagConstraints gbc_labelTimeRangeMinimum = new GridBagConstraints();
		gbc_labelTimeRangeMinimum.fill = GridBagConstraints.BOTH;
		gbc_labelTimeRangeMinimum.insets = new Insets(0, 0, 5, 5);
		gbc_labelTimeRangeMinimum.gridx = 0;
		gbc_labelTimeRangeMinimum.gridy = 0;
		panelRandomizer.add(labelTimeRangeMinimum, gbc_labelTimeRangeMinimum);

		_timeSliderMinimum = new TimeSlider();
		_timeSliderMinimum.setEnabled(false);
		_timeSliderMinimum.setToolTipText("Minimum time to wait for restarting the sound. Set to infinite to never restart or to 0 to loop");
		labelTimeRangeMinimum.setLabelFor(_timeSliderMinimum);
		_timeSliderMinimum.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent event)
			{
				SoundFxBasicPanel.this.timeSliderMinimumChanged();
			}
		});
		final GridBagLayout gbl_timeSliderMinimum = (GridBagLayout) _timeSliderMinimum.getLayout();
		gbl_timeSliderMinimum.rowWeights = new double[] { 1.0 };
		gbl_timeSliderMinimum.rowHeights = new int[] { 0 };
		gbl_timeSliderMinimum.columnWeights = new double[] { 1.0, 0.0 };
		gbl_timeSliderMinimum.columnWidths = new int[] { 0, 0 };
		final GridBagConstraints gbc_timeSliderMinimum = new GridBagConstraints();
		gbc_timeSliderMinimum.insets = new Insets(0, 0, 5, 0);
		gbc_timeSliderMinimum.fill = GridBagConstraints.BOTH;
		gbc_timeSliderMinimum.gridx = 1;
		gbc_timeSliderMinimum.gridy = 0;
		panelRandomizer.add(_timeSliderMinimum, gbc_timeSliderMinimum);

		final JLabel labelTimeRangeMaximum = new JLabel("");
		labelTimeRangeMaximum.setToolTipText("Maximum time to wait for restarting the sound. Set to infinite to never restart");
		labelTimeRangeMaximum.setIcon(new ImageIcon(SoundFxBasicPanel.class.getResource("/seventhsense/resources/ReplayMaxTime_20.png")));
		labelTimeRangeMaximum.setEnabled(false);
		final GridBagConstraints gbc_labelTimeRangeMaximum = new GridBagConstraints();
		gbc_labelTimeRangeMaximum.fill = GridBagConstraints.BOTH;
		gbc_labelTimeRangeMaximum.insets = new Insets(0, 0, 0, 5);
		gbc_labelTimeRangeMaximum.gridx = 0;
		gbc_labelTimeRangeMaximum.gridy = 1;
		panelRandomizer.add(labelTimeRangeMaximum, gbc_labelTimeRangeMaximum);

		_timeSliderMaximum = new TimeSlider();
		_timeSliderMaximum.setEnabled(false);
		_timeSliderMaximum.setToolTipText("Maximum time to wait for restarting the sound. Set to infinite to never restart");
		labelTimeRangeMaximum.setLabelFor(_timeSliderMaximum);
		_timeSliderMaximum.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent event)
			{
				SoundFxBasicPanel.this.timeSliderMaximumChanged();
			}
		});
		final GridBagLayout gbl_timeSliderMaximum = (GridBagLayout) _timeSliderMaximum.getLayout();
		gbl_timeSliderMaximum.rowWeights = new double[] { 1.0 };
		gbl_timeSliderMaximum.rowHeights = new int[] { 0 };
		gbl_timeSliderMaximum.columnWeights = new double[] { 1.0, 0.0 };
		gbl_timeSliderMaximum.columnWidths = new int[] { 0, 0 };
		final GridBagConstraints gbc_timeSliderMaximum = new GridBagConstraints();
		gbc_timeSliderMaximum.fill = GridBagConstraints.BOTH;
		gbc_timeSliderMaximum.gridx = 1;
		gbc_timeSliderMaximum.gridy = 1;
		panelRandomizer.add(_timeSliderMaximum, gbc_timeSliderMaximum);
		
		final JPanel panelPlayer = new JPanel();
		panelPlayer.setEnabled(false);
		panelPlayer.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Player", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_panelPlayer = new GridBagConstraints();
		gbc_panelPlayer.fill = GridBagConstraints.BOTH;
		gbc_panelPlayer.gridx = 0;
		gbc_panelPlayer.gridy = 3;
		add(panelPlayer, gbc_panelPlayer);
		final GridBagLayout gbl_panelPlayer = new GridBagLayout();
		gbl_panelPlayer.columnWidths = new int[]{0, 0};
		gbl_panelPlayer.rowHeights = new int[]{0, 0};
		gbl_panelPlayer.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelPlayer.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelPlayer.setLayout(gbl_panelPlayer);

		_player = new SoundPlayer();
		final GridBagConstraints gbc_player = new GridBagConstraints();
		gbc_player.fill = GridBagConstraints.BOTH;
		gbc_player.gridx = 0;
		gbc_player.gridy = 0;
		panelPlayer.add(_player, gbc_player);
	}

	@Override
	public void setEnabled(final boolean enabled)
	{
		super.setEnabled(enabled);
		for (Component c : this.getComponents())
		{
			c.setEnabled(enabled);
			if (c instanceof Container)
			{
				for (Component cSub : ((Container) c).getComponents())
				{
					cSub.setEnabled(enabled);
				}
			}
		}
	}

	/**
	 * Event
	 */
	private void mediaFileChanged()
	{
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			_data.setFile(_mediaFilePanel.getValue());
			_performChangeEvents = true;
			// Set new model for player
		}
	}

	/**
	 * Event
	 */
	private void timeSliderMinimumChanged()
	{
		final double minValue = _timeSliderMinimum.getValue();
		final double maxValue = _timeSliderMaximum.getValue();
		if (maxValue < minValue)
		{
			_timeSliderMaximum.setValue(minValue);
		}
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			_data.setMinReplayWaitTime(_timeSliderMinimum.getValue());
			_performChangeEvents = true;
		}
	}

	/**
	 * Event
	 */
	private void timeSliderMaximumChanged()
	{
		final double minValue = _timeSliderMinimum.getValue();
		final double maxValue = _timeSliderMaximum.getValue();
		if (minValue > maxValue)
		{
			_timeSliderMinimum.setValue(maxValue);
		}
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			_data.setMaxReplayWaitTime(_timeSliderMaximum.getValue());
			_performChangeEvents = true;
		}
	}

	/**
	 * Event
	 */
	private void sliderVolumeChanged()
	{
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			_data.setVolume(_sliderVolume.getValue() / 100.0);
			_performChangeEvents = true;
		}
	}

	/**
	 * Event
	 * @param property property
	 */
	private void dataChanged(final String property)
	{
		if (_performChangeEvents)
		{
			_performChangeEvents = false;
			if ((property == null) || "file".equals(property))
			{
				_mediaFilePanel.setValue(_data.getFile());
			}
			if ((property == null) || "minReplayWaitTime".equals(property))
			{
				_timeSliderMinimum.setValue(_data.getMinReplayWaitTime());
			}
			if ((property == null) || "maxReplayWaitTime".equals(property))
			{
				_timeSliderMaximum.setValue(_data.getMaxReplayWaitTime());
			}
			if ((property == null) || "volume".equals(property))
			{
				_sliderVolume.setValue(_data.getVolume() * 100.0);
			}
			_performChangeEvents = true;
		}
	}

	/**
	 * Set the model
	 * 
	 * @param data model
	 */
	public void setModel(final SoundFxItem data)
	{
		if (_data != data)
		{
			if (_data != null)
			{
				_data.removeListener(_dataListener);

				final SoundFxItem oldData = _data;
				final FileReference file = _mediaFilePanel.getValue();
				final double minReplayWaitTime = _timeSliderMinimum.getValue();
				final double maxReplayWaitTime = _timeSliderMaximum.getValue();
				final double volume = _sliderVolume.getValue() / 100.0;
				oldData.setFile(file);
				oldData.setMinReplayWaitTime(minReplayWaitTime);
				oldData.setMaxReplayWaitTime(maxReplayWaitTime);
				oldData.setVolume(volume);
			}
			_data = data;
			setEnabled(_data != null);
			_player.setModel(_data);
			if (_data != null)
			{
				dataChanged(null);
				_data.addListener(_dataListener);
			}
		}
	}
}
