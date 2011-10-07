/*
 * EventList.java
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
package seventhsense.data.eventlist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * List for event listeners. Supports a safe enumeration, even if elements are deleted while enumerating
 * 
 * @author Parallan
 *
 * @param <E> type of listeners
 */
public class EventList<E> extends ArrayList<E>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Contains a copy for enumerations
	 */
	private Object[] _copyForEnumeration = new Object[0];
	
	/**
	 * Method for getting a safe enumeration, that is not modified when the list is changed
	 * 
	 * TODO: it is not safe, when inside the enumeration "iterateEvents" is called again!
	 * 
	 * @return safe enumeration
	 */
	public Iterable<E> iterateEvents()
	{
		// synchronize lists
		if(_copyForEnumeration.length != this.size())
		{
			_copyForEnumeration = new Object[this.size()];
		}
		for(int i = 0; i < this.size(); i++)
		{
			final E item = this.get(i);
			if(_copyForEnumeration[i] != item)
			{
				_copyForEnumeration[i] = item;
			}
		}
		return new Iterable<E>()
		{
			@Override
			public Iterator<E> iterator()
			{
				return new Iterator<E>()
				{
					int index = 0;
					
					@Override
					public boolean hasNext()
					{
						return index < _copyForEnumeration.length;
					}

					@SuppressWarnings("unchecked")
					@Override
					public E next()
					{
						if(index >= _copyForEnumeration.length)
						{
							throw new NoSuchElementException();
						}
						return (E) _copyForEnumeration[index++];
					}

					@Override
					public void remove()
					{
						throw new IllegalStateException("Not possible");
					}
				};
			}
		};
	}
}
