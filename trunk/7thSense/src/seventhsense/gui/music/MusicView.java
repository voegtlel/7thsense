/*
 * MusicView.java
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
package seventhsense.gui.music;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import seventhsense.data.file.FileReference;
import seventhsense.data.scenario.sound.FadeType;
import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.gui.ModelView;
import seventhsense.gui.file.MediaFilePanel;
import seventhsense.gui.numberslider.NumberSlider;
import seventhsense.gui.player.SoundPlayer;
import seventhsense.sound.engine.PlayerMixer;
import seventhsense.sound.engine.SoundEventType;

/**
 * Panel for music items
 * 
 * @author Parallan
 *
 */
public class MusicView extends ModelView<MusicItem>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger(MusicView.class.getName());
	
	private final MediaFilePanel _mediaFilePanel;
	private final JComboBox _comboBoxFadeType;
	private final JCheckBox _checkBoxIsIntroSong;
	private final JCheckBox _checkBoxIsLoopSong;
	private final NumberSlider _numberSliderVolume;
	private final SoundPlayer _player;

	/**
	 * Used to prevent infinite recursion
	 */
	private boolean _performChangeEvents = true;

	private transient final ISoundItemListener<MusicItem> _dataListener;
	private MusicItem _data;
	private JLabel _labelIntroSong;
	private JLabel _labelLoopSong;

	/**
	 * Creates the music view
	 */
	public MusicView()
	{
		super();

		_dataListener = new ISoundItemListener<MusicItem>()
		{
			@Override
			public void propertyChanged(final MusicItem item, final String property)
			{
				MusicView.this.dataChanged(property);
			}

			@Override
			public void soundEvent(final MusicItem item, final SoundEventType event)
			{
				// Not needed for now
			}
		};

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

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
		final GridBagConstraints gbc_mediaFilePanel = new GridBagConstraints();
		gbc_mediaFilePanel.fill = GridBagConstraints.BOTH;
		gbc_mediaFilePanel.gridx = 0;
		gbc_mediaFilePanel.gridy = 0;
		panelMediaFile.add(_mediaFilePanel, gbc_mediaFilePanel);
		_mediaFilePanel.setEnabled(false);
		_mediaFilePanel.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				MusicView.this.mediaFileChanged();
			}
		});

		final JPanel panelFadeType = new JPanel();
		panelFadeType.setEnabled(false);
		panelFadeType.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "General", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		final GridBagConstraints gbc_panelFadeType = new GridBagConstraints();
		gbc_panelFadeType.insets = new Insets(0, 0, 5, 0);
		gbc_panelFadeType.fill = GridBagConstraints.BOTH;
		gbc_panelFadeType.gridx = 0;
		gbc_panelFadeType.gridy = 1;
		add(panelFadeType, gbc_panelFadeType);
		final GridBagLayout gbl_panelFadeType = new GridBagLayout();
		gbl_panelFadeType.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelFadeType.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelFadeType.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelFadeType.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panelFadeType.setLayout(gbl_panelFadeType);

		final JLabel labelFadeType = new JLabel("");
		labelFadeType.setToolTipText("The fade type for the media file. Used when fading in");
		labelFadeType.setIcon(new ImageIcon(MusicView.class.getResource("/seventhsense/resources/FadeType_20.png")));
		labelFadeType.setEnabled(false);
		final GridBagConstraints gbc_labelFadeType = new GridBagConstraints();
		gbc_labelFadeType.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelFadeType.insets = new Insets(0, 0, 5, 5);
		gbc_labelFadeType.gridx = 0;
		gbc_labelFadeType.gridy = 0;
		panelFadeType.add(labelFadeType, gbc_labelFadeType);

		_comboBoxFadeType = new JComboBox(FadeType.values());
		_comboBoxFadeType.setEnabled(false);
		_comboBoxFadeType.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				MusicView.this.fadeTypeChanged();
			}
		});
		_comboBoxFadeType.setToolTipText("The fade type for the media file. Used when fading in");
		labelFadeType.setLabelFor(_comboBoxFadeType);
		final GridBagConstraints gbc__comboBoxFadeType = new GridBagConstraints();
		gbc__comboBoxFadeType.insets = new Insets(0, 0, 5, 0);
		gbc__comboBoxFadeType.fill = GridBagConstraints.HORIZONTAL;
		gbc__comboBoxFadeType.gridx = 1;
		gbc__comboBoxFadeType.gridy = 0;
		panelFadeType.add(_comboBoxFadeType, gbc__comboBoxFadeType);

		_checkBoxIsIntroSong = new JCheckBox("Is Intro Song");
		_checkBoxIsIntroSong.setEnabled(false);
		_checkBoxIsIntroSong.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				MusicView.this.isIntroSongChanged();
			}
		});
		
		_labelIntroSong = new JLabel("");
		_labelIntroSong.setLabelFor(_checkBoxIsIntroSong);
		_labelIntroSong.setEnabled(false);
		_labelIntroSong.setToolTipText("When checked, this song may be played as first song");
		_labelIntroSong.setIcon(new ImageIcon(MusicView.class.getResource("/seventhsense/resources/Intro_20.png")));
		final GridBagConstraints gbc_labelIntroSong = new GridBagConstraints();
		gbc_labelIntroSong.fill = GridBagConstraints.BOTH;
		gbc_labelIntroSong.insets = new Insets(0, 0, 5, 5);
		gbc_labelIntroSong.gridx = 0;
		gbc_labelIntroSong.gridy = 1;
		panelFadeType.add(_labelIntroSong, gbc_labelIntroSong);
		_checkBoxIsIntroSong.setToolTipText("When checked, this song may be played as first song");
		final GridBagConstraints gbc__checkBoxIsIntroSong = new GridBagConstraints();
		gbc__checkBoxIsIntroSong.insets = new Insets(0, 0, 5, 0);
		gbc__checkBoxIsIntroSong.fill = GridBagConstraints.HORIZONTAL;
		gbc__checkBoxIsIntroSong.gridx = 1;
		gbc__checkBoxIsIntroSong.gridy = 1;
		panelFadeType.add(_checkBoxIsIntroSong, gbc__checkBoxIsIntroSong);

		_checkBoxIsLoopSong = new JCheckBox("Is Loop Song");
		_checkBoxIsLoopSong.setEnabled(false);
		_checkBoxIsLoopSong.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				MusicView.this.isLoopSongChanged();
			}
		});
		
		_labelLoopSong = new JLabel("");
		_labelLoopSong.setLabelFor(_checkBoxIsLoopSong);
		_labelLoopSong.setEnabled(false);
		_labelLoopSong.setIcon(new ImageIcon(MusicView.class.getResource("/seventhsense/resources/Loop_20.png")));
		_labelLoopSong.setToolTipText("When checked, this song may be played after the first song");
		final GridBagConstraints gbc_labelLoopSong = new GridBagConstraints();
		gbc_labelLoopSong.fill = GridBagConstraints.BOTH;
		gbc_labelLoopSong.insets = new Insets(0, 0, 5, 5);
		gbc_labelLoopSong.gridx = 0;
		gbc_labelLoopSong.gridy = 2;
		panelFadeType.add(_labelLoopSong, gbc_labelLoopSong);
		_checkBoxIsLoopSong.setToolTipText("When checked, this song may be played after the first song");
		final GridBagConstraints gbc__checkBoxIsLoopSong = new GridBagConstraints();
		gbc__checkBoxIsLoopSong.insets = new Insets(0, 0, 5, 0);
		gbc__checkBoxIsLoopSong.fill = GridBagConstraints.HORIZONTAL;
		gbc__checkBoxIsLoopSong.gridx = 1;
		gbc__checkBoxIsLoopSong.gridy = 2;
		panelFadeType.add(_checkBoxIsLoopSong, gbc__checkBoxIsLoopSong);

		final JLabel labelVolume = new JLabel("");
		labelVolume.setToolTipText("Volume for the music");
		labelVolume.setIcon(new ImageIcon(MusicView.class.getResource("/seventhsense/resources/Volume_20.png")));
		labelVolume.setEnabled(false);
		final GridBagConstraints gbc_labelVolume = new GridBagConstraints();
		gbc_labelVolume.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelVolume.insets = new Insets(0, 0, 0, 5);
		gbc_labelVolume.gridx = 0;
		gbc_labelVolume.gridy = 3;
		panelFadeType.add(labelVolume, gbc_labelVolume);

		_numberSliderVolume = new NumberSlider();
		_numberSliderVolume.setEnabled(false);
		_numberSliderVolume.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent event)
			{
				MusicView.this.volumeChanged();
			}
		});
		_numberSliderVolume.setToolTipText("Volume for the music");
		_numberSliderVolume.setRange(0.0, 100.0, 5.0);
		labelVolume.setLabelFor(_numberSliderVolume);
		final GridBagConstraints gbc__numberSliderVolume = new GridBagConstraints();
		gbc__numberSliderVolume.fill = GridBagConstraints.BOTH;
		gbc__numberSliderVolume.gridx = 1;
		gbc__numberSliderVolume.gridy = 3;
		panelFadeType.add(_numberSliderVolume, gbc__numberSliderVolume);
		
		final JPanel panel = new JPanel();
		panel.setEnabled(false);
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Player", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		add(panel, gbc_panel);
		final GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);

		_player = new SoundPlayer();
		_player.setEnabled(false);
		final GridBagConstraints gbc_player = new GridBagConstraints();
		gbc_player.fill = GridBagConstraints.BOTH;
		gbc_player.gridx = 0;
		gbc_player.gridy = 0;
		panel.add(_player, gbc_player);
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
	 * Event.
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
	 * Event.
	 */
	private void fadeTypeChanged()
	{
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			_data.setFadeType((FadeType) _comboBoxFadeType.getSelectedItem());
			_performChangeEvents = true;
		}
	}

	/**
	 * Event.
	 */
	private void isIntroSongChanged()
	{
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			_data.setIsIntroSong(_checkBoxIsIntroSong.isSelected());
			_performChangeEvents = true;
		}
	}

	/**
	 * Event.
	 */
	private void isLoopSongChanged()
	{
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			_data.setIsLoopSong(_checkBoxIsLoopSong.isSelected());
			_performChangeEvents = true;
		}
	}

	/**
	 * Event.
	 */
	private void volumeChanged()
	{
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			_data.setVolume(_numberSliderVolume.getValue() / 100.0);
			_performChangeEvents = true;
		}
	}

	/**
	 * Event.
	 */
	private void dataChanged(final String property)
	{
		if (_performChangeEvents)
		{
			_performChangeEvents = false;
			if ((property == null) || MusicItem.PROPERTY_FILE.equals(property))
			{
				_mediaFilePanel.setValue(_data.getFile());
			}
			if ((property == null) || MusicItem.PROPERTY_FADE_TYPE.equals(property))
			{
				_comboBoxFadeType.setSelectedItem(_data.getFadeType());
			}
			if ((property == null) || MusicItem.PROPERTY_INTRO_SONG.equals(property))
			{
				_checkBoxIsIntroSong.setSelected(_data.isIntroSong());
			}
			if ((property == null) || MusicItem.PROPERTY_LOOP_SONG.equals(property))
			{
				_checkBoxIsLoopSong.setSelected(_data.isLoopSong());
			}
			if ((property == null) || MusicItem.PROPERTY_VOLUME.equals(property))
			{
				_numberSliderVolume.setValue(_data.getVolume() * 100.0);
			}
			_performChangeEvents = true;
		}
	}

	@Override
	public void setModel(final MusicItem data)
	{
		if (_data != data)
		{
			if (_data != null)
			{
				_data.removeListener(_dataListener);

				final MusicItem oldData = _data;
				final FileReference file = _mediaFilePanel.getValue();
				final FadeType fadeType = (FadeType) _comboBoxFadeType.getSelectedItem();
				final boolean isIntroSong = _checkBoxIsIntroSong.isSelected();
				final boolean isLoopSong = _checkBoxIsLoopSong.isSelected();
				final double volume = _numberSliderVolume.getValue() / 100.0;
				oldData.setFile(file);
				oldData.setFadeType(fadeType);
				oldData.setIsIntroSong(isIntroSong);
				oldData.setIsLoopSong(isLoopSong);
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
