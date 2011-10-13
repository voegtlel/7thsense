/*
 * SoundFadeFile.java
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

import java.util.logging.Level;
import java.util.logging.Logger;

import seventhsense.data.eventlist.EventList;
import seventhsense.data.fx.Fx;
import seventhsense.data.fx.IFxListener;
import seventhsense.data.fx.IFxSetter;
import seventhsense.data.fx.transitions.LinearTransition;
import seventhsense.sound.engine.IPlayer;
import seventhsense.sound.engine.ISoundListener;
import seventhsense.sound.engine.SoundEventType;
import seventhsense.sound.engine.SoundException;

/**
 * This class plays a sound file with fading options
 *
 */
public class SoundFadeFile implements IPlayerFade
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(SoundFadeFile.class.getName());
	
	/**
	 * Default time for fading (play/stop/pause/resume)
	 */
	private static final double DEFAULT_FADETIME = 0.25;
	
	/**
	 * The parent file to control
	 */
	private final IPlayer _file;
	/**
	 * The fx for fading
	 */
	private final Fx _volumeFx;
	/**
	 * Stores the full/maximum volume
	 */
	private double _fullVolume;
	/**
	 * Current fade time
	 */
	private double _fadeTime = 2.0;
	
	/**
	 * State to set after fading
	 */
	private FadeState _fadeState = FadeState.None;
	
	/**
	 * Listener list
	 */
	private final EventList<ISoundListener<IPlayer>> _listeners = new EventList<ISoundListener<IPlayer>>();
	
	/**
	 * Constructor for a fading sound file
	 * 
	 * @param file base file
	 */
	public SoundFadeFile(final IPlayer file)
	{
		_file = file;
		_file.addSoundListener(new ISoundListener<IPlayer>()
		{
			@Override
			public void soundEvent(final IPlayer player, final SoundEventType eventType)
			{
				if(eventType != SoundEventType.Volume)
				{
					fireEvent(eventType);
				}
			}
		});
		_volumeFx = new Fx(50, new LinearTransition());
		_volumeFx.addFxSetter(new IFxSetter()
		{
			@Override
			public void setValue(final double value)
			{
				_file.setVolume(value);
			}
		});
		_volumeFx.addListener(new IFxListener()
		{
			@Override
			public void fxStarted()
			{
				// Not required
			}
			
			@Override
			public void fxFinished()
			{
				onVolumeFxDone();
			}
			
			@Override
			public void fxCancelled()
			{
				onVolumeFxDone();
			}
		});
		_volumeFx.setValue(0);
		_fullVolume = file.getVolume();
	}
	
	/**
	 * Event.
	 */
	private void onVolumeFxDone()
	{
		final FadeState fadeState = _fadeState;
		_fadeState = FadeState.None;
		switch(fadeState)
		{
		case FadeStopping: _file.stop(); break;
		case FadePausing: _file.pause(); break;
		case FadeClosing: _file.close(); break;
		default:
		}
		LOGGER.log(Level.FINE, "volume fx done: " + fadeState);
	}

	@Override
	public void setVolume(final double volume)
	{
		_fullVolume = volume;
		if(_file.isPlaying())
		{
			_fadeState = FadeState.FadeVolume;
			_volumeFx.start(volume, DEFAULT_FADETIME);
		}
		fireEvent(SoundEventType.Volume);
		LOGGER.log(Level.FINE, "set volume " + volume);
	}

	@Override
	public void play() throws SoundException
	{
		fireEvent(SoundEventType.Starting);
		_file.play();
		_fadeState = FadeState.FadePlaying;
		_volumeFx.start(_fullVolume, _fadeTime);
		LOGGER.log(Level.FINE, "play");
	}

	@Override
	public void stop()
	{
		fireEvent(SoundEventType.Stopping);
		_fadeState = FadeState.FadeStopping;
		_volumeFx.start(0, _fadeTime);
	}

	@Override
	public void pause()
	{
		fireEvent(SoundEventType.Pausing);
		_fadeState = FadeState.FadePausing;
		_volumeFx.start(0, DEFAULT_FADETIME);
		LOGGER.log(Level.FINE, "pause");
	}

	@Override
	public void resume() throws SoundException
	{
		fireEvent(SoundEventType.Resuming);
		_file.resume();
		_fadeState = FadeState.FadeResuming;
		_volumeFx.start(_fullVolume, DEFAULT_FADETIME);
		LOGGER.log(Level.FINE, "resume");
	}

	@Override
	public boolean isPlaying()
	{
		return _file.isPlaying();
	}

	@Override
	public boolean isPaused()
	{
		return _file.isPaused();
	}

	@Override
	public void setTime(final double time) throws SoundException
	{
		_file.setTime(time);
	}

	@Override
	public double getTime()
	{
		return _file.getTime();
	}

	@Override
	public double getDuration() throws SoundException
	{
		return _file.getDuration();
	}

	@Override
	public double getVolume()
	{
		return _fullVolume;
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
		return _fadeTime;
	}

	@Override
	public void setFadeTime(final double fadeTime)
	{
		_fadeTime = fadeTime;
	}
	
	@Override
	public void close()
	{
		fireEvent(SoundEventType.Closing);
		_fadeState = FadeState.FadeClosing;
		_volumeFx.start(0, _fadeTime);
		LOGGER.log(Level.FINE, "close");
	}
}
