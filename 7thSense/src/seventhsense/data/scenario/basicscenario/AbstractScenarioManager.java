/*
 * AbstractScenarioManager.java
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
import java.util.logging.Logger;

import javax.sound.sampled.Mixer;

import seventhsense.data.IPropertyChangedListener;
import seventhsense.data.eventlist.EventList;
import seventhsense.data.listenerlist.IListItemListener;
import seventhsense.data.listenerlist.IListenerList;
import seventhsense.data.listenerlist.ListenerArrayList;
import seventhsense.data.scenario.sound.AbstractSoundItem;
import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.player.PlayerMixer;
import seventhsense.data.scenario.sound.player.SoundEventType;

/**
 * Abstract class for scenario managers. Contains common methods.
 * 
 * @author Parallan, Drag-On
 *
 * @param <E> Type of Item
 */
public abstract class AbstractScenarioManager<E extends AbstractSoundItem<E>> implements Serializable
{
	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_VALID = "valid";
	
	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_FADE_TIME = "fadeTime";

	/**
	 * Default serial version for serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger for debugging purposes
	 */
	private static final Logger LOGGER = Logger.getLogger(AbstractScenarioManager.class.getName());

	/**
	 * Array for background music of this scenario
	 */
	protected final IListenerList<E> _soundList = new ListenerArrayList<E>();

	/**
	 * Caches the valid-state
	 */
	private boolean _isValid = false;
	
	/**
	 * Listeners for all music item events
	 */
	protected transient EventList<ISoundItemListener<E>> _itemListeners;

	/**
	 * Time used for fading in/out
	 */
	protected transient double _fadeTime;

	/**
	 * Listener for sound items
	 */
	private transient ISoundItemListener<E> _soundItemListener;

	/**
	 * Mixer used for playing
	 */
	protected transient PlayerMixer _mixer;

	/**
	 * Listener list for events in a scenario manager
	 */
	private transient EventList<IPropertyChangedListener<AbstractScenarioManager<E>>> _listeners;

	/**
	 * Constructor
	 */
	public AbstractScenarioManager()
	{
		initialize(); // NOPMD
	}

	/**
	 * List event.
	 * 
	 * @param index index
	 * @param item item
	 */
	protected void onItemAdded(final int index, final E item)
	{
		item.addListener(_soundItemListener);
	}

	/**
	 * List event.
	 * 
	 * @param index index
	 * @param item item
	 */
	protected void onItemRemoved(final int index, final E item)
	{
		item.removeListener(_soundItemListener);
	}

	/**
	 * returns the list for MusicItems
	 * 
	 * @return list for MusicItems
	 */
	public IListenerList<E> getList()
	{
		return _soundList;
	}

	/**
	 * set the time used for fading
	 * @param fadeTime fade time
	 */
	public void setFadeTime(final double fadeTime)
	{
		_fadeTime = fadeTime;
		fireChanged(PROPERTY_FADE_TIME);
	}

	/**
	 * get the time used for fading
	 * @return fade time
	 */
	public double getFadeTime()
	{
		return _fadeTime;
	}

	/**
	 * Event.
	 * 
	 * @param item item
	 * @param event event
	 */
	protected void onSoundEvent(final E item, final SoundEventType event)
	{
		fireItemSoundEvent(item, event);
	}
	
	/**
	 * Event.
	 * 
	 * @param item item
	 * @param property property
	 */
	protected void onSoundChanged(final E item, final String property)
	{
		fireItemChanged(item, property);
	}
	
	/**
	 * starts playing
	 */
	public abstract void play();

	/**
	 * stops playing
	 */
	public abstract void stop();

	/**
	 * pauses playing
	 */
	public abstract void pause();

	/**
	 * resumes playing
	 */
	public abstract void resume();

	/**
	 * Get playing state
	 * @return playing state
	 */
	public abstract boolean isPlaying();

	/**
	 * Get paused state
	 * @return paused state
	 */
	public abstract boolean isPaused();

