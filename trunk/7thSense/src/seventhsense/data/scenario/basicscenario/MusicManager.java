/*
 * MusicManager.java
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
package seventhsense.data.scenario.basicscenario;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.data.scenario.sound.SoundException;
import seventhsense.data.scenario.sound.player.SoundEventType;

/**
 * Manager for MusicItems
 * 
 * @author Parallan, Drag-On
 *
 */
public class MusicManager extends AbstractScenarioManager<MusicItem>
{
	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_RANDOMIZED = "randomized";

	/**
	 * Default serial version for serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger for debugging purposes
	 */
	private static final Logger LOGGER = Logger.getLogger(MusicManager.class.getName());
	
	/**
	 * Flag indicating if music is currently randomized
	 */
	private boolean _isRandomized = false;

	/**
	 * Indicates the order of the music to play
	 */
	private transient List<Integer> _musicOrder;
	
	/**
	 * current playing item
	 */
	private transient MusicItem _currentItem;

	/**
	 * Listener for active sound item
	 */
	private transient ISoundItemListener<MusicItem> _activeSoundItemListener;

	/**
	 * Constructor
	 */
	public MusicManager()
	{
		super();
		initialize(); // NOPMD
	}

	/**
	 * Setter for isMusicRandomized
	 * 
	 * @param isRandomized new value
	 */
	public void setRandomized(final boolean isRandomized)
	{
		_isRandomized = isRandomized;
		if (isRandomized)
		{
			randomizeMusicOrder();
		}
		else
		{
			orderMusicOrder();
		}
		
		fireChanged(PROPERTY_RANDOMIZED);
	}

	/**
	 * Getter for isMusicRandomized
	 * 
	 * @return true, if music is randomized
	 */
	public boolean isRandomized()
	{
		return _isRandomized;
	}

	/**
	 * Returns the real (internal) index for the virtual index (the one that can be seen from the "outside")
	 * 
	 * @param virtualIndex index to translate
	 * @return real index (index in view/internal order)
	 */
	public int virtualIndexToRealIndex(final int virtualIndex)
	{
		return _musicOrder.get(virtualIndex);
	}

	/**
	 * Returns the virtual index (the one that can be seen from the "outside") for the real (internal) index
	 * 
	 * @param realIndex index to translate
	 * @return virtual index (index in play order)
	 */
	public int realIndexToVirtualIndex(final int realIndex)
	{
		return _musicOrder.indexOf(Integer.valueOf(realIndex));
	}

	/**
	 * Randomizes the order of the music to play. Enables randomizing if not enabled.
	 */
	public void randomizeMusicOrder()
	{
		_isRandomized = true;
		LOGGER.log(Level.FINE, "randomize last order: " + _musicOrder);
		Collections.shuffle(_musicOrder);
		LOGGER.log(Level.FINE, "randomize new order: " + _musicOrder);
	}

	/**
	 * Orders the order of the music to play. Disables randomizing if enabled.
	 */
	public void orderMusicOrder()
	{
		_isRandomized = false;
		for (int i = 0; i < _musicOrder.size(); i++)
		{
			_musicOrder.set(i, i);
		}
	}

	/**
	 * Loads the music item in _currentItem
	 * @return true on success
	 */
	private boolean loadMusicItem()
	{
		try
		{
			_currentItem.setMixer(_mixer);
			_currentItem.load();
		}
		catch (SoundException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
			_currentItem = null;
			return false;
		}
		_currentItem.addListener(_activeSoundItemListener);
		return true;
	}

	/**
	 * Moves to the next music and unloads the current
	 * 
	 * @param requireFirst if true, it will only take musics with isFirst, else one with isLoop
	 */
	private void nextMusic(final boolean requireFirst)
	{
		if (_soundList.isEmpty())
		{
			_currentItem = null;
		}
		else if (_soundList.size() == 1)
		{
			if (_currentItem == null)
			{
				_currentItem = _soundList.get(0);
				loadMusicItem();
			}
			else if (_currentItem != _soundList.get(0))
			{
				_currentItem.removeListener(_activeSoundItemListener);
				_currentItem.unload(_fadeTime);
				_currentItem = _soundList.get(0);
				if((_currentItem.isIntroSong() && requireFirst) || (_currentItem.isLoopSong() && !requireFirst))
				{
					LOGGER.log(Level.FINE, "found next item: " + _currentItem.getFile());
					loadMusicItem();
				}
				else
				{
					_currentItem = null;
				}
			}
		}
		else
		{
			// Free currently played music
			if (_currentItem != null)
			{
				_currentItem.removeListener(_activeSoundItemListener);
				_currentItem.unload(_fadeTime);
			}
			// Step to next song
			int nextIndex = 0;
			final int currentRealIndex = _soundList.indexOf(_currentItem);
			if (currentRealIndex != -1)
			{
				nextIndex = _musicOrder.indexOf(currentRealIndex) + 1;
			}
			// search for next available music item (search each item only once)
			for (int i = 0; i < _soundList.size(); i++)
			{
				if (nextIndex >= _soundList.size())
				{
					nextIndex = 0;
					if(_isRandomized)
					{
						//after randomizing, restart counter
						randomizeMusicOrder();
						i = 0;
					}
				}
				_currentItem = _soundList.get(_musicOrder.get(nextIndex));
				if((_currentItem.isIntroSong() && requireFirst) || (_currentItem.isLoopSong() && !requireFirst))
				{
					if (loadMusicItem())
					{
						LOGGER.log(Level.FINE, "found next item: " + _currentItem.getFile() + " " + _currentItem.isIntroSong() + " " + _currentItem.isLoopSong() + " " + requireFirst);
						break;
					}
				}
				else
				{
					_currentItem = null;
				}
				nextIndex++;
			}
		}
	}

