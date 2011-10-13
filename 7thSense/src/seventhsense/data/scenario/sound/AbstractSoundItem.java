/*
 * AbstractSoundItem.java
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
package seventhsense.data.scenario.sound;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import seventhsense.data.FileReference;
import seventhsense.data.IItem;
import seventhsense.data.eventlist.EventList;
import seventhsense.sound.SoundFactory;
import seventhsense.sound.engine.IPlayer;
import seventhsense.sound.engine.ISoundListener;
import seventhsense.sound.engine.PlayerMixer;
import seventhsense.sound.engine.SoundEventType;
import seventhsense.sound.engine.SoundException;
import seventhsense.sound.fade.IPlayerFade;
import seventhsense.sound.fade.SoundFadeFile;

/**
 * Abstract class for sounds.
 * 
 * @author Parallan, Drag-On
 *
 * @param <E> Type for events (must be the implementing class)
 */
public abstract class AbstractSoundItem<E> implements IItem, IPlayable
{
	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_VOLUME = "volume";

	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_FILE = "file";

	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_FADE_TYPE = "fadeType";

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger(AbstractSoundItem.class.getName());

	/**
	 * Media file reference
	 */
	protected FileReference _file;

	/**
	 * Volume of this song
	 */
	protected double _volume;

	/**
	 * Indicates how to handle fadings with this song
	 */
	protected FadeType _fadeType;

	/**
	 * Event listeners
	 */
	private transient EventList<ISoundItemListener<E>> _listeners;

	/**
	 * Listener for _soundThread
	 */
	private transient ISoundListener<IPlayer> _soundListener;

	/**
	 * Thread where this sound will be updated
	 */
	protected transient IPlayerFade _soundPlayer;

	/**
	 * Thread for AfterFade
	 */
	protected transient DelayThread _afterFadeThread;

	/**
	 * Mixer
	 */
	private transient PlayerMixer _mixer;

	/**
	 * Default time for fading in
	 */
	private transient double _defaultFadeInTime = 0.0;
	
	/**
	 * Default time for fading out
	 */
	private transient double _defaultFadeOutTime = 0.25;

	/**
	 * Constructor for AbstractSoundItem
	 * 
	 * @param fadeType initial fade type
	 * @param volume initial volume
	 * @param file initial media file
	 */
	public AbstractSoundItem(final FadeType fadeType, final double volume, final FileReference file)
	{
		_fadeType = fadeType;
		_volume = volume;
		_file = file;
		initialize();
	}

	/**
	 * Gets fade type
	 * 
	 * @return the fadeType
	 */
	public FadeType getFadeType()
	{
		return _fadeType;
	}

	/**
	 * Sets fade type
	 * 
	 * @param fadeType the fadeType to set
	 */
	public void setFadeType(final FadeType fadeType)
	{
		_fadeType = fadeType;
		fireChanged(PROPERTY_FADE_TYPE);
	}

	/**
	 * Gets media file
	 * 
	 * @return the media file reference
	 */
	public FileReference getFile()
	{
		return _file;
	}

