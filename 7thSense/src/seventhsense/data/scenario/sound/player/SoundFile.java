/*
 * SoundFile.java
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
package seventhsense.data.scenario.sound.player;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import seventhsense.data.eventlist.EventList;
import seventhsense.data.fx.ITransitionReversible;
import seventhsense.data.fx.transitions.ExpTransition;
import seventhsense.data.scenario.sound.SoundException;

/**
 * Manager for SoundThread. Automatically creates threads as needed.
 * 
 * @author Parallan
 */
public class SoundFile implements IPlayer
{
	/**
	 * Exponent for volume calculation from linear scala
	 */
	private final static double VOLUME_EXPONENT = 10.0;
	/**
	 * Factor for volume calculation from linear scala
	 */
	private final static double VOLUME_MUL = 20.0;
	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(SoundFile.class.getName());
	
	/**
	 * The file to play
	 */
	private final File _file;
	
	/**
	 * The audio format of the source file
	 */
	private final AudioFileFormat _fileFormat;
	
	/**
	 * The destination line for audio data
	 */
	private final PlayerMixerLine _line;
	
	/**
	 * The thread for playing
	 */
	private SoundThread _currentThread;
	
	/**
	 * Listener for sound thread
	 */
	private final ISoundListener<SoundThread> _soundThreadListener;
	
	/**
	 * Listeners
	 */
	private final EventList<ISoundListener<IPlayer>> _listeners = new EventList<ISoundListener<IPlayer>>();
	
	/**
	 * Volume sounds better with an additional exponential scala
	 */
	private final ITransitionReversible _volumeFactor = new ExpTransition(10);
	
	/**
	 * Creates a thread manager for a file
	 * 
	 * @param file file to play
	 * @param fileFormat format of the sound file
	 * @param line destination line for audio data
	 */
	public SoundFile(final File file, final AudioFileFormat fileFormat, final PlayerMixerLine line)
	{
		_file = file;
		_fileFormat = fileFormat;
		_line = line;
		_soundThreadListener = new ISoundListener<SoundThread>()
		{
			@Override
			public void soundEvent(final SoundThread thread, final SoundEventType eventType)
			{
				fireEvent(eventType);
			}
		};
		LOGGER.log(Level.FINE, "Created SoundFile");
	}
	
	/**
	 * (re)creates the play thread if needed
	 * 
	 * @throws SoundException
	 */
	private void createThread() throws SoundException
	{
		if(isPlaying())
		{
			_currentThread.removeSoundListener(_soundThreadListener);
			_currentThread.stop();
			_currentThread = null;
			LOGGER.log(Level.FINE, "destroy thread");
		}
		if((_currentThread == null) || _currentThread.isClosed())
		{
			if(_currentThread != null)
			{
				_currentThread.removeSoundListener(_soundThreadListener);
			}
			_currentThread = new SoundThread(_file, _fileFormat, _line);
			_currentThread.addSoundListener(_soundThreadListener);
			LOGGER.log(Level.FINE, "created thread");
		}
	}
	
	@Override
	public void play() throws SoundException
	{
		createThread();
		_currentThread.play();
		LOGGER.log(Level.FINE, "play");
	}
	
	@Override
	public void stop()
	{
		if(isPlaying())
		{
			_currentThread.stop();
			LOGGER.log(Level.FINE, "stop");
		}
	}
	
	@Override
	public void pause()
	{
		if(isPlaying())
		{
			_currentThread.pause();
		}
	}
	
	@Override
	public void resume()
	{
		if(isPlaying())
		{
			_currentThread.resume();
		}
	}
	
	@Override
	public boolean isPlaying()
	{
		return (_currentThread != null) && (!_currentThread.isClosed()) && (_currentThread.isPlaying());
	}
	
	@Override
	public boolean isPaused()
	{
		return (_currentThread != null) && (!_currentThread.isClosed()) && (_currentThread.isPaused());
	}
	
	@Override
	public void setTime(final double time) throws SoundException
	{
		final boolean wasPlaying = isPlaying();
		final boolean wasPaused = isPaused();
		createThread();
		_currentThread.setTime(time);
		if(wasPlaying)
		{
			_currentThread.play();
		}
		if(wasPaused)
		{
			_currentThread.pause();
		}
	}
	
	@Override
	public double getTime()
	{
		if(isPlaying())
		{
			return _currentThread.getTime();
		}
		return 0;
	}
	
	@Override
	public double getDuration() throws SoundException
	{
		if(_currentThread == null)
		{
			createThread();
		}
		return _currentThread.getDuration();
	}
	
	@Override
	public double getVolume()
	{
		return _line.getVolume();
	}
	
	@Override
	public void setVolume(final double volume)
	{
		_line.setVolume(volume);
	}
	
	@Override
	public void removeSoundListener(final ISoundListener<IPlayer> listener)
	{
		_listeners.remove(listener);
	}

	@Override
	public void addSoundListener(final ISoundListener<IPlayer> listener)
	{
		_listeners.add(listener);
	}

	/**
	 * fires a sound event
	 * 
	 * @param eventType event type
	 */
	private void fireEvent(final SoundEventType eventType)
	{
		for (ISoundListener<IPlayer> listener : _listeners.iterateEvents())
		{
			listener.soundEvent(this, eventType);
		}
	}
}
