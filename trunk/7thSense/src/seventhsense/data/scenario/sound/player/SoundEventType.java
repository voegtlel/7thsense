/*
 * SoundEventType.java
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

/**
 * Defines the type of sound event
 * 
 * @author Drag-On, Parallan
 *
 */
public enum SoundEventType
{
	/**
	 * Player is currently starting (only fired when fading)
	 */
	Starting,
	/**
	 * Player has started (fired after fading or when no fading is performed)
	 */
	Started,
	/**
	 * Player is currently stopping (only fired when fading)
	 */
	Stopping,
	/**
	 * Player has stopped (fired after fading or when no fading is performed)
	 */
	Stopped,
	/**
	 * Player is currently pausing (only fired when fading)
	 */
	Pausing,
	/**
	 * Player has paused (fired after fading or when no fading is performed)
	 */
	Paused,
	/**
	 * Player is currently resuming (only fired when fading)
	 */
	Resuming,
	/**
	 * Player has resumed (fired after fading or when no fading is performed)
	 */
	Resumed,
	/**
	 * Player has seeked
	 */
	Seeked,
	/**
	 * Volume has changed
	 */
	Volume,
	/**
	 * Sound file has played to its end
	 */
	Finished
}
