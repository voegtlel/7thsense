/*
 * SeventhSenseStartup.java
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
package seventhsense;

import java.awt.SplashScreen;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import seventhsense.gui.MainWindow;
import seventhsense.gui.logging.LoggerFrame;

/**
 * Main start class.
 *
 * @author Parallan, Drag-On
 */
public class SeventhSenseStartup
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(SeventhSenseStartup.class.getName());

	/**
	 * Creates the main window with a little delay to show the splash screen ;)
	 * 
	 * @return created main window
	 */
	public MainWindow createMainWindow()
	{
		final SplashScreen splash = SplashScreen.getSplashScreen();
		MainWindow mainWindow = null;
        if (splash == null)
        {
        	LOGGER.log(Level.SEVERE, "can't create splash screen");
        	try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
        	mainWindow = new MainWindow();
        	mainWindow.setVisible(true);
        }
        else
        {
        	try
        	{
	            Thread.sleep(250);
	            
	            mainWindow = new MainWindow();
	            
	            Thread.sleep(750);
		        
		        splash.close();
		        
		        mainWindow.setVisible(true);
        	}
        	catch(InterruptedException e)
        	{
        		LOGGER.log(Level.SEVERE, e.toString(), e);
        	}
        }
        return mainWindow;
	}
	
	/**
	 * "Parses" the arguments
	 * 
	 * @param args arguments to parse
	 * @return parsed arguments in a map
	 */
	private static Map<String, List<String>> getArguments(final String[] args)
	{
		final Map<String, List<String>> argumentMap = new HashMap<String, List<String>>();
		List<String> currentParameters = new ArrayList<String>();
		for(String arg : args)
		{
			if(arg.charAt(0) == '-')
			{
				currentParameters = new ArrayList<String>();
				argumentMap.put(arg.substring(1), currentParameters);
			}
			else
			{
				currentParameters.add(arg);
			}
		}
		return argumentMap;
	}

	
	/**
	 * Main Method for starting the program.
	 * 
	 * @param args Arguments to the program
	 * @author Parallan, Drag-On
	 */
	public static void main(final String[] args)
	{
		final Map<String, List<String>> arguments = getArguments(args);
		
		if(arguments.containsKey("log-fine"))
		{
			final List<String> debugArgs = arguments.get("log-fine");
			for(String arg : debugArgs)
			{
				Logger.getLogger(arg).setLevel(Level.FINE);
			}
		}
		
		if(arguments.containsKey("log-finest"))
		{
			final List<String> debugArgs = arguments.get("log-finest");
			for(String arg : debugArgs)
			{
				Logger.getLogger(arg).setLevel(Level.FINEST);
			}
		}
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		
		LoggerFrame logger = null;
		if(arguments.containsKey("logger"))
		{
			logger = new LoggerFrame();
		}
		final LoggerFrame finalLogger = logger;
		final SeventhSenseStartup starter = new SeventhSenseStartup();
		final MainWindow mainWindow = starter.createMainWindow();
		mainWindow.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(final WindowEvent e)
			{
				if(finalLogger != null)
				{
					finalLogger.dispose();
				}
			}
		});
		if(finalLogger != null)
		{
			finalLogger.setVisible(true);
		}
	}

}
