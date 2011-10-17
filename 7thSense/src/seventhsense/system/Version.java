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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.print.attribute.standard.DateTimeAtCompleted;

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
	
	/**
	 * Contains the program version
	 */
	private static final Version PROPERTIES_VERSION = loadVersion();

	private final String _major;
	private final String _minor;
	private final String _date;

	/**
	 * Constructor
	 * @param major Major application version
	 * @param minor Minor application version
	 * @param date Date of build
	 */
	public Version(final String major, final String minor, final String date)
	{
		_major = major;
		_minor = minor;
		_date = date;
	}
	
	/**
	 * Loads the version from the resource file
	 * 
	 * @return version stored in resource file
	 */
	private static Version loadVersion()
	{
		String major = "0";
		String minor = "0";
		Date date = new Date(0);

		final Properties prop = new Properties();
		try
		{
			final InputStream stream = Version.class.getResource("/seventhsense/resources/properties.txt").openStream();
			prop.load(stream);
			minor = prop.getProperty("minor");
			major = prop.getProperty("major");
			final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			date = dateFormat.parse(prop.getProperty("date"));
			stream.close();
		}
		catch (FileNotFoundException e)
		{
			LOGGER.log(Level.FINE, e.getMessage());
		}
		catch (IOException e)
		{
			LOGGER.log(Level.FINE, e.getMessage());
		}
		catch (ParseException e)
		{
			LOGGER.log(Level.FINE, e.getMessage());
		}
		return new Version(major, minor, new SimpleDateFormat().format(date));
	}
	
	/**
	 * Get the current version
	 * 
	 * @return version
	 */
	public static Version getVersion()
	{
		return PROPERTIES_VERSION;
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
