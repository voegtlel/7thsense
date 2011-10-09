/*
 * SoundPlayer.java
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
package seventhsense.gui.player;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import seventhsense.data.scenario.sound.DelayThread;
import seventhsense.data.scenario.sound.IPlayable;
import seventhsense.data.scenario.sound.SoundException;
import seventhsense.data.scenario.sound.player.PlayerMixer;
import seventhsense.gui.ModelView;
import seventhsense.gui.numberslider.NumberSlider;
import seventhsense.gui.timeslider.TimeSlider;
import seventhsense.gui.timeslider.TimeSliderLinear;

/**
 * This class serves as a Controller for the audio playback (some may call it a "player")
 * 
 * @author Drag-On
 *
 */
public class SoundPlayer extends ModelView<IPlayable>
{
	/**
	 * Default serial version for serializable
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(SoundPlayer.class.getName());

	private final TimeSlider _timeSliderFadeTime;

	/**
	 * Current playable item (most likely a scenario)
	 */
	private IPlayable _playingModel;

	/**
	 * Current playable item (most likely a scenario)
	 */
	private IPlayable _model;

	/**
	 * Mixer; everything connected to sound will need to pass here.
	 */
	private final PlayerMixer _mixer;
	
	/**
	 * Seeker
	 */
	private TimeSliderLinear _timeSliderSeeker;
	
	/**
	 * Used for events
	 */
	private boolean _isSeeking = false;

	private DelayThread _seekerThread;
	private NumberSlider _numberSliderVolume;

