/*
 * NodeTransferable.java
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
package seventhsense.gui.transfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import seventhsense.data.INode;

/**
 * Transferable for INodes
 * 
 * @author Parallan
 *
 */
public class NodeTransferable implements Transferable
{
	public static final DataFlavor NODE_TRANSFERABLE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + NodeTransferable.class.getName() + "\"", "7th Sense Node");

	private final INode[] _nodes;

	/**
	 * Creates a transferable for INodes
	 * 
	 * @param nodes nodes
	 */
	public NodeTransferable(final INode[] nodes)
	{
		_nodes = nodes;
	}

	@Override
	public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (flavor.equals(NODE_TRANSFERABLE_FLAVOR))
		{
			return _nodes;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { NODE_TRANSFERABLE_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor flavor)
	{
		return flavor.equals(NODE_TRANSFERABLE_FLAVOR);
	}

}
