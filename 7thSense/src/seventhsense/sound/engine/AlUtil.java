/*
 * AlUtil.java
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALConstants;

/**
 * @author Parallan
 *
 */
public final class AlUtil
{
	/**
	 * Can't instantiate
	 */
	private AlUtil()
	{
	}
	
	/**
	 * Returns the constant names of the given exception number
	 * 
	 * @param exception al exception number to search
	 * @return exception constant names
	 */
	private static String getExceptionFieldNames(final int exception)
	{
		final StringBuilder fieldNames = new StringBuilder();
		final int modifiers = Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;
		for(Field declaredField : ALConstants.class.getDeclaredFields())
		{
			try
			{
				if(((declaredField.getModifiers() & modifiers) == modifiers)
						&& int.class.equals(declaredField.getType())
						&& (declaredField.getInt(null) == exception))
				{
					fieldNames.append(", ");
					fieldNames.append(declaredField.getName());
				}
			}
			catch(IllegalAccessException e)
			{
				//Ignore exceptions here
			}
		}
		if(fieldNames.length() > 2)
		{
			return fieldNames.substring(2);
		}
		return "<no exception constant>";
	}
	
	/**
	 * Function for checking AL for an error
	 * 
	 * @param al AL to check for error
	 */
	public static void checkError(final AL al)
	{
		final int alError = al.alGetError();
		if (alError != AL.AL_NO_ERROR)
		{
			throw new IllegalStateException("OpenAl Error: " + alError + " " + getExceptionFieldNames(alError));
		}
	}
}
