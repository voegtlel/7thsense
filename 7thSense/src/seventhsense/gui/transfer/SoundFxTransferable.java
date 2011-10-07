/*
 * SoundFxTransferable.java
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

import seventhsense.data.scenario.sound.SoundFxItem;

/**
 * Transferable for SoundFxItems
 * 
 * @author Parallan
 *
 */
public class SoundFxTransferable implements Transferable
{
	public static final DataFlavor SOUNDFX_TRANSFERABLE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + SoundFxTransferable.class.getName() + "\"", "7th Sense SoundFx");

	private final SoundFxItem[] _items;

	/**
	 * Creates a transferable for SoundFxItems
	 * 
	 * @param items items to transfer
	 */
	public SoundFxTransferable(final SoundFxItem[] items)
	{
		_items = items;
	}

	@Override
	public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (flavor.equals(SOUNDFX_TRANSFERABLE_FLAVOR))
		{
			return _items;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { SOUNDFX_TRANSFERABLE_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor flavor)
	{
		return flavor.equals(SOUNDFX_TRANSFERABLE_FLAVOR);
	}

}
