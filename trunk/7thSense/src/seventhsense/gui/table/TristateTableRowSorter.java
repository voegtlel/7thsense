/*
 * TristateTableRowSorter.java
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
package seventhsense.gui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Row sorter with tristate sorting: Default sorting, ascending, descending
 * 
 * @author Parallan
 *
 * @param <M> type of table model
 */
public class TristateTableRowSorter<M extends TableModel> extends TableRowSorter<M>
{
	@Override
	public void toggleSortOrder(final int column)
	{
		if (column >= 0 && column < getModelWrapper().getColumnCount() && isSortable(column))
		{
			final List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
			if (!keys.isEmpty())
			{
				final SortKey sortKey = keys.get(0);
				if ((sortKey.getColumn() == column) && (sortKey.getSortOrder() == SortOrder.DESCENDING))
				{
					setSortKeys(null);
					return;
				}
			}
		}
		super.toggleSortOrder(column);
	}
}