	/**
	 * Sets the mixer
	 * 
	 * @param mixer new mixer
	 */
	public void setMixer(final PlayerMixer mixer)
	{
		_mixer = mixer;
	}

	/**
	 * Returns the cached check-result
	 * 
	 * @return check result
	 */
	public boolean isValid()
	{
		return _isValid;
	}

	/**
	 * Checks all sub items for validity
	 */
	public void validate()
	{
		final boolean wasValid = _isValid;
		_isValid = true;
		for (AbstractSoundItem<E> item : _soundList)
		{
			if (!item.validate())
			{
				_isValid = false;
				if(wasValid)
				{
					fireChanged(PROPERTY_VALID);
				}
				break;
			}
		}
		if(!wasValid)
		{
			fireChanged(PROPERTY_VALID);
		}
	}

	/**
	 * Add an item listener, that listens to all items
	 * 
	 * @param listener listener
	 */
	public void addItemListener(final ISoundItemListener<E> listener)
	{
		_itemListeners.add(listener);
	}

	/**
	 * Remove an item listener
	 * 
	 * @param listener listener
	 */
	public void removeItemListener(final ISoundItemListener<E> listener)
	{
		_itemListeners.remove(listener);
	}
	
	/**
	 * Add an item listener, that listens to all items
	 * 
	 * @param listener listener
	 */
	public void addListener(final IPropertyChangedListener<AbstractScenarioManager<E>> listener)
	{
		_listeners.add(listener);
	}

	/**
	 * Remove an item listener
	 * 
	 * @param listener listener
	 */
	public void removeListener(final IPropertyChangedListener<AbstractScenarioManager<E>> listener)
	{
		_listeners.remove(listener);
	}

	/**
	 * Fires item changed event.
	 * 
	 * @param item item
	 * @param property property
	 */
	private void fireItemChanged(final E item, final String property)
	{
		for (ISoundItemListener<E> listener : _itemListeners.iterateEvents())
		{
			listener.propertyChanged(item, property);
		}
	}

	/**
	 * Fires item sound event.
	 * 
	 * @param item item
	 * @param event event
	 */
	private void fireItemSoundEvent(final E item, final SoundEventType event)
	{
		for (ISoundItemListener<E> listener : _itemListeners.iterateEvents())
		{
			listener.soundEvent(item, event);
		}
	}
	
	/**
	 * Fires changed event.
	 * 
	 * @param property property
	 */
	protected void fireChanged(final String property)
	{
		for (IPropertyChangedListener<AbstractScenarioManager<E>> listener : _listeners.iterateEvents())
		{
			listener.propertyChanged(this, property);
		}
	}

	/**
	 * Creates a deep clone of the object, that is independent from this references
	 * 
	 * @return clone of this
	 */
	public abstract AbstractScenarioManager<E> deepClone();

	/**
	 * Initialized transient fields
	 */
	private void initialize()
	{
		// initialize listener lists
		_itemListeners = new EventList<ISoundItemListener<E>>();
		_listeners = new EventList<IPropertyChangedListener<AbstractScenarioManager<E>>>();

		// initialize sound listener
		_soundItemListener = new ISoundItemListener<E>()
		{
			@Override
			public void soundEvent(final E item, final SoundEventType event)
			{
				onSoundEvent(item, event);
			}

			@Override
			public void propertyChanged(final E item, final String property)
			{
				onSoundChanged(item, property);
			}
		};

		// initialize music list
		_soundList.addListener(new IListItemListener<E>()
		{
			@Override
			public void itemRemoved(final IListenerList<E> list, final int index, final E item)
			{
				AbstractScenarioManager.this.onItemRemoved(index, item);
			}

			@Override
			public void itemAdded(final IListenerList<E> list, final int index, final E item)
			{
				AbstractScenarioManager.this.onItemAdded(index, item);
			}
		});
		
		// initialize events
		for (E soundItem : _soundList)
		{
			soundItem.addListener(_soundItemListener);
		}
	}

	/**
	 * Read-method
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
