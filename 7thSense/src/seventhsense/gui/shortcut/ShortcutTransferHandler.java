/*
 * ShortcutTransferHandler.java
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
package seventhsense.gui.shortcut;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import seventhsense.data.INode;
import seventhsense.data.scenario.sound.IPlayable;
import seventhsense.gui.transfer.NodeTransferable;
import seventhsense.gui.treeview.TreeViewTransferHandler;

/**
 * Transfer handler for dragging to shortcut
 * 
 * @author Parallan
 *
 */
public class ShortcutTransferHandler extends TransferHandler
{
	private static final Logger LOGGER = Logger.getLogger(TreeViewTransferHandler.class.getName());

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private final ShortcutPanel _shortcutPanel;

	/**
	 * Creates a transfer handler
	 * 
	 * @param shortcutPanel Panel to operate on
	 */
	public ShortcutTransferHandler(final ShortcutPanel shortcutPanel)
	{
		super();
		_shortcutPanel = shortcutPanel;
	}

	@Override
	public boolean canImport(final TransferSupport support)
	{
		if (support.isDataFlavorSupported(NodeTransferable.NODE_TRANSFERABLE_FLAVOR))
		{
			final Transferable transferable = support.getTransferable();
			if (transferable != null)
			{
				try
				{
					final INode[] transferData = (INode[]) transferable.getTransferData(NodeTransferable.NODE_TRANSFERABLE_FLAVOR);
					// Check if data is descendant of destination
					if(transferData.length != 1)
					{
						return false;
					}
					final INode importNode = transferData[0].getRealNode();
					if(!(importNode instanceof IPlayable))
					{
						return false;
					}
				}
				catch (UnsupportedFlavorException e) //NOPMD
				{
				}
				catch (IOException e) //NOPMD
				{
				}
			}
			if(support.isDrop())
			{
				support.setDropAction(LINK);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean importData(final TransferSupport support)
	{
		if (!canImport(support))
		{
			return false;
		}
		LOGGER.log(Level.FINER, "import");
		if (support.isDataFlavorSupported(NodeTransferable.NODE_TRANSFERABLE_FLAVOR))
		{
			try
			{
				final INode[] transferData = (INode[]) support.getTransferable().getTransferData(NodeTransferable.NODE_TRANSFERABLE_FLAVOR);
				if(transferData.length > 0)
				{
					final INode realNode = transferData[0].getRealNode();
					if(realNode instanceof IPlayable)
					{
						_shortcutPanel.setModel((IPlayable) realNode);
					}
					if(support.isDrop())
					{
						support.setDropAction(LINK);
					}
					return true;
				}
			}
			catch (UnsupportedFlavorException e)
			{
				throw new IllegalStateException("flavor exception should have been checked before!", e);
			}
			catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
		return false;
	}

	@Override
	protected Transferable createTransferable(final JComponent c)
	{
		return null;
	}

	@Override
	public int getSourceActions(final JComponent c)
	{
		return NONE;
	}
}
