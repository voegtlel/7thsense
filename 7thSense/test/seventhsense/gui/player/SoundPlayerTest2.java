/*
 * SoundPlayerTest2.java
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
package seventhsense.gui.player;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import seventhsense.data.file.FileReference;
import seventhsense.data.scenario.basicscenario.BasicScenarioNode;
import seventhsense.data.scenario.sound.FadeType;
import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.sound.engine.PlayerMixer;

/**
 * @author Drag-On
 *
 */
public class SoundPlayerTest2
{

	private static final Logger LOGGER = Logger.getLogger(SoundPlayerTest2.class.getName());
	private JFrame _frame;
	private SoundPlayer _player;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					final SoundPlayerTest2 window = new SoundPlayerTest2();
					window._frame.setVisible(true);
					window._player.setVisible(true);
				}
			});
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
	}

	/**
	 * Constructor
	 */
	public SoundPlayerTest2()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		_frame = new JFrame();
		_frame.setTitle("7th Sense Test");
		_frame.setIconImage(Toolkit.getDefaultToolkit().getImage(SoundPlayerTest2.class.getResource("/seventhsense/resources/AppIcon_32.png")));
		_frame.setBounds(100, 100, 221, 82);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		_player = new SoundPlayer();
		_frame.getContentPane().add(_player);

		final FileReference fileRef = new FileReference("test/data/Erdenstern - Sons of the desert_Remastered.mp3");
		final MusicItem item = new MusicItem(FadeType.NoFade, false, false, 100, fileRef);
		
		_player.setModel(item);
	}
}
