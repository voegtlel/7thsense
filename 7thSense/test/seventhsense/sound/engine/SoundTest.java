/*
 * SoundTest3.java
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
package seventhsense.sound.engine;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import seventhsense.gui.file.MediaFilePanel;
import seventhsense.gui.numberslider.NumberSlider;
import seventhsense.gui.timeslider.TimeSliderLinear;
import seventhsense.sound.engine.input.JavaSoundDecoderStream;
import seventhsense.system.NativeLoader;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

/**
 * @author Parallan
 *
 */
public class SoundTest extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(SoundTest.class.getName());

	private final TimeSliderLinear _timeSliderLinearSeek;
	private final MediaFilePanel _mediaFilePanel;
	private final NumberSlider _numberSliderVolume;
	private boolean _timeSliderLinearSeekIsSetting = false;

	private final AL _al;

	private AudioThread _audioThread;

	/**
	 * 
	 */
	public SoundTest()
	{
		super();
		ALut.alutInit();
		_al = ALFactory.getAL();

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(final WindowEvent e)
			{
				onWindowClosing();
			}
		});

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		final JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				onPlay();
			}
		});

		_mediaFilePanel = new MediaFilePanel();
		_mediaFilePanel.addChangeListener(new ChangeListener()
		{
			public void stateChanged(final ChangeEvent e)
			{
				onFileChanged();
			}
		});
		final GridBagConstraints gbc_mediaFilePanel = new GridBagConstraints();
		gbc_mediaFilePanel.insets = new Insets(0, 0, 5, 0);
		gbc_mediaFilePanel.gridwidth = 6;
		gbc_mediaFilePanel.fill = GridBagConstraints.BOTH;
		gbc_mediaFilePanel.gridx = 0;
		gbc_mediaFilePanel.gridy = 0;
		getContentPane().add(_mediaFilePanel, gbc_mediaFilePanel);
		final GridBagConstraints gbc_btnPlay = new GridBagConstraints();
		gbc_btnPlay.fill = GridBagConstraints.BOTH;
		gbc_btnPlay.insets = new Insets(0, 0, 5, 5);
		gbc_btnPlay.gridx = 1;
		gbc_btnPlay.gridy = 1;
		getContentPane().add(btnPlay, gbc_btnPlay);

		final JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				onPause();
			}
		});
		final GridBagConstraints gbc_btnPause = new GridBagConstraints();
		gbc_btnPause.fill = GridBagConstraints.BOTH;
		gbc_btnPause.insets = new Insets(0, 0, 5, 5);
		gbc_btnPause.gridx = 2;
		gbc_btnPause.gridy = 1;
		getContentPane().add(btnPause, gbc_btnPause);

		final JButton btnResume = new JButton("Resume");
		btnResume.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				onResume();
			}
		});
		final GridBagConstraints gbc_btnResume = new GridBagConstraints();
		gbc_btnResume.fill = GridBagConstraints.BOTH;
		gbc_btnResume.insets = new Insets(0, 0, 5, 5);
		gbc_btnResume.gridx = 3;
		gbc_btnResume.gridy = 1;
		getContentPane().add(btnResume, gbc_btnResume);

		final JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				onStop();
			}
		});
		final GridBagConstraints gbc_btnStop = new GridBagConstraints();
		gbc_btnStop.fill = GridBagConstraints.BOTH;
		gbc_btnStop.insets = new Insets(0, 0, 5, 5);
		gbc_btnStop.gridx = 4;
		gbc_btnStop.gridy = 1;
		getContentPane().add(btnStop, gbc_btnStop);

		_timeSliderLinearSeek = new TimeSliderLinear();
		_timeSliderLinearSeek.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				onSeek();
			}
		});
		_timeSliderLinearSeek.setValue(0);
		_timeSliderLinearSeek.setTimeRange(0, 0);
		final GridBagConstraints gbc__timeSlider = new GridBagConstraints();
		gbc__timeSlider.insets = new Insets(0, 0, 5, 0);
		gbc__timeSlider.gridwidth = 6;
		gbc__timeSlider.fill = GridBagConstraints.BOTH;
		gbc__timeSlider.gridx = 0;
		gbc__timeSlider.gridy = 2;
		getContentPane().add(_timeSliderLinearSeek, gbc__timeSlider);
		
		_numberSliderVolume = new NumberSlider();
		_numberSliderVolume.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				onVolumeChanged();
			}
		});
		_numberSliderVolume.setRange(0, 100, 10);
		final GridBagConstraints gbc_numberSliderVolume = new GridBagConstraints();
		gbc_numberSliderVolume.gridwidth = 6;
		gbc_numberSliderVolume.insets = new Insets(0, 0, 5, 5);
		gbc_numberSliderVolume.fill = GridBagConstraints.BOTH;
		gbc_numberSliderVolume.gridx = 0;
		gbc_numberSliderVolume.gridy = 3;
		getContentPane().add(_numberSliderVolume, gbc_numberSliderVolume);

		pack();
		
		this.setVisible(true);
	}

	/**
	 * 
	 */
	protected void onVolumeChanged()
	{
		if(_audioThread != null)
		{
			_audioThread.setVolume(_numberSliderVolume.getValue() / 100.0);
		}
	}

	/**
	 * 
	 */
	protected void onSeek()
	{
		if(!_timeSliderLinearSeekIsSetting && !_timeSliderLinearSeek.isAdjusting() && (_audioThread != null))
		{
			try
			{
				_audioThread.setTime(_timeSliderLinearSeek.getValue());
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	/**
	 * 
	 */
	protected void onFileChanged()
	{
		try
		{
			if (_audioThread != null)
			{
				_audioThread.close();
			}
			_audioThread = null;
			_audioThread = new AudioThread(new AudioBuffer(_al, new JavaSoundDecoderStream(new File(_mediaFilePanel.getValue().getPath()))));
			_audioThread.addSoundListener(new ISoundListener<IPlayer>()
			{
				@Override
				public void soundEvent(final IPlayer player, final SoundEventType eventType)
				{
					if(eventType == SoundEventType.Update)
					{
						onAudioThreadUpdate();
					}
				}
			});
			_timeSliderLinearSeek.setTimeRange(0, _audioThread.getDuration());
		}
		catch (SoundException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

	/**
	 * 
	 */
	protected void onAudioThreadUpdate()
	{
		_timeSliderLinearSeekIsSetting = true;
		_timeSliderLinearSeek.setValue(_audioThread.getTime());
		_timeSliderLinearSeekIsSetting = false;
	}

	/**
	 * 
	 */
	protected void onStop()
	{
		if (_audioThread != null)
		{
			_audioThread.stop();
		}
	}

	/**
	 * 
	 */
	protected void onResume()
	{
		if (_audioThread != null)
		{
			try
			{
				_audioThread.resume();
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	/**
	 * 
	 */
	protected void onPause()
	{
		if (_audioThread != null)
		{
			_audioThread.pause();
		}
	}

	/**
	 * 
	 */
	protected void onPlay()
	{
		if (_audioThread != null)
		{
			try
			{
				_audioThread.play();
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	/**
	 * 
	 */
	protected void onWindowClosing()
	{
		if (_audioThread != null)
		{
			_audioThread.close();
			_audioThread = null;
		}
		this.dispose();
	}

	@Override
	protected void finalize() throws Throwable
	{
		ALut.alutExit();
		super.finalize();
	}

	public static void main(final String[] args)
	{
		final NativeLoader loader = new NativeLoader(new File("lib"));
		loader.initializeNatives();
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (InstantiationException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (IllegalAccessException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (UnsupportedLookAndFeelException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		new SoundTest();
	}
}
