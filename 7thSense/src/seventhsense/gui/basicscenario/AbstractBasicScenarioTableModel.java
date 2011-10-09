/*
 * AbstractBasicScenarioTableModel.java
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

import seventhsense.data.listenerlist.IListItemListener;
import seventhsense.data.listenerlist.IListenerList;
import seventhsense.data.scenario.sound.AbstractSoundItem;
import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.player.SoundEventType;

/**
 * @author Parallan
 *
 */
public abstract class AbstractBasicScenarioTableModel<E extends AbstractSoundItem<E>> extends AbstractTableModel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The data, that is visualized by this class
	 */
	protected IListenerList<E> _data;
	
	/**
	 * Listener for data changes
	 */
	protected final transient IListItemListener<E> _listener;
	
	/**
	 * Listener for data item changes
	 */
	protected final transient ISoundItemListener<E> _soundItemListener;

	/**
	 * 
	 */
	public AbstractBasicScenarioTableModel()
	{
		super();
		
		_listener = new IListItemListener<E>()
		{
			@Override
			public void itemRemoved(final IListenerList<E> list, final int index, final E item)
			{
				AbstractBasicScenarioTableModel.this.listItemRemoved(index, item);
			}

			@Override
			public void itemAdded(final IListenerList<E> list, final int index, final E item)
			{
				AbstractBasicScenarioTableModel.this.listItemAdded(index, item);
			}
		};
		_soundItemListener = new ISoundItemListener<E>()
		{
			@Override
			public void changed(final E item, final String property)
			{
				AbstractBasicScenarioTableModel.this.basicScenarioSoundChanged(item);
			}

			@Override
			public void soundEvent(final E item, final SoundEventType event)
			{
				// not required
			}
		};
	}

	@Override
	public int getRowCount()
	{
		if (_data == null)
		{
			return 0;
		}
		return _data.size();
	}

	/**
	 * Event
	 * @param index index
	 * @param item item
	 */
	protected void listItemAdded(final int index, final E item)
	{
		this.fireTableRowsInserted(index, index);
		item.addListener(_soundItemListener);
	}

	/**
	 * Event
	 * @param index index
	 * @param item item
	 */
	protected void listItemRemoved(final int index, final E item)
	{
		this.fireTableRowsDeleted(index, index);
		item.removeListener(_soundItemListener);
	}

	/**
	 * Event
	 * @param item item
	 */
	protected void basicScenarioSoundChanged(final E item)
	{
		final int index = _data.indexOf(item);
		this.fireTableRowsUpdated(index, index);
	}

	/**
	 * Sets the model
	 * 
	 * @param data model
	 */
	public void setModel(final IListenerList<E> data)
	{
		if (_data != null)
		{
			_data.removeListener(_listener);
			for (E item : _data)
			{
				item.removeListener(_soundItemListener);
			}
		}
		_data = data;
		if (_data != null)
		{
			_data.addListener(_listener);
			for (E item : _data)
			{
				item.addListener(_soundItemListener);
			}
		}
		this.fireTableDataChanged();
	}

	/**
	 * returns the model
	 * 
	 * @return model
	 */
	public IListenerList<E> getModel()
	{
		return _data;
	}

}