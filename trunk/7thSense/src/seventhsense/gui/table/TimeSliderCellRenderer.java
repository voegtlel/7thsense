/*
 * TimeSliderCellRenderer.java
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
package seventhsense.gui.table;

import java.awt.Component;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import seventhsense.gui.timeslider.TimeSliderFormatter;

/**
 * Cell renderer for time slider
 * 
 * @author Parallan
 *
 */
public class TimeSliderCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(TimeSliderCellRenderer.class.getName());
	
	private final TimeSliderFormatter _timeFormatter;
	
	/**
	 * Creates the time slider renderer
	 */
	public TimeSliderCellRenderer()
	{
		super();
		_timeFormatter = new TimeSliderFormatter();
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
	{
		String textValue = value.toString();
		try
		{
			textValue = _timeFormatter.valueToString(value);
		}
		catch (ParseException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		return super.getTableCellRendererComponent(table, textValue, isSelected, hasFocus, row, column);
	}
}
