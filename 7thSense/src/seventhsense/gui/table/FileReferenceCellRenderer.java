/*
 * FileReferenceCellRenderer.java
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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer for file references (right aligned text)
 * 
 * @author Parallan
 *
 */
public class FileReferenceCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates the renderer for file references
	 */
	public FileReferenceCellRenderer()
	{
		super();
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
	{
		super.getTableCellRendererComponent(table, value.toString(), isSelected, hasFocus, row, column);
		return this;
	}
	
	@Override
	public void paint(final Graphics g)
	{
		final StringBuffer renderStr = new StringBuffer(this.getText());
		final FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
		Rectangle2D r = fontMetrics.getStringBounds(renderStr.toString(), g);
		final double maxWidth = this.getWidth() - (this.getInsets().right + this.getInsets().left);
		if(r.getWidth() >= maxWidth)
		{
			renderStr.insert(0, "...");
			while((r.getWidth() >= maxWidth) && (renderStr.length() > 3))
			{
				r = fontMetrics.getStringBounds(renderStr.toString(), g);
				renderStr.delete(3, 4);
			}
			this.setHorizontalAlignment(JLabel.TRAILING);
		}
		else
		{
			this.setHorizontalAlignment(JLabel.LEADING);
		}
		this.setText(renderStr.toString());
		super.paint(g);
	}
}
