/*
 * ISoundItemListener.java
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
package seventhsense.data.scenario.sound;

import java.util.EventListener;

import seventhsense.data.IPropertyChangedListener;
import seventhsense.sound.engine.SoundEventType;

/**
 * Listener class for sound items
 * 
 * @author Parallan, Drag-On
 *
 * @param <E> listener for a sound item
 */
public interface ISoundItemListener<E> extends EventListener, IPropertyChangedListener<E>
{	
	/**
	 * fired, when a sound event occured
	 * 
	 * @param item item, that contains the event
	 * @param event type of the event
	 */
	void soundEvent(E item, SoundEventType event);
}