	/**
	 * Constructor
	 */
	public SoundPlayer()
	{
		super();
		_mixer = new PlayerMixer(AudioSystem.getMixer(null)); // Get the system's default mixer
		
		addAncestorListener(new AncestorListener()
		{
			@Override
			public void ancestorAdded(final AncestorEvent event)
			{
				SoundPlayer.this.onAncestorAdded();
			}

			@Override
			public void ancestorMoved(final AncestorEvent event)
			{
				// Not required
			}

			@Override
			public void ancestorRemoved(final AncestorEvent event)
			{
				SoundPlayer.this.onAncestorRemoved();
			}
		});
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 29, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final JButton buttonPlay = new JButton("");
		buttonPlay.setEnabled(false);
		buttonPlay.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				onPlayPressed();
			}
		});
		buttonPlay.setToolTipText("Start scenario playback or fade to another scenario");
		buttonPlay.setIcon(new ImageIcon(SoundPlayer.class.getResource("/seventhsense/resources/Play_20.png")));
		final GridBagConstraints gbc_buttonPlay = new GridBagConstraints();
		gbc_buttonPlay.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonPlay.anchor = GridBagConstraints.NORTH;
		gbc_buttonPlay.insets = new Insets(0, 0, 5, 5);
		gbc_buttonPlay.gridx = 1;
		gbc_buttonPlay.gridy = 0;
		add(buttonPlay, gbc_buttonPlay);

		final JButton buttonPause = new JButton("");
		buttonPause.setEnabled(false);
		buttonPause.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				onPlayPausePressed();
			}
		});
		buttonPause.setToolTipText("Pause or continue playback");
		buttonPause.setIcon(new ImageIcon(SoundPlayer.class.getResource("/seventhsense/resources/Play_Pause_20.png")));
		final GridBagConstraints gbc_buttonPause = new GridBagConstraints();
		gbc_buttonPause.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonPause.anchor = GridBagConstraints.NORTH;
		gbc_buttonPause.insets = new Insets(0, 0, 5, 5);
		gbc_buttonPause.gridx = 2;
		gbc_buttonPause.gridy = 0;
		add(buttonPause, gbc_buttonPause);

		final JButton buttonStop = new JButton("");
		buttonStop.setEnabled(false);
		buttonStop.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				onStopPressed();
			}
		});
		buttonStop.setToolTipText("Stop playback instantly");
		buttonStop.setIcon(new ImageIcon(SoundPlayer.class.getResource("/seventhsense/resources/Stop_20.png")));
		final GridBagConstraints gbc_buttonStop = new GridBagConstraints();
		gbc_buttonStop.insets = new Insets(0, 0, 5, 5);
		gbc_buttonStop.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonStop.anchor = GridBagConstraints.NORTH;
		gbc_buttonStop.gridx = 3;
		gbc_buttonStop.gridy = 0;
		add(buttonStop, gbc_buttonStop);

		final JPanel panelPlayOptions = new JPanel();
		panelPlayOptions.setEnabled(false);
		final GridBagConstraints gbc_panelPlayOptions = new GridBagConstraints();
		gbc_panelPlayOptions.insets = new Insets(0, 0, 5, 0);
		gbc_panelPlayOptions.fill = GridBagConstraints.BOTH;
		gbc_panelPlayOptions.gridwidth = 5;
		gbc_panelPlayOptions.gridx = 0;
		gbc_panelPlayOptions.gridy = 1;
		add(panelPlayOptions, gbc_panelPlayOptions);
		final GridBagLayout gbl_panelPlayOptions = new GridBagLayout();
		gbl_panelPlayOptions.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelPlayOptions.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelPlayOptions.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelPlayOptions.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelPlayOptions.setLayout(gbl_panelPlayOptions);

		final JLabel labelFadeTime = new JLabel("");
		labelFadeTime.setToolTipText("Change the fade time used for sounds that fade");
		labelFadeTime.setIcon(new ImageIcon(SoundPlayer.class.getResource("/seventhsense/resources/FadeType_20.png")));
		labelFadeTime.setEnabled(false);
		final GridBagConstraints gbc_labelFadeTime = new GridBagConstraints();
		gbc_labelFadeTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelFadeTime.insets = new Insets(0, 5, 5, 5);
		gbc_labelFadeTime.gridx = 0;
		gbc_labelFadeTime.gridy = 0;
		panelPlayOptions.add(labelFadeTime, gbc_labelFadeTime);

		_timeSliderFadeTime = new TimeSlider();
		_timeSliderFadeTime.setToolTipText("Change the fade time used for sounds that fade");
		labelFadeTime.setLabelFor(_timeSliderFadeTime);
		_timeSliderFadeTime.setEnabled(false);
		final GridBagConstraints gbc_timeSliderFadeTime = new GridBagConstraints();
		gbc_timeSliderFadeTime.insets = new Insets(0, 0, 5, 0);
		gbc_timeSliderFadeTime.fill = GridBagConstraints.BOTH;
		gbc_timeSliderFadeTime.gridx = 1;
		gbc_timeSliderFadeTime.gridy = 0;
		panelPlayOptions.add(_timeSliderFadeTime, gbc_timeSliderFadeTime);
		_timeSliderFadeTime.setMaxInf(false);
		_timeSliderFadeTime.setTimeRange(0.5, 10.0, 5.0);
		_timeSliderFadeTime.setValue(2.0);
		_timeSliderFadeTime.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				onTimeSliderFadeChanged();
			}
		});
		
		final JLabel labelVolume = new JLabel("");
		final GridBagConstraints gbc_labelVolume = new GridBagConstraints();
		gbc_labelVolume.fill = GridBagConstraints.BOTH;
		gbc_labelVolume.insets = new Insets(0, 0, 0, 5);
		gbc_labelVolume.gridx = 0;
		gbc_labelVolume.gridy = 1;
		panelPlayOptions.add(labelVolume, gbc_labelVolume);
		labelVolume.setIcon(new ImageIcon(SoundPlayer.class.getResource("/seventhsense/resources/Volume_20.png")));
		labelVolume.setToolTipText("Change the global volume.");
		labelVolume.setEnabled(false);
		
		_numberSliderVolume = new NumberSlider();
		final GridBagConstraints gbc_numberSliderVolume = new GridBagConstraints();
		gbc_numberSliderVolume.fill = GridBagConstraints.BOTH;
		gbc_numberSliderVolume.gridx = 1;
		gbc_numberSliderVolume.gridy = 1;
		panelPlayOptions.add(_numberSliderVolume, gbc_numberSliderVolume);
		_numberSliderVolume.setValue(100.0);
		_numberSliderVolume.setRange(0, 100, 5);
		_numberSliderVolume.setToolTipText("Change the fade time used for sounds that fade");
		_numberSliderVolume.setEnabled(false);
		_numberSliderVolume.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				onNumberSliderVolumeChanged();
			}
		});
		
		
		_timeSliderSeeker = new TimeSliderLinear();
		_timeSliderSeeker.setToolTipText("Play time, used to seek the sound");
		_timeSliderSeeker.setTimeRange(0, 0);
		_timeSliderSeeker.setValue(0.0);
		_timeSliderSeeker.setEnabled(false);
		_timeSliderSeeker.setVisible(false);
		_timeSliderSeeker.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				onTimeSliderSeekerChanged();
			}
		});
		final GridBagConstraints gbc__timeSliderSeek = new GridBagConstraints();
		gbc__timeSliderSeek.gridwidth = 5;
		gbc__timeSliderSeek.fill = GridBagConstraints.BOTH;
		gbc__timeSliderSeek.gridx = 0;
		gbc__timeSliderSeek.gridy = 2;
		add(_timeSliderSeeker, gbc__timeSliderSeek);
	}

	/**
	 * Event.
	 */
	protected void onAncestorAdded()
	{
		_seekerThread = new DelayThread(0.1, new Runnable()
		{
			@Override
			public void run()
			{
				onUpdateSeeker();
			}
		});
		_seekerThread.setLoopAction(true);
		_seekerThread.start();
	}

	/**
	 * Event
	 */
	private void onAncestorRemoved()
	{
		if(_playingModel != null)
		{
			_playingModel.setFadeTime(0.25);
		}
		stop();
		_seekerThread.stop();
		_seekerThread = null;
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
	 * Set data model
	 * 
	 * @param model Data model to use
	 */
	@Override
	public void setModel(final IPlayable model)
	{
		_model = model;
		setEnabled(model != null);
	}

	/**
	 * Gets the model
	 * @return the model
	 */
	public IPlayable getModel()
	{
		return _model;
	}

	/**
	 * Start playing a specified model
	 * 
	 * @param model model
	 */
	public void play(final IPlayable model)
	{
		if ((_playingModel != null) && (_playingModel != model))
		{
			_playingModel.stop();
			_playingModel.unload();
			_playingModel.setMixer(null);
		}
		_playingModel = model;
		_playingModel.setMixer(_mixer);
		try
		{
			if (!_playingModel.isLoaded())
			{
				_playingModel.load();
			}
		}
		catch (SoundException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		if (_playingModel.isLoaded())
		{
			LOGGER.log(Level.FINE, "duration: " + _playingModel.getDuration());
			if(_playingModel.getDuration() > 0)
			{
				_timeSliderSeeker.setVisible(true);
			}
			else
			{
				_timeSliderSeeker.setVisible(false);
			}
			_playingModel.setFadeTime(_timeSliderFadeTime.getValue());
			_playingModel.play();
		}
		else
		{
			_timeSliderSeeker.setVisible(false);
		}
	}

	/**
	 * plays a specified model
	 */
	public void play()
	{
		play(_model);
	}

	/**
	 * Stop playing
	 */
	public void stop()
	{
		if(_playingModel != null)
		{
			_playingModel.stop();
		}
	}

	/**
	 * Pause
	 */
	public void pause()
	{
		if(_playingModel != null)
		{
			_playingModel.pause();
		}
	}

	/**
	 * Resume
	 */
	public void resume()
	{
		if(_playingModel != null)
		{
			_playingModel.resume();
		}
	}

	/**
	 * Toggle pause state
	 */
	public void pauseResume()
	{
		if(_playingModel != null)
		{
			if (_playingModel.isPaused())
			{
				resume();
			}
			else
			{
				pause();
			}
		}
	}
	
	/**
	 * Event.
	 */
	private void onNumberSliderVolumeChanged()
	{
		_mixer.setVolume(_numberSliderVolume.getValue() / 100.0);
	}

	/**
	 * Event.
	 */
	private void onTimeSliderFadeChanged()
	{
		if (_model != null)
		{
			_model.setFadeTime(_timeSliderFadeTime.getValue());
		}
		if ((_playingModel != null) && (_playingModel != _model))
		{
			_playingModel.setFadeTime(_timeSliderFadeTime.getValue());
		}
	}
	
	/**
	 * Event.
	 */
	private void onTimeSliderSeekerChanged()
	{
		if(!_isSeeking)
		{
			_isSeeking = true;
			if(!_timeSliderSeeker.isAdjusting())
			{
				if((_playingModel != null) && (_playingModel.getDuration() > 0))
				{
					_playingModel.setTime(_timeSliderSeeker.getValue());
				}
			}
			_isSeeking = false;
		}
	}
	
	/**
	 * Event.
	 */
	private void onUpdateSeeker()
	{
		if(!_isSeeking)
		{
			_isSeeking = true;
			if((_playingModel != null) && (_playingModel.getDuration() > 0))
			{
				_timeSliderSeeker.setTimeRange(0, _playingModel.getDuration());
				_timeSliderSeeker.setValue(_playingModel.getTime());
			}
			_isSeeking = false;
		}
	}

	/**
	 * Event
	 */
	private void onStopPressed()
	{
		stop();
	}

	/**
	 * Event
	 */
	private void onPlayPausePressed()
	{
		pauseResume();
	}

	/**
	 * Event
	 */
	private void onPlayPressed()
	{
		play();
	}
}
