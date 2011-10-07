/*
 * INodeListener.java
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
package seventhsense.data;

/**
 * Listener for an INode
 * 
 * @author Parallan
 *
 */
public interface INodeListener
{
	/**
	 * Fired, when a child node was added
	 * 
	 * @param parentNode node to which was inserted
	 * @param index index, where the node was added
	 * @param node node, which was added
	 */
	void nodeAdded(INode parentNode, int index, INode node);
	
	/**
	 * Fired, when a child node is about to be added
	 * 
	 * @param parentNode node to which will be inserted
	 * @param index index, where the node will be added
	 * @param node node, which will be added
	 */
	void nodeAdding(INode parentNode, int index, INode node);
	
	/**
	 * Fired, when a child node was removed
	 * 
	 * @param parentNode node from which was removed
	 * @param index index, where the node was added
	 * @param node node, which was removed
	 */
	void nodeRemoved(INode parentNode, int index, INode node);
	
	/**
	 * Fired, when a child node is about to be remove
	 * 
	 * @param parentNode node from which is being removed
	 * @param index index, where the node is located
	 * @param node node, which will be removed
	 */
	void nodeRemoving(INode parentNode, int index, INode node);
	
	/**
	 * Fired, when the node was changed (mostly when the text changes)
	 * 
	 * @param node node which changed
	 * @param property property as a string, that was changed
	 */
	void nodeChanged(INode node, String property);
	
	/**
	 * Fired, when the node is about to change (mostly when the text changes)
	 * 
	 * @param node node which is changing
	 * @param property property as a string, that is changing
	 */
	void nodeChanging(INode node, String property);
}
