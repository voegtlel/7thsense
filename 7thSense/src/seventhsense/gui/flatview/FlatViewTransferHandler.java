/*
 * FlatViewTransferHandler.java
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
package seventhsense.gui.flatview;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import seventhsense.data.INode;
import seventhsense.gui.transfer.NodeTransferable;

/**
 * Transfer handler for exporting nodes from the flat view
 * 
 * @author Parallan
 *
 */
public class FlatViewTransferHandler extends TransferHandler
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(FlatViewTransferHandler.class.getName());

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private final FlatView _flatView;
	
	/**
	 * Creates a transfer handler
	 * 
	 * @param flatView TreeView to operate on
	 */
	public FlatViewTransferHandler(final FlatView flatView)
	{
		super();
		_flatView = flatView;
	}
	
	@Override
	public boolean canImport(final TransferSupport support)
	{
		return false;
	}
	
	@Override
	public boolean importData(final TransferSupport support)
	{
		return false;
	}
	
	@Override
	protected Transferable createTransferable(final JComponent c)
	{
		LOGGER.log(Level.FINER, "createTransferable");
		if(_flatView.getSelectedItem() == null)
		{
			return null;
		}
		else
		{
			return new NodeTransferable(new INode[] {_flatView.getSelectedItem()});
		}
	}
	
	@Override
	protected void exportDone(final JComponent source, final Transferable data, final int action)
	{
		if(action == MOVE)
		{
			LOGGER.log(Level.FINER, "exportDone");
			try
			{
				final INode[] transferData = (INode[]) data.getTransferData(NodeTransferable.NODE_TRANSFERABLE_FLAVOR);
				for(INode node : transferData)
				{
					node.getParent().removeNode(node);
				}
			}
			catch (UnsupportedFlavorException e)
			{
				throw new IllegalStateException(e);
			}
			catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
	}
	
	@Override
	public int getSourceActions(final JComponent c)
	{
		return COPY_OR_MOVE;
	}
}
