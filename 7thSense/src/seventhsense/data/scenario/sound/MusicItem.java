/*
 * MusicItem.java
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

import seventhsense.data.FileReference;

/**
 * Represents a single music of a scenario with all of it's properties
 * 
 * @author Drag-On, Parallan
 * 
 */
public class MusicItem extends AbstractSoundItem<MusicItem>
{
	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_LOOP_SONG = "isLoopSong";

	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_INTRO_SONG = "isIntroSong";

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * If this is an intro song it will never be played afterwards again
	 */
	private boolean _isIntroSong;

	/**
	 * Indicates if this song can be played after the intro song (possibly
	 * multiple times)
	 */
	private boolean _isLoopSong;

	/**
	 * Constructor
	 * 
	 * @param fadeType How to fade this music
	 * @param isIntroSong Indicates if this is an intro song
	 * @param isLoopSong Indicates if this music can be played after the intro song
	 * @param volume Volume for this song
	 * @param file Media file reference
	 */
	public MusicItem(final FadeType fadeType, final boolean isIntroSong, final boolean isLoopSong, final double volume, final FileReference file)
	{
		super(fadeType, volume, file);
		_isIntroSong = isIntroSong;
		_isLoopSong = isLoopSong;
	}

	/**
	 * Default constructor
	 */
	public MusicItem()
	{
		this(FadeType.VolumeFade, true, true, 1.0, new FileReference(""));
	}
	
	/**
	 * Returns if it is an intro song
	 * 
	 * @return the isIntroType
	 */
	public boolean isIntroSong()
	{
		return _isIntroSong;
	}

	/**
	 * Sets if it is an intro song and might be played as first song
	 * 
	 * @param isIntroSong the isIntroSong to set
	 */
	public void setIsIntroSong(final boolean isIntroSong)
	{
		_isIntroSong = isIntroSong;
		fireChanged(PROPERTY_INTRO_SONG);
	}

	/**
	 * Returns if it is a loop song and might be played after the first song
	 * 
	 * @return the isLoopSong
	 */
	public boolean isLoopSong()
	{
		return _isLoopSong;
	}

	/**
	 * Sets if it is a loop song
	 * 
	 * @param isLoopSong the isLoopSong to set
	 */
	public void setIsLoopSong(final boolean isLoopSong)
	{
		_isLoopSong = isLoopSong;
		fireChanged(PROPERTY_LOOP_SONG);
	}

	@Override
	public String toString()
	{
		return _file.toString();
	}
	
	/**
	 * Creates a clone of this object
	 * 
	 * @return cloned instance
	 */
	@Override
	public MusicItem deepClone()
	{
		return new MusicItem(_fadeType, _isIntroSong, _isLoopSong, _volume, _file);
	}
}
