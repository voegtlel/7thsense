/*
 * LinkReferenceNode.java
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
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

/**
 * Class for subnodes in LinkNode
 * @author Parallan
 *
 */
class LinkReferenceNode extends LinkNode
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a link reference node, that reflects the wrapped node.
	 * Should only be used by LinkNode
	 * 
	 * @param wrappedNode
	 */
	public LinkReferenceNode(final INode wrappedNode)
	{
		super(wrappedNode);
	}

	@Override
	public UUID getUUID()
	{
		return _wrappedNode.getUUID();
	}
	
	@Override
	public INode getWrappedNode()
	{
		return _wrappedNode;
	}
	
	@Override
	public INode deepClone()
	{
		return _wrappedNode.deepClone();
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
		throw new NotSerializableException("Can't deserialize LinkReferenceNode");
	}

	/**
	 * Function for serialization.
	 * 
	 * @param out object for serializing
	 * @throws IOException
	 */
	private void writeObject(final ObjectOutputStream out) throws IOException
	{
		throw new NotSerializableException("Can't serialize LinkReferenceNode");
	}
}