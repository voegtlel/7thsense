/*
 * IPlayer.java
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


/**
 * Interface for a sound-file player
 * 
 * @author Parallan, Drag-On
 *
 */
public interface IPlayer
{

	/**
	 * (re)start playing
	 * 
	 * @throws SoundException
	 */
	void play() throws SoundException;

	/**
	 * stop if playing
	 */
	void stop();

	/**
	 * pause if playing
	 */
	void pause();

	/**
	 * resume if paused
	 * 
	 * @throws SoundException 
	 */
	void resume() throws SoundException;

	/**
	 * true, if the file is playing or paused
	 * 
	 * @return playing state
	 */
	boolean isPlaying();

	/**
	 * true, if the file is paused
	 * 
	 * @return pause state
	 */
	boolean isPaused();

	/**
	 * Seek the play position in seconds
	 * 
	 * @param time time to seek to
	 * @throws SoundException
	 */
	void setTime(final double time) throws SoundException;

	/**
	 * Get current play position in seconds
	 * 
	 * @return current play time
	 */
	double getTime();

	/**
	 * Get total duration of the sound file in seconds
	 * 
	 * @return duration
	 * @throws SoundException 
	 */
	double getDuration() throws SoundException;
	
	/**
	 * Set volume of sound [0, 1]
	 * 
	 * @param volume volume
	 */
	void setVolume(double volume);

	/**
	 * returns volume of sound [0, 1]
	 * 
	 * @return volume
	 */
	double getVolume();

	/**
	 * Add a sound listener
	 * 
	 * @param listener listener
	 */
	void addSoundListener(ISoundListener<IPlayer> listener);
	
	/**
	 * Remove a listener
	 * 
	 * @param listener listener
	 */
	void removeSoundListener(ISoundListener<IPlayer> listener);
	
	/**
	 * Closes all assigned resources
	 */
	void close();
}