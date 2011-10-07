/*
 * IListItemListener.java
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


/**
 * Listener for the listener list
 * 
 * @author Parallan
 *
 * @param <E> item type
 */
public interface IListItemListener<E>
{
	/**
	 * Fired, when an item was added to the list
	 * 
	 * @param list list, where the item was added
	 * @param index index, where the item was added
	 * @param item item, that was added
	 */
	void itemAdded(IListenerList<E> list, int index, E item);
	
	/**
	 * Fired, when an item was removed from the list
	 * 
	 * @param list list, where the item was removed
	 * @param index index, where the item was removed
	 * @param item item, that was removed
	 */
	void itemRemoved(IListenerList<E> list, int index, E item);
}
