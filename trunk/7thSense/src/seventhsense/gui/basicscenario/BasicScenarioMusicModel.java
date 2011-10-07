/*
 * BasicScenarioMusicModel.java
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
import seventhsense.data.scenario.sound.FadeType;
import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.MusicItem;
import seventhsense.data.scenario.sound.player.SoundEventType;

/**
 * Model the the table of music items
 * 
 * @author Parallan
 *
 */
public class BasicScenarioMusicModel extends AbstractTableModel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAMES = new String[] { "Name", "Fade Type", "Intro", "Loop", "Volume (%)" };
	private static final Class<?>[] COLUMN_CLASSES = new Class<?>[] { FileReference.class, FadeType.class, Boolean.class, Boolean.class, Double.class };

	private BasicScenarioNode _data;
	private final transient IListItemListener<MusicItem> _listener;
	private final transient ISoundItemListener<MusicItem> _musicItemListener;

	/**
	 * Creates the basic scenario music model
	 */
	public BasicScenarioMusicModel()
	{
		super();
		_listener = new IListItemListener<MusicItem>()
		{
			@Override
			public void itemRemoved(final IListenerList<MusicItem> list, final int index, final MusicItem item)
			{
				BasicScenarioMusicModel.this.listItemRemoved(index, item);
			}

			@Override
			public void itemAdded(final IListenerList<MusicItem> list, final int index, final MusicItem item)
			{
				BasicScenarioMusicModel.this.listItemAdded(index, item);
			}
		};
		_musicItemListener = new ISoundItemListener<MusicItem>()
		{
			@Override
			public void changed(final MusicItem item, final String property)
			{
				BasicScenarioMusicModel.this.basicScenarioMusicChanged(item);
			}

			@Override
			public void soundEvent(final MusicItem item, final SoundEventType event)
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
		return _data.getMusicManager().getList().size();
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex)
	{
		switch (columnIndex)
		{
		case 0:
			return _data.getMusicManager().getList().get(rowIndex).getFile();
		case 1:
			return _data.getMusicManager().getList().get(rowIndex).getFadeType();
		case 2:
			return _data.getMusicManager().getList().get(rowIndex).isIntroSong();
		case 3:
			return _data.getMusicManager().getList().get(rowIndex).isLoopSong();
		case 4:
			return _data.getMusicManager().getList().get(rowIndex).getVolume() * 100.0;
		default:
			return null;
		}
	}

	/**
	 * Event
	 * @param index index
	 * @param item item
	 */
	private void listItemAdded(final int index, final MusicItem item)
	{
		this.fireTableRowsInserted(index, index);
		item.addListener(_musicItemListener);
	}

	/**
	 * Event
	 * @param index index
	 * @param item item
	 */
	private void listItemRemoved(final int index, final MusicItem item)
	{
		this.fireTableRowsDeleted(index, index);
		item.removeListener(_musicItemListener);
	}

	/**
	 * Event
	 * @param item item
	 */
	private void basicScenarioMusicChanged(final MusicItem item)
	{
		final int index = _data.getMusicManager().getList().indexOf(item);
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
			_data.getMusicManager().getList().removeListener(_listener);
			for (MusicItem item : _data.getMusicManager().getList())
			{
				item.removeListener(_musicItemListener);
			}
		}
		_data = data;
		if (_data != null)
		{
			_data.getMusicManager().getList().addListener(_listener);
			for (MusicItem item : _data.getMusicManager().getList())
			{
				item.addListener(_musicItemListener);
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
