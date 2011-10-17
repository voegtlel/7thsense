/*
 * BasicScenarioViewTest.java
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
package seventhsense.gui.basicscenario;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.UIManager;

import seventhsense.data.file.FileReference;
import seventhsense.data.scenario.basicscenario.BasicScenarioNode;
import seventhsense.data.scenario.sound.FadeType;
import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.data.scenario.sound.SoundFxItem;
import seventhsense.sound.engine.PlayerMixer;

public class BasicScenarioViewTest
{
	private static final Logger LOGGER = Logger.getLogger(BasicScenarioViewTest.class.getName());
	
	private JFrame frame;

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
					final BasicScenarioViewTest window = new BasicScenarioViewTest();
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
			}
		});
	}
	
	public static BasicScenarioNode getTestData()
	{
		final BasicScenarioNode data = new BasicScenarioNode("Scenario");
		data.getMusicManager().setRandomized(false);
		data.getMusicManager().getList().add(new MusicItem(FadeType.NoFade, false, false, 0.5, new FileReference("C:\\temp")));
		data.getMusicManager().getList().add(new MusicItem(FadeType.NoFade, false, false, 0.0, new FileReference("C:\\temp")));
		data.getMusicManager().getList().add(new MusicItem(FadeType.NoFade, false, false, 1.0, new FileReference("C:\\temp")));
		data.getSoundFxManager().getList().add(new SoundFxItem(0, 30*60, 0.5, new FileReference("C:\\temp"), null, null, null));
		data.getSoundFxManager().getList().add(new SoundFxItem(0, 30*60, 0.0, new FileReference("C:\\temp"), null, null, null));
		data.getSoundFxManager().getList().add(new SoundFxItem(0, 30*60, 1.0, new FileReference("C:\\temp"), null, null, null));
		
		return data;
	}

	/**
	 * Create the application.
	 */
	public BasicScenarioViewTest()
	{
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
		
		final BasicScenarioView panel = new BasicScenarioView();
		panel.setModel(getTestData());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
	}

}
