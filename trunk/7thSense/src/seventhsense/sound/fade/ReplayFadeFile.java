/*
 * ReplayFadeFile.java
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
package seventhsense.sound.fade;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import seventhsense.data.eventlist.EventList;
import seventhsense.sound.SoundFactory;
import seventhsense.sound.engine.IPlayer;
import seventhsense.sound.engine.ISoundListener;
import seventhsense.sound.engine.SoundEventType;
import seventhsense.sound.engine.SoundException;

/**
 * This class plays a sound file with fading options
 *
 */
public class ReplayFadeFile implements IPlayerFade
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(ReplayFadeFile.class.getName());
	
	/**
	 * The source-file
	 */
	private final File _file;
	
	/**
	 * The currently used fade-file
	 */
	private IPlayerFade _playFile;
	
	/**
	 * Listener list
	 */
	private final EventList<ISoundListener<IPlayer>> _listeners = new EventList<ISoundListener<IPlayer>>();
	
	/**
	 * The listener for events in the playFile
	 */
	private final ISoundListener<IPlayer> _playFileListener;
	
	/**
	 * Constructor for a fading sound file
	 * 
	 * @param file base file
	 * @throws SoundException 
	 */
	public ReplayFadeFile(final File file) throws SoundException
	{
		_file = file;
		_playFile = new SoundFadeFile(SoundFactory.createPlayer(file));
		
		_playFileListener = new ISoundListener<IPlayer>()
		{
			@Override
			public void soundEvent(final IPlayer player, final SoundEventType eventType)
			{
				fireEvent(eventType);
			}
		};
		_playFile.addSoundListener(_playFileListener);
	}
	
	@Override
	public void setVolume(final double volume)
	{
		_playFile.setVolume(volume);
	}

	@Override
	public void play() throws SoundException
	{
		LOGGER.log(Level.INFO, "play: " + _playFile.isPlaying() + ", " + _playFile.isPaused());
		if(_playFile.isPlaying() && !_playFile.isPaused())
		{
			LOGGER.log(Level.INFO, "replay start");
			final double lastVolume = _playFile.getVolume();
			final double lastFadeTime = _playFile.getFadeTime();
			_playFile.removeSoundListener(_playFileListener);
			_playFile.close();
			_playFile = new SoundFadeFile(SoundFactory.createPlayer(_file));
			_playFile.addSoundListener(_playFileListener);
			_playFile.setVolume(lastVolume);
			_playFile.setFadeTime(lastFadeTime);
		}
		_playFile.play();
	}

	@Override
	public void stop()
	{
		_playFile.stop();
	}

	@Override
	public void pause()
	{
		_playFile.pause();
	}

	@Override
	public void resume() throws SoundException
	{
		_playFile.resume();
	}

	@Override
	public boolean isPlaying()
	{
		return _playFile.isPlaying();
	}

	@Override
	public boolean isPaused()
	{
		return _playFile.isPaused();
	}
	
	@Override
	public boolean isClosed()
	{
		return _playFile.isClosed();
	}

	@Override
	public void setTime(final double time) throws SoundException
	{
		_playFile.setTime(time);
	}

	@Override
	public double getTime()
	{
		return _playFile.getTime();
	}

	@Override
	public double getDuration() throws SoundException
	{
		return _playFile.getDuration();
	}

	@Override
	public double getVolume()
	{
		return _playFile.getVolume();
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
	 * Fire a sound event
	 * @param eventType event type
	 */
	private void fireEvent(final SoundEventType eventType)
	{
		for (ISoundListener<IPlayer> listener : _listeners.iterateEvents())
		{
			listener.soundEvent(this, eventType);
		}
	}
	
	@Override
	public double getFadeTime()
	{
		return _playFile.getFadeTime();
	}

	@Override
	public void setFadeTime(final double fadeTime)
	{
		_playFile.setFadeTime(fadeTime);
	}
	
	@Override
	public void close()
	{
		_playFile.close();
		_playFile = null;
	}
}
