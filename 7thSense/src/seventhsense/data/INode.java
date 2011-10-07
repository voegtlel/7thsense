/*
 * INode.java
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

import java.io.Serializable;
import java.util.UUID;

import javax.swing.tree.TreeNode;

/**
 * Interface for all nodes.
 * Reflects the database.
 * 
 * @author Parallan
 *
 */
public interface INode extends Serializable, TreeNode, Iterable<INode>, IItem
{
	/**
	 * Getter for node name
	 * 
	 * @return name of node
	 */
	String getName();
	
	/**
	 * Getter for parent
	 * 
	 * Overrides {@link TreeNode#getParent()}
	 * 
	 * @return parent for node
	 */
	INode getParent();
	
	/**
	 * Setter for parent
	 * 
	 * @param parent new parent for the node
	 */
	void setParent(INode parent);
	
	/**
	 * Setter for node name
	 * 
	 * @param name new name for node
	 */
	void setName(String name);
	
	/**
	 * Adds a node to this node
	 * 
	 * @param index index, where to add the node. -1 for adding at end
	 * @param node node to insert
	 */
	void addNode(int index, INode node);
	
	/**
	 * removes a node
	 * 
	 * @param node node to remove
	 */
	void removeNode(INode node);
	
	/**
	 * removes a node
	 * 
	 * @param index index to remove
	 */
	void removeNode(int index);
	
	/**
	 * Adds a node listener
	 * 
	 * @param listener the listener to add
	 */
	void addNodeListener(INodeListener listener);
	
	/**
	 * removed a previously added node listener
	 * 
	 * @param listener listener to remove
	 */
	void removeNodeListener(INodeListener listener);
	
	/**
	 * Creates a deep clone of the object
	 * 
	 * @return clone of the node
	 */
	INode deepClone();
	
	/**
	 * Gets the universal unique identifier for this node
	 * 
	 * @return universal unique identifier for this node
	 */
	UUID getUUID();
	
	/**
	 * Searches this node and all subnodes for an INode with the UUID.
	 * 
	 * @param uuid uuid to find
	 * @return found object or null if not found
	 */
	INode findNode(UUID uuid);
	
	/**
	 * Resolves Links if existing
	 * @return the real node within this node (or this node if it is a real node)
	 */
	INode getRealNode();
	
	/**
	 * Checks if the item is correct. The result is cached, call validate to update it.
	 * @return true, if the item has no errors, otherwise false
	 */
	boolean isValid();
	
	/**
	 * Checks if the item is correct.
	 * To get the result, use {@link INode#isValid()}
	 * 
	 * @param recursive if true, the item is validated recursive, else only this item is validated
	 */
	void validate(boolean recursive);
}
