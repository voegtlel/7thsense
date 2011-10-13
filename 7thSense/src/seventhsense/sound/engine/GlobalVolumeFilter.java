/*
 * GlobalAudioFilter.java
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

import java.util.logging.Level;
import java.util.logging.Logger;

import seventhsense.data.IPropertyChangedListener;
import seventhsense.data.eventlist.EventList;

/**
 * @author Parallan
 *
 */
public class GlobalVolumeFilter implements IPlayer
{
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger(GlobalVolumeFilter.class.getName());
	
	/**
	 * The parent mixer
	 */
	private final PlayerMixer _mixer;
	
	/**
	 * The data line to manage
	 */
	private final IPlayer _player;
	
	/**
	 * Listener for the mixer volume
	 */
	private final IPropertyChangedListener<PlayerMixer> _mixerListener;
	
	/**
	 * Listener for the mixer volume
	 */
	private final ISoundListener<IPlayer> _playerListener;
	
	/**
	 * List for listeners
	 */
	private final EventList<ISoundListener<IPlayer>> _listeners = new EventList<ISoundListener<IPlayer>>();
	
	/**
	 * Internal Volume
	 */
	private double _volume;
	
	/**
	 * Create a source data line
	 * 
	 * @param mixer the controlling mixer
	 * @param sourceDataLine the managed line
	 */
	public GlobalVolumeFilter(final PlayerMixer mixer, final IPlayer player)
	{
		_mixer = mixer;
		_player = player;
		_volume = player.getVolume();
		
		_mixerListener = new IPropertyChangedListener<PlayerMixer>()
		{
			@Override
			public void propertyChanged(final PlayerMixer caller, final String property)
			{
				onMixerPropertyChanged(property);
			}
		};
		
		_playerListener = new ISoundListener<IPlayer>()
		{
			@Override
			public void soundEvent(final IPlayer player, final SoundEventType eventType)
			{
				if(eventType != SoundEventType.Volume)
				{
					fireSoundEvent(eventType);
				}
			}
		};
		
		_mixer.addListener(_mixerListener);
		_player.addSoundListener(_playerListener);
	}
	
	/**
	 * Event.
	 * 
	 * @param property property
	 */
	private void onMixerPropertyChanged(final String property)
	{
		if(PlayerMixer.PROPERTY_VOLUME.equals(property))
		{
			//update volume
			setVolume(getVolume());
		}
	}

	@Override
	public void setVolume(final double volume)
	{
		_volume = Math.min(Math.max(0, volume), 1);
		final double finalVolume = _volume * _mixer.getVolume();
		LOGGER.log(Level.FINEST, "set volume to " + (finalVolume*100) + "%");
		_player.setVolume(finalVolume);
		fireSoundEvent(SoundEventType.Volume);
	}
	
	@Override
	public double getVolume()
	{
		return _volume;
	}

	@Override
	public void play() throws SoundException
	{
		_player.play();
	}

	@Override
	public void stop()
	{
		_player.stop();
	}

	@Override
	public void pause()
	{
		_player.pause();
	}

	@Override
	public void resume() throws SoundException
	{
		_player.resume();
	}

	@Override
	public boolean isPlaying()
	{
		return _player.isPlaying();
	}

	@Override
	public boolean isPaused()
	{
		return _player.isPaused();
	}

	@Override
	public void setTime(final double time) throws SoundException
	{
		_player.setTime(time);
	}

	@Override
	public double getTime()
	{
		return _player.getTime();
	}

	@Override
	public double getDuration() throws SoundException
	{
		return _player.getDuration();
	}

	@Override
	public void close()
	{
		_player.close();
		_mixer.removeListener(_mixerListener);
		_player.removeSoundListener(_playerListener);
	}

	@Override
	public void addSoundListener(final ISoundListener<IPlayer> listener)
	{
		_listeners.add(listener);
	}

	@Override
	public void removeSoundListener(final ISoundListener<IPlayer> listener)
	{
		_listeners.remove(listener);
	}
	
	private void fireSoundEvent(final SoundEventType eventType)
	{
		for(ISoundListener<IPlayer> listener : _listeners.iterateEvents())
		{
			listener.soundEvent(this, eventType);
		}
	}
}
