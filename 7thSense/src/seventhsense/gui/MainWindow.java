/*
 * MainWindow.java
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

import javax.swing.JFrame;

import seventhsense.gui.main.MainView;
import seventhsense.sound.engine.PlayerMixer;

import java.awt.Toolkit;

/**
 * Main window used by seventhsensestartup
 * 
 * @author Parallan
 *
 */
public class MainWindow extends JFrame
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private final PlayerMixer _mixer;

	/**
	 * Create the application.
	 */
	public MainWindow()
	{
		super();
		
		_mixer = new PlayerMixer();
		
		setTitle("7th Sense");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/seventhsense/resources/AppIcon_32.png")));
		
		this.setLocationByPlatform(true);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		getContentPane().add(new MainView(_mixer));
	}

}
