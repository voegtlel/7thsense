/*
 * DisabledCheckboxCellRenderer.java
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

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer for showing readonly checkboxes as disabled checkboxes
 * 
 * @author Parallan
 *
 */
public class DisabledCheckboxCellRenderer extends JCheckBox implements TableCellRenderer, UIResource
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	/**
	 * Creates the renderer
	 */
	public DisabledCheckboxCellRenderer()
	{
		super();
		setHorizontalAlignment(JLabel.CENTER);
		setBorderPainted(true);
		setEnabled(false);
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
	{
		if (isSelected)
		{
			setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		}
		else
		{
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		setSelected((value != null && ((Boolean) value).booleanValue()));

		if (hasFocus)
		{
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		}
		else
		{
			setBorder(noFocusBorder);
		}

		return this;
	}
}