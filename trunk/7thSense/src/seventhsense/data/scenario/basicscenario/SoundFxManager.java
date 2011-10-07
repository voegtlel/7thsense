/*
 * SoundFxManager.java
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.Mixer;

import seventhsense.data.listenerlist.IListItemListener;
import seventhsense.data.listenerlist.IListenerList;
import seventhsense.data.listenerlist.ListenerArrayList;
import seventhsense.data.scenario.sound.DelayThread;
import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.data.scenario.sound.SoundException;
import seventhsense.data.scenario.sound.SoundFxItem;
import seventhsense.data.scenario.sound.player.SoundEventType;

/**
 * This class handles playback of sound effects
 * 
 * @author Drag-On, Parallan
 *
 */
public class SoundFxManager extends AbstractScenarioManager<SoundFxItem>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger for debugging purposes
	 */
	private static final Logger LOGGER = Logger.getLogger(SoundFxManager.class.getName());

	/**
	 * If delay is above this time, the item will be unloaded and loaded again for replay
	 */
	private static final double UNLOAD_THRESHOLD = 2.0;

	/**
	 * Time used for fading when play is pressed (and no fadeTime was set)
	 */
	private static final double PLAY_FADETIME = 0.0;

	/**
	 * Time used for stopping
	 */
	private static final double STOP_FADETIME = 0.25;

	/**
	 * Contains the delay-threads for the soundFxList
	 */
	private transient List<DelayThread> _replayThreads;

	/**
	 * thread-lock object
	 */
	private transient Object _replayThreadLock;

	/**
	 * Current playing state 
	 */
	private transient boolean _playing;

	/**
	 * Current paused state
	 */
	private transient boolean _paused;

	/**
	 * Constructor
	 */
	public SoundFxManager()
	{
		super();
		initialize();
	}

	/**
	 * Starts an item with a random generated waitTime
	 * 
	 * @param item to play
	 * @throws SoundException 
	 */
	private void playItem(final SoundFxItem item) throws SoundException
	{
		synchronized (_replayThreadLock)
		{
			final int itemIndex = _soundList.indexOf(item);
			final DelayThread replayThread = _replayThreads.get(itemIndex);
			if (replayThread != null)
			{
				replayThread.stop();
			}
			final Random timeGen = new Random();
			final double waitTime = item.getMinReplayWaitTime() + timeGen.nextDouble() * (item.getMaxReplayWaitTime() - item.getMinReplayWaitTime());
			if (waitTime > UNLOAD_THRESHOLD)
			{
				item.unload(STOP_FADETIME);
			}
			else if (!item.isLoaded())
			{
				item.setMixer(_mixer);
				item.load();
			}
			_replayThreads.set(itemIndex, new DelayThread(waitTime, new Runnable()
			{
				@Override
				public void run()
				{
					SoundFxManager.this.startPlay(item);
				}
			}));
			_replayThreads.get(itemIndex).start();
		}
	}

	/**
	 * Starts playing an item now
	 * 
	 * @param item item to play
	 */
	private void startPlay(final SoundFxItem item)
	{
		synchronized (_replayThreadLock)
		{
			final int itemIndex = _soundList.indexOf(item);
			final DelayThread replayThread = _replayThreads.get(itemIndex);
			if (replayThread != null)
			{
				replayThread.stop();
				_replayThreads.set(itemIndex, null);
			}
			if (!item.isLoaded())
			{
				try
				{
					item.setMixer(_mixer);
					item.load();
				}
				catch (SoundException e)
				{
					LOGGER.log(Level.SEVERE, e.toString(), e);
					throw new IllegalStateException("can't open soundFx", e);
				}
			}
			if (item.getInitScript() != null)
			{
				item.getInitScript().run(this);
			}
			item.play(PLAY_FADETIME);
		}
	}

	/**
	 * Pauses an item
	 * 
	 * @param item item to pause
	 * @param index if not -1: real index of the item (performance) 
	 */
	private void pauseItem(final SoundFxItem item, final int index)
	{
		int finalIndex = index;
		if (finalIndex < 0)
		{
			finalIndex = _soundList.indexOf(item);
		}
		final DelayThread replayThread = _replayThreads.get(finalIndex);
		if (replayThread == null)
		{
			if (item.isLoaded())
			{
				item.pause();
			}
		}
		else
		{
			replayThread.pause();
		}
	}

	/**
	 * Resumes an item
	 * 
	 * @param item item to resume
	 * @param index if not -1: real index of the item (performance)
	 */
	private void resumeItem(final SoundFxItem item, final int index)
	{
		int finalIndex = index;
		if (finalIndex < 0)
		{
			finalIndex = _soundList.indexOf(item);
		}
		final DelayThread replayThread = _replayThreads.get(finalIndex);
		if (replayThread == null)
		{
			if (item.isLoaded())
			{
				item.resume();
			}
		}
		else
		{
			replayThread.resume();
		}
	}

	/**
	 * Resumes an item
	 * 
	 * @param item item to stop
	 * @param index if not -1: real index of the item (performance)
	 */
	private void stopItem(final SoundFxItem item, final int index)
	{
		int finalIndex = index;
		if (finalIndex < 0)
		{
			finalIndex = _soundList.indexOf(item);
		}
		final DelayThread replayThread = _replayThreads.get(finalIndex);
		if (replayThread == null)
		{
			if (item.isLoaded())
			{
				item.stop(STOP_FADETIME);
			}
		}
		else
		{
			replayThread.stop();
		}
	}

	@Override
	protected void onItemRemoved(final int index, final SoundFxItem item)
	{
		super.onItemRemoved(index, item);
		_replayThreads.remove(index);
		if (item.isPlaying())
		{
			item.stop(STOP_FADETIME);
		}
	}

	@Override
	protected void onItemAdded(final int index, final SoundFxItem item)
	{
		super.onItemAdded(index, item);
		_replayThreads.add(index, null);
		if (_playing)
		{
			try
			{
				playItem(item);
				if (_paused)
				{
					pauseItem(item, index);
				}
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, "Can't start soundfx", e);
			}
		}
	}

	@Override
	protected void onSoundEvent(final SoundFxItem item, final SoundEventType event)
	{
		super.onSoundEvent(item, event);
		if ((event == SoundEventType.Finished) && _playing)
		{
			try
			{
				playItem(item);
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, "Can't replay soundfx", e);
			}
		}
	}

	@Override
	protected void onSoundChanged(final SoundFxItem item, final String property)
	{
		if (_playing)
		{
			try
			{
				if ("file".equals(property))
				{
					playItem(item);
				}
				else if ("minReplayWaitTime".equals(property))
				{
					playItem(item);
				}
				else if ("maxReplayWaitTime".equals(property))
				{
					playItem(item);
				}
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
			if (_paused)
			{
				pauseItem(item, -1);
			}
		}
	}

	@Override
	public boolean isPlaying()
	{
		return _playing;
	}

	@Override
	public boolean isPaused()
	{
		return _paused;
	}

	@Override
	public void play()
	{
		_playing = true;
		_paused = false;
		for (SoundFxItem soundItem : _soundList)
		{
			try
			{
				playItem(soundItem);
			}
			catch (SoundException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	@Override
	public void stop()
	{
		_playing = false;
		_paused = false;
		for (int i = 0; i < _soundList.size(); i++)
		{
			stopItem(_soundList.get(i), i);
		}
	}

	@Override
	public void pause()
	{
		if (_playing)
		{
			_paused = true;
			for (int i = 0; i < _soundList.size(); i++)
			{
				pauseItem(_soundList.get(i), i);
			}
		}
	}

	@Override
	public void resume()
	{
		if (_playing)
		{
			_paused = false;
			for (int i = 0; i < _soundList.size(); i++)
			{
				resumeItem(_soundList.get(i), i);
			}
		}
	}

	@Override
	public SoundFxManager deepClone()
	{
		final SoundFxManager clone = new SoundFxManager();

		for (SoundFxItem soundFxItem : _soundList)
		{
			clone.getList().add(soundFxItem.deepClone());
		}
		return clone;
	}

	/**
	 * Initialized transient fields
	 */
	private void initialize()
	{
		_replayThreadLock = new Object();

		// initialize delay threads
		_replayThreads = new ArrayList<DelayThread>(_soundList.size());
		for (int i = 0; i < _soundList.size(); i++)
		{
			_replayThreads.add(null);
		}
	}

	/**
	 * Read Method.
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