	/**
	 * Sets media file
	 * 
	 * @param file the media file reference
	 */
	public void setFile(final FileReference file)
	{
		_file = file;
		fireChanged(PROPERTY_FILE);
		if (_soundPlayer != null)
		{
			_soundPlayer.removeSoundListener(_soundListener);
			_soundPlayer.stop();
			_soundPlayer = null;
			try
			{
				load();
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	/**
	 * Gets volume
	 * 
	 * @return the volume
	 */
	public double getVolume()
	{
		return _volume;
	}

	/**
	 * Sets volume
	 * 
	 * @param volume the volume to set
	 */
	public void setVolume(final double volume)
	{
		_volume = volume;
		if (_soundPlayer != null)
		{
			_soundPlayer.setVolume(volume);
		}
		fireChanged(PROPERTY_VOLUME);
	}

	@Override
	public void load() throws SoundException
	{
		if (_soundPlayer == null)
		{
			LOGGER.log(Level.FINE, _file + " load");
			_soundPlayer = new SoundFadeFile(SoundFactory.createPlayer(new File(_file.getPath()), _mixer));
			_soundPlayer.setVolume(_volume);
			_soundPlayer.addSoundListener(_soundListener);
		}
	}
	
	/**
	 * Unloads the media file with a fadout
	 * 
	 * @param fadeTime time to fade out
	 */
	public void unload(final double fadeTime)
	{
		LOGGER.log(Level.FINE, _file + " unload");
		if (_afterFadeThread != null)
		{
			_afterFadeThread.stop();
			_afterFadeThread = null;
		}
		if (_soundPlayer != null)
		{
			_soundPlayer.setFadeTime(fadeTime);
			_soundPlayer.close();
			_soundPlayer.removeSoundListener(_soundListener);
			_soundPlayer = null;
		}
	}

	@Override
	public void unload()
	{
		unload(0.25);
	}

	@Override
	public boolean isLoaded()
	{
		return _soundPlayer != null;
	}

	@Override
	public boolean isPlaying()
	{
		return isLoaded() && _soundPlayer.isPlaying();
	}

	@Override
	public boolean isPaused()
	{
		return isLoaded() && _soundPlayer.isPaused();
	}

	/**
	 * Starts playback of this sound
	 * 
	 * @param fadeTime Time for FadeIn in sec
	 */
	public void play(final double fadeTime)
	{
		LOGGER.log(Level.FINE, _file + " play " + fadeTime);
		if (_afterFadeThread != null)
		{
			_afterFadeThread.stop();
			_afterFadeThread = null;
		}
		if (_soundPlayer != null)
		{
			if (_soundPlayer.isPlaying())
			{
				_soundPlayer.setFadeTime(_defaultFadeOutTime);
				_soundPlayer.stop();
			}
			if (_fadeType == FadeType.NoFade)
			{
				_soundPlayer.setFadeTime(0);
				try
				{
					_soundPlayer.play();
				}
				catch (SoundException e)
				{
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
			}
			else if (_fadeType == FadeType.AfterFade)
			{
				_afterFadeThread = new DelayThread(fadeTime, new Runnable()
				{
					@Override
					public void run()
					{
						_soundPlayer.setFadeTime(0);
						try
						{
							_soundPlayer.play();
						}
						catch (SoundException e)
						{
							LOGGER.log(Level.SEVERE, e.toString(), e);
						}
					}
				});
				_afterFadeThread.start();
			}
			else
			{
				_soundPlayer.setFadeTime(fadeTime);
				try
				{
					_soundPlayer.play();
				}
				catch (SoundException e)
				{
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
			}
		}
		else
		{
			throw new IllegalStateException("sound not loaded");
		}
	}

	@Override
	public void play()
	{
		play(_defaultFadeInTime);
	}

	/**
	 * Stops playback of this sound
	 * 
	 * @param fadeTime Time for FadeOut in sec
	 */
	public void stop(final double fadeTime)
	{
		LOGGER.log(Level.FINE, _file + " stop " + fadeTime);
		if (_afterFadeThread != null)
		{
			_afterFadeThread.stop();
			_afterFadeThread = null;
		}
		if (_soundPlayer != null)
		{
			_soundPlayer.setFadeTime(fadeTime);
			_soundPlayer.stop();
		}
	}

	@Override
	public void stop()
	{
		stop(_defaultFadeOutTime);
	}

	@Override
	public void pause()
	{
		if (_soundPlayer != null)
		{
			_soundPlayer.pause();
		}
	}

	@Override
	public void resume()
	{
		if (_soundPlayer != null)
		{
			try
			{
				_soundPlayer.resume();
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	@Override
	public double getTime()
	{
		if(_soundPlayer != null)
		{
			return _soundPlayer.getTime();
		}
		return 0;
	}

	@Override
	public double getDuration()
	{
		if(_soundPlayer != null)
		{
			try
			{
				return _soundPlayer.getDuration();
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
		return 0;
	}

	@Override
	public void setTime(final double time)
	{
		if(_soundPlayer != null)
		{
			try
			{
				_soundPlayer.setTime(time);
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	@Override
	public void setMixer(final PlayerMixer mixer)
	{
		_mixer = mixer;
	}

	@Override
	public void setFadeTime(final double fadeTime)
	{
		_defaultFadeInTime = fadeTime;
		//_defaultFadeOutTime = fadeTime;
	}

	/**
	 * Adds a listener
	 * 
	 * @param listener listener
	 */
	public void addListener(final ISoundItemListener<E> listener)
	{
		_listeners.add(listener);
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener listener
	 */
	public void removeListener(final ISoundItemListener<E> listener)
	{
		_listeners.remove(listener);
	}

	/**
	 * Fires the changed event
	 * @param property property that was changed
	 */
	@SuppressWarnings("unchecked")
	protected void fireChanged(final String property)
	{
		for (ISoundItemListener<E> listener : _listeners.iterateEvents())
		{
			listener.propertyChanged((E) this, property);
		}
	}

	/**
	 * Fires the sound event
	 * @param event event
	 */
	@SuppressWarnings("unchecked")
	private void fireSoundEvent(final SoundEventType event)
	{
		for (ISoundItemListener<E> listener : _listeners.iterateEvents())
		{
			listener.soundEvent((E) this, event);
		}
	}

	/**
	 * Validates the SoundItem
	 * 
	 * @return true, if the item was valid
	 */
	public boolean validate()
	{
		try
		{
			AudioSystem.getAudioFileFormat(new File(_file.getPath()));
		}
		catch (UnsupportedAudioFileException e)
		{
			return false;
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}

	/**
	 * Initialized transient fields
	 */
	private void initialize()
	{
		_soundListener = new ISoundListener<IPlayer>()
		{
			@Override
			public void soundEvent(final IPlayer player, final SoundEventType eventType)
			{
				//LOGGER.log(Level.INFO, _file + " SoundEvent " + eventType);
				fireSoundEvent(eventType);
			}
		};
		_listeners = new EventList<ISoundItemListener<E>>();
	}

	/**
	 * Function for deserialization.
	 * 
	 * @param in object for deserializing
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		initialize();
	}
}
