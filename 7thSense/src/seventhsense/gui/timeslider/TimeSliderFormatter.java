/*
 * TimeSliderFormatter.java
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
package seventhsense.gui.timeslider;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * Class for formatting the TimeSlider
 * 
 * @author Parallan
 *
 */
public class TimeSliderFormatter extends AbstractFormatter
{
	private static final Logger LOGGER = Logger.getLogger(TimeSliderFormatter.class.getName());
	
	private static final long serialVersionUID = 1L;
	private final Pattern _parseRegexpMin;
	private final Pattern _parseRegexpSec;
	private final Pattern _parseRegexp;
	private final NumberFormat _numberFormat;
    
	/**
	 * Creates a new Time Formatter
	 */
	public TimeSliderFormatter()
	{
		super();
		final String baseRegex = "([0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)";
		_parseRegexpMin = Pattern.compile(baseRegex + " min", Pattern.CASE_INSENSITIVE);
		_parseRegexpSec = Pattern.compile(baseRegex + " sec", Pattern.CASE_INSENSITIVE);
		_parseRegexp = Pattern.compile(baseRegex, Pattern.CASE_INSENSITIVE);
		_numberFormat = NumberFormat.getInstance(Locale.ROOT);
	}
	
	@Override
	public Object stringToValue(final String text) throws ParseException
	{
		if("Infinite".equals(text))
		{
			return Integer.MAX_VALUE;
		}
		else
		{
			boolean oneMatchFound = false;
			final Matcher matcherMin = _parseRegexpMin.matcher(text);
			double min = 0;
			if(matcherMin.find())
			{
				final String minStr = matcherMin.group();
				try
				{
					min = _numberFormat.parse(minStr.substring(0, minStr.length() - 4)).doubleValue();
					oneMatchFound = true;
				}
				catch (ParseException e)
				{
					LOGGER.log(Level.WARNING, e.toString(), e);
				}
			}
			
			final Matcher matcherSec = _parseRegexpSec.matcher(text);
			double sec = 0;
			if(matcherSec.find())
			{
				final String secStr = matcherSec.group();
				try
				{
					sec = _numberFormat.parse(secStr.substring(0, secStr.length() - 4)).doubleValue();
					oneMatchFound = true;
				}
				catch (ParseException e)
				{
					LOGGER.log(Level.WARNING, e.toString(), e);
				}
			}
			if(!oneMatchFound)
			{
				try
				{
					sec = _numberFormat.parse(text).doubleValue();
				}
				catch (ParseException e) //NOPMD
				{
					//Nothing here
				}
			}
			
			return (int)(min * 600.0) + (int)(sec * 10.0);
		}
	}

	@Override
	public String valueToString(final Object value) throws ParseException
	{
		if((value instanceof Integer) || (value instanceof Double))
		{
			int intValue;
			if(value instanceof Double)
			{
				intValue = (int)((Double)value * 10.0);
			}
			else
			{
				intValue = (Integer)value;
			}
			if(intValue == Integer.MAX_VALUE)
			{
				return "Infinite";
			}
			if(intValue > 600)
			{
				final int min = intValue / 600;
				final int sec = intValue - min * 600;
				return String.format(Locale.ROOT, "%d min, %.1f sec", min, (double)sec / 10.0);
			}
			return String.format(Locale.ROOT, "%.1f sec", (double)intValue / 10.0);
		}
		return value.toString();
	}
	
}