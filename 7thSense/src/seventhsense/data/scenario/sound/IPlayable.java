/*
 * IPlayable.java
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

import javax.sound.sampled.Mixer;

/**
 * This interface has been designed to match all the requirements that modern playable items need to be played back. But the best thing is: It's only 30$!
 * @author Drag-On, Parallan
 *
 */
public interface IPlayable
{
	/**
	 * Loads the playable to be ready to play
	 * 
	 * @throws SoundException exception on loading
	 */
	void load() throws SoundException;
	
	/**
	 * Unloads the playable and frees all resources
	 */
	void unload();
	
	/**
	 * Starts playing
	 */
	void play();

	/**
	 * Stops playing
	 */
	void stop();

	/**
	 * Pauses playing
	 */
	void pause();

	/**
	 * Resumes playing
	 */
	void resume();

	/**
	 * Gets play state
	 * 
	 * @return play state
	 */
	boolean isPlaying();
	
	/**
	 * Gets pause state
	 * 
	 * @return pause state
	 */
	boolean isPaused();
	
	/**
	 * Gets loaded state
	 * 
	 * @return loaded state
	 */
	boolean isLoaded();
	
	/**
	 * Sets the mixer
	 * 
	 * @param mixer mixer to use for playing
	 */
	void setMixer(Mixer mixer);

	/**
	 * Sets the fade time
	 * 
	 * @param fadeTime fade Time
	 */
	void setFadeTime(double fadeTime);
	
	/**
	 * Gets the play-time (if <= 0 none might be able)
	 * 
	 * @return play time
	 */
	double getTime();
	
	/**
	 * Gets the duration (if <= 0 none might be able)
	 * 
	 * @return duration of sound
	 */
	double getDuration();
	
	/**
	 * Seeks the play time
	 * 
	 * @param time new time to seek to
	 */
	void setTime(double time);
}
