/*
 * BasicScenarioMusicPanel.java
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

import javax.swing.ImageIcon;

import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.gui.table.TableHeaderIconCellRenderer;

/**
 * Panel with a table of music items
 * 
 * @author Parallan
 *
 */
public class BasicScenarioMusicTablePanel extends AbstractBasicScenarioTablePanel<MusicItem>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the basic scenario music panel
	 */
	public BasicScenarioMusicTablePanel()
	{
		super(new BasicScenarioMusicTableModel(), new BasicScenarioMusicTableTransferHandler());
		
		_table.getColumnModel().getColumn(0).setHeaderRenderer(new TableHeaderIconCellRenderer(
				_table.getTableHeader().getDefaultRenderer(),
				new ImageIcon(BasicScenarioMusicTablePanel.class.getResource("/seventhsense/resources/Name_BG_20.png"))));
		_table.getColumnModel().getColumn(1).setHeaderRenderer(new TableHeaderIconCellRenderer(
				_table.getTableHeader().getDefaultRenderer(),
				new ImageIcon(BasicScenarioMusicTablePanel.class.getResource("/seventhsense/resources/FadeType_BG_20.png"))));
		_table.getColumnModel().getColumn(2).setHeaderRenderer(new TableHeaderIconCellRenderer(
				_table.getTableHeader().getDefaultRenderer(),
				new ImageIcon(BasicScenarioMusicTablePanel.class.getResource("/seventhsense/resources/Intro_BG_20.png"))));
		_table.getColumnModel().getColumn(3).setHeaderRenderer(new TableHeaderIconCellRenderer(
				_table.getTableHeader().getDefaultRenderer(),
				new ImageIcon(BasicScenarioMusicTablePanel.class.getResource("/seventhsense/resources/Loop_BG_20.png"))));
		_table.getColumnModel().getColumn(4).setHeaderRenderer(new TableHeaderIconCellRenderer(
				_table.getTableHeader().getDefaultRenderer(),
				new ImageIcon(BasicScenarioMusicTablePanel.class.getResource("/seventhsense/resources/Volume_BG_20.png"))));
	}

	@Override
	protected void onAddAction()
	{
		final MusicItem newItem = new MusicItem();
		_tableModel.getModel().add(newItem);
		setSelectedItem(newItem);
	}

	@Override
	protected void onDeleteAction()
	{
		final MusicItem selectedItem = getSelectedItem();
		if (selectedItem != null)
		{
			_tableModel.getModel().remove(selectedItem);
		}
	}
}
