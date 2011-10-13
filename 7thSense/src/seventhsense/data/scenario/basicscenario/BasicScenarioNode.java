/*
 * BasicScenarioNode.java
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
import java.util.logging.Level;
import java.util.logging.Logger;

import seventhsense.data.INode;
import seventhsense.data.listenerlist.IListItemListener;
import seventhsense.data.listenerlist.IListenerList;
import seventhsense.data.scenario.AbstractScenarioNode;
import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.data.scenario.sound.SoundFxItem;
import seventhsense.sound.engine.PlayerMixer;
import seventhsense.sound.engine.SoundEventType;
import seventhsense.sound.engine.SoundException;

/**
 * Represents a basic scenario. Leaf node.
 * 
 * @author Parallan, Drag-On
 * 
 */
public class BasicScenarioNode extends AbstractScenarioNode
{
	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_VALID = "valid";

	/**
	 * Default serial version for serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger for debugging purposes
	 */
	private static final Logger LOGGER = Logger.getLogger(BasicScenarioNode.class.getName());

	/**
	 * Default music manager
	 */
	private final MusicManager _musicManager;

	/**
	 * Default soundeffect manager
	 */
	private final SoundFxManager _soundFxManager;

	/**
	 * Is Valid
	 */
	private boolean _isValid = true;

	/**
	 * Constructor
	 * 
	 * @param name Name of this scenario
	 */
	public BasicScenarioNode(final String name)
	{
		super(name);
		_musicManager = new MusicManager();
		_soundFxManager = new SoundFxManager();
		initialize(); //NOPMD
	}

	/**
	 * Private Constructor for cloning
	 * 
	 * @param name Name of this scenario
	 * @param musicManager the musicManager to use
	 * @param soundFxManager the soundFxManger to use
	 */
	protected BasicScenarioNode(final String name, final MusicManager musicManager, final SoundFxManager soundFxManager)
	{
		super(name);
		_musicManager = musicManager;
		_soundFxManager = soundFxManager;
		initialize(); //NOPMD
	}

	/**
	 * Returns the music manager
	 * @return the music manager
	 */
	public MusicManager getMusicManager()
	{
		return _musicManager;
	}

	/**
	 * Returns the music manager
	 * @return the soundfx manager
	 */
	public SoundFxManager getSoundFxManager()
	{
		return _soundFxManager;
	}

	@Override
	public void play()
	{
		_musicManager.play();
		_soundFxManager.play();
	}

	@Override
	public void stop()
	{
		_musicManager.stop();
		_soundFxManager.stop();
	}

	@Override
	public void pause()
	{
		_musicManager.pause();
		_soundFxManager.pause();
	}

	@Override
	public void resume()
	{
		_musicManager.resume();
		_soundFxManager.resume();
	}

	@Override
	public boolean isPlaying()
	{
		return _musicManager.isPlaying() || _soundFxManager.isPlaying();
	}

	@Override
	public boolean isPaused()
	{
		return _musicManager.isPaused() && _soundFxManager.isPaused();
	}

	@Override
	public void setFadeTime(final double fadeTime)
	{
		_musicManager.setFadeTime(fadeTime);
		_soundFxManager.setFadeTime(fadeTime);
	}

	@Override
	public void setMixer(final PlayerMixer mixer)
	{
		_musicManager.setMixer(mixer);
		_soundFxManager.setMixer(mixer);
	}

	@Override
	public void load() throws SoundException
	{
	}

	@Override
	public void unload()
	{
	}

	@Override
	public boolean isLoaded()
	{
		return true;
	}
	
	@Override
	public double getDuration()
	{
		return 0;
	}
	
	@Override
	public double getTime()
	{
		return 0;
	}
	
	@Override
	public void setTime(final double time)
	{
		// do nothing
	}

	@Override
	public boolean isValid()
	{
		return _isValid;
	}

	@Override
	public void validate(final boolean recursive)
	{
		_musicManager.validate();
		_soundFxManager.validate();
		final boolean isValid = _musicManager.isValid() && _soundFxManager.isValid();
		if (isValid != _isValid)
		{
			fireNodeChanging(PROPERTY_VALID);
			LOGGER.log(Level.FINER, "validate " + _isValid + " -> " + isValid);
			_isValid = isValid;
			fireNodeChanged(PROPERTY_VALID);
		}
	}

	@Override
	public INode deepClone()
	{
		final BasicScenarioNode clone = new BasicScenarioNode(_name, _musicManager.deepClone(), _soundFxManager.deepClone());
		return clone;
	}

	/**
	 * Initialized transient fields
	 */
	private void initialize()
	{
		_musicManager.addItemListener(new ISoundItemListener<MusicItem>()
		{
			@Override
			public void soundEvent(final MusicItem item, final SoundEventType event)
			{
				// Not required
			}

			@Override
			public void propertyChanged(final MusicItem item, final String property)
			{
				if ("file".equals(property))
				{
					validate(false);
				}
			}
		});
		_musicManager.getList().addListener(new IListItemListener<MusicItem>()
		{

			@Override
			public void itemRemoved(final IListenerList<MusicItem> list, final int index, final MusicItem item)
			{
				validate(false);
			}

			@Override
			public void itemAdded(final IListenerList<MusicItem> list, final int index, final MusicItem item)
			{
				validate(false);
			}
		});
		_soundFxManager.addItemListener(new ISoundItemListener<SoundFxItem>()
		{
			@Override
			public void soundEvent(final SoundFxItem item, final SoundEventType event)
			{
				// Not required
			}

			@Override
			public void propertyChanged(final SoundFxItem item, final String property)
			{
				if ("file".equals(property))
				{
					validate(false);
				}
			}
		});
		_soundFxManager.getList().addListener(new IListItemListener<SoundFxItem>()
		{

			@Override
			public void itemRemoved(final IListenerList<SoundFxItem> list, final int index, final SoundFxItem item)
			{
				validate(false);
			}

			@Override
			public void itemAdded(final IListenerList<SoundFxItem> list, final int index, final SoundFxItem item)
			{
				validate(false);
			}
		});
	}

	/**
	 * Read Object Method
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
