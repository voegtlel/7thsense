/*
 * AbstractNode.java
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.UUID;

import seventhsense.data.eventlist.EventList;

/**
 * Abstract class for INode.
 * Provides basic properties: parent, name, listeners and toString
 * 
 * @author Parallan
 *
 */
public abstract class AbstractNode implements INode
{
	/**
	 * Default serial version for serializable
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * name of the node
	 */
	protected String _name;
	
	/**
	 * parent of the node
	 */
	protected INode _parent = null;
	
	/**
	 * Unchangable uuid of the node, identifying it globally
	 */
	private final UUID _uuid = UUID.randomUUID();
	
	/**
	 * list of listeners
	 */
	private transient EventList<INodeListener> _nodeListeners;
	
	/**
	 * Default constructor for abstract node. Sets the name of the node.
	 * 
	 * @param name name of the node
	 */
	public AbstractNode(final String name)
	{
		_name = name;
		initialize();
	}

	@Override
	public INode getParent()
	{
		return _parent;
	}
	
	public void setParent(final INode parent)
	{
		_parent = parent;
	}

	@Override
	public String getName()
	{
		return _name;
	}

	@Override
	public void setName(final String name)
	{
		fireNodeChanging("name");
		_name = name;
		fireNodeChanged("name");
	}

	@Override
	public void addNodeListener(final INodeListener listener)
	{
		_nodeListeners.add(listener);
	}
	
	@Override
	public void removeNodeListener(final INodeListener listener)
	{
		_nodeListeners.remove(listener);
	}
	
	/**
	 * Fires the node added event
	 * 
	 * @param index index to the newly added node
	 * @param node inserted node
	 */
	protected void fireNodeAdded(final int index, final INode node)
	{
		for(INodeListener listener : _nodeListeners.iterateEvents())
		{
			listener.nodeAdded(this, index, node);
		}
	}
	
	/**
	 * Fires the node removed event
	 * 
	 * @param index index where the node once was
	 * @param node node that was removed
	 */
	protected void fireNodeRemoved(final int index, final INode node)
	{
		for(INodeListener listener : _nodeListeners.iterateEvents())
		{
			listener.nodeRemoved(this, index, node);
		}
	}
	
	/**
	 * fires the node changed event (usually when the node text changes)
	 * 
	 * @param property property
	 */
	protected void fireNodeChanged(final String property)
	{
		for(INodeListener listener : _nodeListeners.iterateEvents())
		{
			listener.nodeChanged(this, property);
		}
	}
	
	/**
	 * Fires the node adding event
	 * 
	 * @param index index to the newly added node
	 * @param node inserted node
	 */
	protected void fireNodeAdding(final int index, final INode node)
	{
		for(INodeListener listener : _nodeListeners.iterateEvents())
		{
			listener.nodeAdding(this, index, node);
		}
	}
	
	/**
	 * Fires the node removing event
	 * 
	 * @param index index where the node once was
	 * @param node node that was removed
	 */
	protected void fireNodeRemoving(final int index, final INode node)
	{
		for(INodeListener listener : _nodeListeners.iterateEvents())
		{
			listener.nodeRemoving(this, index, node);
		}
	}
	
	/**
	 * fires the node changing event (usually when the node text changes)
	 * 
	 * @param property property
	 */
	protected void fireNodeChanging(final String property)
	{
		for(INodeListener listener : _nodeListeners.iterateEvents())
		{
			listener.nodeChanging(this, property);
		}
	}
	
	@Override
	public String toString()
	{
		return _name;
	}
	
	/**
	 * Initialize transient members
	 */
	private void initialize()
	{
		_nodeListeners = new EventList<INodeListener>();
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
	
	@Override
	public UUID getUUID()
	{
		return _uuid;
	}
	
	@Override
	public INode getRealNode()
	{
		return this;
	}
}
