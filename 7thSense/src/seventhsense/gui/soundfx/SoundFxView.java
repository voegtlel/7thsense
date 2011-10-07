/*
 * SoundFxView.java
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
package seventhsense.gui.soundfx;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import seventhsense.data.scenario.sound.SoundFxItem;

/**
 * Panel for editing SoundFx items
 * 
 * @author Parallan
 *
 */
public class SoundFxView extends JPanel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private final SoundFxBasicPanel _basicPanel;
	private final SoundFxScriptingPanel _scriptingPanel;

	/**
	 * Create the panel.
	 */
	public SoundFxView()
	{
		super();
		setLayout(new BorderLayout(0, 0));

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		add(tabbedPane);

		_basicPanel = new SoundFxBasicPanel();
		tabbedPane.addTab("Basic", null, _basicPanel, "Basic properties for the sound effect");

		_scriptingPanel = new SoundFxScriptingPanel();
		tabbedPane.addTab("Scripting", null, _scriptingPanel, "Advanced properties for the sound effect for scripting javascript");
	}

	/**
	 * Sets the displayed data. The given model won't be modified
	 * 
	 * @param data source data for displaying
	 */
	public void setModel(final SoundFxItem data)
	{
		_basicPanel.setModel(data);
		_scriptingPanel.setModel(data);
	}
}
