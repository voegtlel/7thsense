/*
 * ListenerArrayList.java
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
package seventhsense.data.listenerlist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import seventhsense.data.eventlist.EventList;

/**
 * An array implementation of the listener list
 * 
 * @author Parallan
 *
 * @param <E> type of items
 */
public class ListenerArrayList<E> extends ArrayList<E> implements IListenerList<E>, Serializable
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Listeners
	 */
	private transient EventList<IListItemListener<E>> _listeners;
	
	/**
	 * Creates an array listener list
	 */
	public ListenerArrayList()
	{
		super();
		initialize();
	}

	@Override
	public boolean add(final E element)
	{
		final boolean result = super.add(element);
		fireItemAdded(this.size() - 1, element);
		return result;
	}
	
	@Override
	public boolean addAll(final Collection<? extends E> c)
	{
		int startIndex = this.size() - 1;
		final boolean result = super.addAll(c);
		for(E e : c)
		{
			fireItemAdded(startIndex, e);
			startIndex++;
		}
		return result;
	}
	
	@Override
	public boolean addAll(final int index, final Collection<? extends E> c)
	{
		int startIndex = index;
		final boolean result = super.addAll(index, c);
		for(E e : c)
		{
			fireItemAdded(startIndex, e);
			startIndex++;
		}
		return result;
	}
	
	@Override
	public void clear()
	{
		@SuppressWarnings("unchecked")
		final E[] origData = (E[]) this.toArray();
		super.clear();
		int index = 0;
		for(E e : origData)
		{
			fireItemRemoved(index, e);
			index++;
		}
	}
	
	@Override
	public E remove(final int index)
	{
		final E result =  super.remove(index);
		fireItemRemoved(index, result);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(final Object o)
	{
		final int index = super.indexOf(o);
		final boolean result = super.remove(o);
		fireItemRemoved(index, (E) o);
		return result;
	}
	
	@Override
	public boolean removeAll(final Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(final Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add(final int index, final E element)
	{
		super.add(index, element);
		fireItemAdded(index, element);
	}

	@Override
	public void addListener(final IListItemListener<E> listener)
	{
		_listeners.add(listener);
	}

	@Override
	public void removeListener(final IListItemListener<E> listener)
	{
		_listeners.remove(listener);
	}
	
	/**
	 * Fires the item added event.
	 * 
	 * @param index index
	 * @param item item
	 */
	protected void fireItemAdded(final int index, final E item)
	{
		for(IListItemListener<E> listener : _listeners.iterateEvents())
		{
			listener.itemAdded(this, index, item);
		}
	}
	
	/**
	 * Fires the item removed event.
	 * 
	 * @param index index
	 * @param item item
	 */
	protected void fireItemRemoved(final int index, final E item)
	{
		for(IListItemListener<E> listener : _listeners.iterateEvents())
		{
			listener.itemRemoved(this, index, item);
		}
	}
	
	/**
	 * Initializes transient fields.
	 */
	private void initialize()
	{
		_listeners = new EventList<IListItemListener<E>>();
	}

	/**
	 * Function for deserialization.
	 * 
	 * @param in object for deserializing
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		initialize();
	}
}
