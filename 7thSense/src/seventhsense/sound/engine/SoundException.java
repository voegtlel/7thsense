/*
 * SoundException.java
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
 * Class for exceptions when loading a sound file
 * 
 * @author Parallan, Drag-On
 *
 */
public class SoundException extends Exception
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a sound exception
	 */
	public SoundException()
	{
		super();
	}
	
	/**
	 * Creates a sound exception
	 * 
	 * @param message message
	 */
	public SoundException(final String message)
	{
		super(message);
	}
	
	/**
	 * Creates a sound exception
	 * 
	 * @param cause cause
	 */
	public SoundException(final Throwable cause)
	{
		super(cause);
	}
	
	/**
	 * Creates a sound exception
	 * 
	 * @param message message
	 * @param cause cause
	 */
	public SoundException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
