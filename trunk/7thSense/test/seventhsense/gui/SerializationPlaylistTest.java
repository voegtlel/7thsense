/*
 * SerializationPlaylistTest.java
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
package seventhsense.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import seventhsense.data.FolderNode;
import seventhsense.gui.library.LibraryViewTest;
import seventhsense.gui.playlist.PlaylistView;
import seventhsense.sound.engine.PlayerMixer;

public class SerializationPlaylistTest extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(LibraryViewTest.class.getName());

	private JFrame frame;

	private PlaylistView _panel;
	
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					final File file = new File("rootModel.dat");
					
					FolderNode rootModel = new FolderNode("root");
					if(file.exists())
					{
						final FileInputStream fis = new FileInputStream(file);
						final ObjectInputStream ois = new ObjectInputStream(fis);
						rootModel = (FolderNode) ois.readObject();
						ois.close();
						fis.close();
					}
					
					final FolderNode finalRootModel = rootModel;
					
					final SerializationPlaylistTest window = new SerializationPlaylistTest();
					window._panel.setModel(finalRootModel);
					window.frame.setVisible(true);
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
	public SerializationPlaylistTest()
	{
		super();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		_panel = new PlaylistView();
		frame.getContentPane().add(_panel, BorderLayout.CENTER);
	}
}
