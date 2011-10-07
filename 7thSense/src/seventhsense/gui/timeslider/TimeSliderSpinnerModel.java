/*
 * TimeSliderSpinnerModel.java
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

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * SpinnerModel for TimeSlider
 * 
 * @author Parallan
 *
 */
public class TimeSliderSpinnerModel extends AbstractSpinnerModel
{
	private static final Logger LOGGER = Logger.getLogger(TimeSliderSpinnerModel.class.getName());
	
	/**
	 * Time in tenth of seconds
	 */
	private int _value;
	
	private final AbstractFormatter _formatter;
    
	/**
	 * Creates a time slider spinner model
	 * 
	 * @param formatter formatter to use
	 */
	public TimeSliderSpinnerModel(final AbstractFormatter formatter)
	{
		super();
		_formatter = formatter;
	}
	
	@Override
	public Object getValue()
	{
		try
		{
			return _formatter.valueToString(_value);
		}
		catch (ParseException e)
		{
			return "0.0 sec";
		}
	}
	
	/**
	 * Returns the double value in seconds
	 * 
	 * @return value as double in seconds
	 */
	public double getDoubleValue()
	{
		return (double)_value / 10.0;
	}

	@Override
	public void setValue(final Object value)
	{
		if(value instanceof Double)
		{
			if((Double)value == Double.POSITIVE_INFINITY)
			{
				_value = Integer.MAX_VALUE;
			}
			else
			{
				_value = (int)(10.0 * (Double)value);
			}
		}
		else if(value instanceof Integer)
		{
			_value = (Integer)value;
		}
		else
		{
			try
			{
				_value = (Integer) _formatter.stringToValue(value.toString());
			}
			catch (ParseException e)
			{
				LOGGER.log(Level.WARNING, e.toString(), e);
			}
		}
		fireStateChanged();
	}

	@Override
	public Object getNextValue()
	{
		if(_value == Integer.MAX_VALUE)
		{
			return Integer.MAX_VALUE;
		}
		return _value + 10;
	}

	@Override
	public Object getPreviousValue()
	{
		if(_value == Integer.MAX_VALUE)
		{
			return Integer.MAX_VALUE;
		}
		return Math.max(_value - 10, 0);
	}
}
