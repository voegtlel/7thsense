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
import seventhsense.data.scenario.sound.player.SoundEventType;

/**
 * Model for SoundFx items in BasicScenario
 * 
 * @author Parallan
 *
 */
public class BasicScenarioSoundFxModel extends AbstractTableModel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAMES = new String[] { "Name", "Replay Min Time", "Replay Max Time", "Volume (%)", "Script Name",
			"Init Script", "Finish Script" };
	private static final Class<?>[] COLUMN_CLASSES = new Class<?>[] { FileReference.class, Double.class, Double.class, Double.class, String.class,
			Boolean.class, Boolean.class };

	private BasicScenarioNode _data;
	private final transient IListItemListener<SoundFxItem> _listener;
	private final transient ISoundItemListener<SoundFxItem> _soundFxItemListener;

	/**
	 * Creates the model for sound fx items
	 */
	public BasicScenarioSoundFxModel()
	{
		super();
		_listener = new IListItemListener<SoundFxItem>()
		{
			@Override
			public void itemRemoved(final IListenerList<SoundFxItem> list, final int index, final SoundFxItem item)
			{
				BasicScenarioSoundFxModel.this.listItemRemoved(index, item);
			}

			@Override
			public void itemAdded(final IListenerList<SoundFxItem> list, final int index, final SoundFxItem item)
			{
				BasicScenarioSoundFxModel.this.listItemAdded(index, item);
			}
		};
		_soundFxItemListener = new ISoundItemListener<SoundFxItem>()
		{
			@Override
			public void changed(final SoundFxItem item, final String property)
			{
				BasicScenarioSoundFxModel.this.basicScenarioSoundFxChanged(item);
			}

			@Override
			public void soundEvent(final SoundFxItem soundFxItem, final SoundEventType event)
			{
				// TODO Auto-generated method stub

			}
		};
	}

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
	public int getRowCount()
	{
		if (_data == null)
		{
			return 0;
		}
		return _data.getSoundFxManager().getList().size();
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex)
	{
		switch (columnIndex)
		{
		case 0:
			return _data.getSoundFxManager().getList().get(rowIndex).getFile();
		case 1:
			return _data.getSoundFxManager().getList().get(rowIndex).getMinReplayWaitTime();
		case 2:
			return _data.getSoundFxManager().getList().get(rowIndex).getMaxReplayWaitTime();
		case 3:
			return _data.getSoundFxManager().getList().get(rowIndex).getVolume() * 100.0;
		case 4:
			return _data.getSoundFxManager().getList().get(rowIndex).getScriptName();
		case 5:
			return _data.getSoundFxManager().getList().get(rowIndex).getInitScript() != null;
		case 6:
			return _data.getSoundFxManager().getList().get(rowIndex).getFinishScript() != null;
		default:
			return null;
		}
	}

	/**
	 * Event
	 * @param index index
	 * @param item item
	 */
	private void listItemAdded(final int index, final SoundFxItem item)
	{
		this.fireTableRowsInserted(index, index);
		item.addListener(_soundFxItemListener);
	}

	/**
	 * Event
	 * @param index index
	 * @param item item
	 */
	private void listItemRemoved(final int index, final SoundFxItem item)
	{
		this.fireTableRowsDeleted(index, index);
		item.removeListener(_soundFxItemListener);
	}

	/**
	 * Event
	 * @param item item
	 */
	private void basicScenarioSoundFxChanged(final SoundFxItem item)
	{
		final int index = _data.getSoundFxManager().getList().indexOf(item);
		this.fireTableRowsUpdated(index, index);
	}

	/**
	 * Sets the model
	 * 
	 * @param data model
	 */
	public void setModel(final BasicScenarioNode data)
	{
		if (_data != null)
		{
			_data.getSoundFxManager().getList().removeListener(_listener);
			for (SoundFxItem item : _data.getSoundFxManager().getList())
			{
				item.removeListener(_soundFxItemListener);
			}
		}
		_data = data;
		if (_data != null)
		{
			_data.getSoundFxManager().getList().addListener(_listener);
			for (SoundFxItem item : _data.getSoundFxManager().getList())
			{
				item.addListener(_soundFxItemListener);
			}
		}
		this.fireTableDataChanged();
	}

	/**
	 * returns the model
	 * 
	 * @return model
	 */
	public BasicScenarioNode getModel()
	{
		return _data;
	}
}
