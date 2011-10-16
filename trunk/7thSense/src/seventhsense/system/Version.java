/*
 * Version.java
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
package seventhsense.system;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Version
 * @author Drag-On
 *
 */
public class Version
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(Version.class.getName());

	private final int _major;
	private final int _minor;
	private final String _date;

	/**
	 * Constructor
	 * @param major Major application version
	 * @param minor Minor application version
	 * @param date Date of build
	 */
	public Version(final int major, final int minor, final String date)
	{
		_major = major;
		_minor = minor;
		_date = date;
	}

	public static Version fromFile()
	{
		int major = 0, minor = 0;
		String date = "";

		final Properties prop = new Properties();
		try
		{
			final FileInputStream s = new FileInputStream("Properties.txt");
			prop.load(s);
			minor = Integer.parseInt(prop.getProperty("minor"));
			major = Integer.parseInt(prop.getProperty("major"));
			date = prop.getProperty("date");
			s.close();
		}
		catch (FileNotFoundException e)
		{
			LOGGER.log(Level.FINE, e.getMessage());
		}
		catch (IOException e)
		{
			LOGGER.log(Level.FINE, e.getMessage());
		}
		
		return new Version(major, minor, date);
	}

	/**
	 * Returns a string with all the version numbers
	 * @return String with the whole version
	 */
	public String toString()
	{
		return _major + "." + _minor + " | " + _date;
	}
}
