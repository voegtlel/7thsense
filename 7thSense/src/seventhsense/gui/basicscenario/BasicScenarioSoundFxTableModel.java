/*
 * BasicScenarioSoundFxModel.java
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

import javax.swing.table.AbstractTableModel;

import seventhsense.data.FileReference;
import seventhsense.data.listenerlist.IListItemListener;
import seventhsense.data.listenerlist.IListenerList;
import seventhsense.data.scenario.basicscenario.BasicScenarioNode;
import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.SoundFxItem;
import seventhsense.sound.engine.SoundEventType;

/**
 * Model for SoundFx items in BasicScenario
 * 
 * @author Parallan
 *
 */
public class BasicScenarioSoundFxTableModel extends AbstractBasicScenarioTableModel<SoundFxItem>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAMES = new String[] { "Name", "Replay Min Time", "Replay Max Time", "Volume (%)", "Script Name",
			"Init Script", "Finish Script" };
	private static final Class<?>[] COLUMN_CLASSES = new Class<?>[] { FileReference.class, Double.class, Double.class, Double.class, String.class,
			Boolean.class, Boolean.class };

	@Override
	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(final int column)
	{
		return COLUMN_NAMES[column];
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex)
	{
		return COLUMN_CLASSES[columnIndex];
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex)
	{
		switch (columnIndex)
		{
		case 0:
			return _data.get(rowIndex).getFile();
		case 1:
			return _data.get(rowIndex).getMinReplayWaitTime();
		case 2:
			return _data.get(rowIndex).getMaxReplayWaitTime();
		case 3:
			return _data.get(rowIndex).getVolume() * 100.0;
		case 4:
			return _data.get(rowIndex).getScriptName();
		case 5:
			return _data.get(rowIndex).getInitScript() != null;
		case 6:
			return _data.get(rowIndex).getFinishScript() != null;
		default:
			return null;
		}
	}
}
