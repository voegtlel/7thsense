/*
 * PlayerMixer.java
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

import java.io.File;
import java.util.logging.Logger;

import seventhsense.data.IPropertyChangedListener;
import seventhsense.data.eventlist.EventList;
import seventhsense.system.NativeLoader;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

/**
 * Class for the mixer used by the player
 * TODO: Make singleton
 * 
 * @author Parallan
 *
 */
public class PlayerMixer
{
	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_VOLUME = "volume";

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger(PlayerMixer.class.getName());
	
	/**
	 * Native loader for the librarys
	 */
	private static NativeLoader __nativeLoader;
	
	/**
	 * The mixer to manage
	 */
	private final AL _mixer;
	
	/**
	 * Volume of mixer
	 */
	private double _volume = 1;
	
	/**
	 * List for listeners
	 */
	private final EventList<IPropertyChangedListener<PlayerMixer>> _listeners = new EventList<IPropertyChangedListener<PlayerMixer>>();
	
	/**
	 * Creates a mixer for the player
	 */
	public PlayerMixer()
	{
		if(__nativeLoader == null)
		{
			__nativeLoader = new NativeLoader(new File("lib"));
			__nativeLoader.initializeNatives();
		}
		ALut.alutInit();
		_mixer = ALFactory.getAL();
	}
	
	/**
	 * Gets the valid al instance
	 * 
	 * @return al instance
	 */
	public AL getAl()
	{
		return _mixer;
	}
	
	/**
	 * Sets the volume for the mixer
	 * 
	 * @param volume volume
	 */
	public void setVolume(final double volume)
	{
		_volume = volume;
		fireChanged(PROPERTY_VOLUME);
	}

	/**
	 * Gets the volume for the mixer
	 * 
	 * @return volume
	 */
	public double getVolume()
	{
		return _volume;
	}
	
	/**
	 * Add a listener
	 * 
	 * @param listener listener
	 */
	public void addListener(final IPropertyChangedListener<PlayerMixer> listener)
	{
		_listeners.add(listener);
	}
	
	/**
	 * Remove a listener
	 * 
	 * @param listener listener
	 */
	public void removeListener(final IPropertyChangedListener<PlayerMixer> listener)
	{
		_listeners.remove(listener);
	}
	
	
	/**
	 * Fires the changed event.
	 * 
	 * @param property property that changed
	 */
	private void fireChanged(final String property)
	{
		for(IPropertyChangedListener<PlayerMixer> listener : _listeners.iterateEvents())
		{
			listener.propertyChanged(this, property);
		}
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		ALut.alutExit();
		super.finalize();
	}
}
