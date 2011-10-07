/*
 * TimeSliderTest.java
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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

public class TimeSliderTest
{
	private static final Logger LOGGER = Logger.getLogger(TimeSliderTest.class.getName());

	private JFrame _frame;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					final TimeSliderTest window = new TimeSliderTest();
					window._frame.setVisible(true);
				}
				catch (Exception e)
				{
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TimeSliderTest()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		_frame = new JFrame();
		_frame.setBounds(100, 100, 450, 300);
		_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		final TimeSlider panel = new TimeSlider();
		_frame.getContentPane().add(panel, BorderLayout.CENTER);
	}

}
