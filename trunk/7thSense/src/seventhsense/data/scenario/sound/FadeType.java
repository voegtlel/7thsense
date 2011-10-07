/*
 * FadeType.java
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

/**
 * Fade type
 * 
 * @author Parallan, Drag-On
 *
 */
public enum FadeType
{
	/**
	 * Fading is disabled, sound will start immediately
	 */
	NoFade("No Fade", "The music will start immediatly and will not fade"),
	/**
	 * Sound will fade by its volume
	 */
	VolumeFade ("Volume Fade", "The volume of the music will fade in"),
	/**
	 * Sound will start after fading has finished
	 */
	AfterFade ("After Fade", "The music will start immediately after general fading");
	
	/**
	 * Contains a human-readable name
	 */
	private String _name;
	/**
	 * Contains a human-readable description
	 */
	private String _description;
	
	/**
	 * Internal Constructor
	 * 
	 * @param name name
	 * @param description description
	 */
	private FadeType(final String name, final String description)
	{
		_name = name;
		_description = description;
	}
	
	/**
	 * Gets a description for the FadeType
	 * 
	 * @return description
	 */
	public String getDescription()
	{
		return _description;
	}
	
	/**
	 * Gets a name for the FadeType
	 * 
	 * @return name
	 */
	public String getName()
	{
		return _name;
	}
	
	@Override
	public String toString()
	{
		return _name;
	}
}
