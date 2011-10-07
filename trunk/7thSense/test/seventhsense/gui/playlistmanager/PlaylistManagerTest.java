/*
 * PlaylistManagerTest.java
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
package seventhsense.gui.playlistmanager;

import java.awt.BorderLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import seventhsense.data.FolderNode;
import seventhsense.data.scenario.basicscenario.BasicScenarioNode;

/**
 * @author Drag-On
 *
 */
public class PlaylistManagerTest
{

	private static final Logger LOGGER = Logger.getLogger(PlaylistManagerTest.class.getName());

	private JFrame frame;

	/**
	 * Main entry
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final PlaylistManagerTest window = new PlaylistManagerTest();
		window.frame.setVisible(true);
	}
	
	/**
	 * Constructor
	 */
	public PlaylistManagerTest()
	{
		initialize();
	}
	
	/**
	 * Initialize
	 */
	private void initialize()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.log(Level.FINE, e.getMessage());
		}
		catch (InstantiationException e)
		{
			LOGGER.log(Level.FINE, e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			LOGGER.log(Level.FINE, e.getMessage());
		}
		catch (UnsupportedLookAndFeelException e)
		{
			LOGGER.log(Level.FINE, e.getMessage());
		}
		
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final FolderNode library = new FolderNode("root");
		final FolderNode playlist = new FolderNode("root");
		
		final FolderNode folder = new FolderNode("First Folder");
		folder.addNode(0, new BasicScenarioNode("First Scenario"));
		folder.addNode(1, new BasicScenarioNode("Second Scenario"));
		library.addNode(0, folder);
		final FolderNode folder2 = new FolderNode("Second Folder");
		folder2.addNode(0, new BasicScenarioNode("Third Scenario"));
		folder2.addNode(1, new BasicScenarioNode("Fourth Scenario"));
		library.addNode(1, folder2);
		
		final PlaylistManagerView panel = new PlaylistManagerView();
		final FolderNode commonRoot = new FolderNode("superroot");
		commonRoot.addNode(-1, library);
		commonRoot.addNode(-1, playlist);
		panel.setModelLibrary(library);
		panel.setModelPlaylist(playlist);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
	}

}