	@Override
	protected void onItemAdded(final int index, final MusicItem item)
	{
		super.onItemAdded(index, item);
		if (_isRandomized)
		{
			final Random newPos = new Random();
			_musicOrder.add(newPos.nextInt(_musicOrder.size() + 1), _soundList.size() - 1);
		}
		else
		{
			_musicOrder.add(index, _soundList.size() - 1);
		}
	}

	@Override
	protected void onItemRemoved(final int index, final MusicItem item)
	{
		super.onItemRemoved(index, item);
		_musicOrder.remove(Integer.valueOf(index));
		if(item.equals(_currentItem))
		{
			nextMusic(false);
		}
	}
	
	/**
	 * Starts playing
	 */
	public void play()
	{
		play(true, true, true);
	}

	/**
	 * Starts music playback
	 * 
	 * @param nextMusic if false, the current item will be "play"ed (may load the first item nevertheless)
	 * @param fadeMusic if true, the next music will be faded in
	 * @param isFirst if true, it will try to play sounds with isFirst (if none was found, it will try isLoop-sounds)
	 */
	private void play(final boolean nextMusic, final boolean fadeMusic, final boolean isFirst)
	{
		if (_soundList.isEmpty())
		{
			return;
		}
		if(nextMusic || (_currentItem == null))
		{
			nextMusic(isFirst);
			if((_currentItem == null) && isFirst)
			{
				nextMusic(false);
			}
		}
		if(_currentItem != null)
		{
			_currentItem.play(fadeMusic?_fadeTime:0);
		}
	}

	/**
	 * Event: Fired when a music finished playing
	 */
	private void onActiveMusicFinished()
	{
		play(true, false, false);
	}

	/**
	 * 
	 */
	@Override
	public void stop()
	{
		if (_currentItem != null)
		{
			_currentItem.unload(_fadeTime);
			_currentItem = null;
		}
	}

	/**
	 * 
	 */
	@Override
	public void pause()
	{
		if (_currentItem != null)
		{
			_currentItem.pause();
		}
	}

	/**
	 * 
	 */
	@Override
	public void resume()
	{
		if (_currentItem != null)
		{
			_currentItem.resume();
		}
	}
	
	/**
	 * @return
	 */
	@Override
	public boolean isPlaying()
	{
		return (_currentItem != null) && (_currentItem.isPlaying());
	}
	
	/**
	 * @return
	 */
	@Override
	public boolean isPaused()
	{
		return (_currentItem != null) && (_currentItem.isPaused());
	}
	
	/**
	 * @return
	 */
	public MusicManager deepClone()
	{
		final MusicManager clone = new MusicManager();

		clone.setRandomized(_isRandomized);
		for (MusicItem musicItem : _soundList)
		{
			clone.getList().add(musicItem.deepClone());
		}
		return clone;
	}

	/**
	 * Initialized transient fields
	 */
	private void initialize()
	{
		// initialize music order
		_musicOrder = new ArrayList<Integer>(_soundList.size());
		for (int i = 0; i < _soundList.size(); i++)
		{
			_musicOrder.add(i);
		}
		if (_isRandomized)
		{
			randomizeMusicOrder();
		}

		// initialize sound listener
		_activeSoundItemListener = new ISoundItemListener<MusicItem>()
		{
			@Override
			public void soundEvent(final MusicItem item, final SoundEventType event)
			{
				if (event == SoundEventType.Finished)
				{
					onActiveMusicFinished();
				}
			}

			@Override
			public void changed(final MusicItem item, final String property)
			{
				// Not required
			}
		};
	}

	/**
	 * Read Object Method.
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		initialize();
	}
}
