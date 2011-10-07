/*
 * AbstractBasicScenarioTransferHandler.java
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
package seventhsense.gui.basicscenario;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import seventhsense.data.IItem;
import seventhsense.data.listenerlist.IListenerList;

/**
 * Abstract transfer handler for subitems of basic scenario
 * 
 * @author Parallan
 *
 * @param <E> type of items
 */
public abstract class AbstractBasicScenarioTransferHandler<E extends IItem> extends TransferHandler
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(AbstractBasicScenarioTransferHandler.class.getName());

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * listener list to listen to
	 */
	protected IListenerList<E> _listenerList;

	/**
	 * The main panel to use
	 */
	protected final AbstractBasicScenarioTablePanel<E> _editorPanel;

	/**
	 * Flavor of this item
	 */
	protected final DataFlavor _flavor;
	
	/**
	 * Creates a transfer handler
	 * 
	 * @param editorPanel panel to operate on
	 * @param flavor flavor of the item
	 */
	public AbstractBasicScenarioTransferHandler(final AbstractBasicScenarioTablePanel<E> editorPanel, final DataFlavor flavor)
	{
		super();
		_editorPanel = editorPanel;
		_flavor = flavor;
	}
	
	/**
	 * Sets the model
	 * 
	 * @param listenerList model
	 */
	public void setModel(final IListenerList<E> listenerList)
	{
		_listenerList = listenerList;
	}
	
	@Override
	public boolean canImport(final TransferSupport support)
	{
		if(_listenerList == null)
		{
			return false;
		}
		if(support.isDataFlavorSupported(_flavor))
		{
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean importData(final TransferSupport support)
	{
		if(!canImport(support))
		{
			return false;
		}
		LOGGER.log(Level.FINER, "import");
		if(support.isDataFlavorSupported(_flavor))
		{
			try
			{
				final E[] transferData = (E[]) support.getTransferable().getTransferData(_flavor);
				int insertIndex = _listenerList.size();
				if(support.isDrop())
				{
					final JTable.DropLocation dropLocation = (JTable.DropLocation) support.getDropLocation();
					insertIndex = dropLocation.getRow();
				}
				else
				{
					final E destinationItem = _editorPanel.getSelectedItem();
					insertIndex = _listenerList.indexOf(destinationItem) + 1;
				}
				for(E importItem : transferData)
				{
					LOGGER.log(Level.FINE, "import node to " + insertIndex + " -> " + importItem);
					_listenerList.add(insertIndex, (E)importItem.deepClone());
					insertIndex++;
				}
				return true;
			}
			catch (UnsupportedFlavorException e)
			{
				throw new IllegalStateException("flavor exception should have been checked before!", e);
			}
			catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
				return false;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void exportDone(final JComponent source, final Transferable data, final int action)
	{
		if(action == MOVE)
		{
			LOGGER.log(Level.FINER, "exportDone");
			try
			{
				final E[] transferData = (E[]) data.getTransferData(_flavor);
				for(E item : transferData)
				{
					_listenerList.remove(item);
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
