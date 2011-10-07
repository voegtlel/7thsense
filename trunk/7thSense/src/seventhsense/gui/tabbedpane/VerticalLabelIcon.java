/*
 * VerticalLabelIcon.java
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
package seventhsense.gui.tabbedpane;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

/**
 * Experimental icon with vertical text
 * 
 * @author Parallan
 *
 */
public class VerticalLabelIcon implements Icon
{
	private String _label;
	private Component _component;
	private Image _cachedImage;
	private boolean _rotateLeft;
	private Insets _insets;

	/**
	 * Creates a icon with vertical text
	 * 
	 * @param component component to use for styling
	 * @param label text to print on label
	 * @param rotateLeft rotate left, otherwise rotate right
	 */
	public VerticalLabelIcon(final Component component, final String label, final boolean rotateLeft)
	{
		this(component, label, rotateLeft, new Insets(2, 2, 2, 2));
	}
	
	/**
	 * Creates a icon with vertical text
	 * 
	 * @param component component to use for styling
	 * @param label text to print on label
	 * @param rotateLeft rotate left, otherwise rotate right
	 * @param insets insets for space
	 */
	public VerticalLabelIcon(final Component component, final String label, final boolean rotateLeft, final Insets insets)
	{
		_component = component;
		_label = label;
		_rotateLeft = rotateLeft;
		_insets = insets;
		renderImage();
		_component.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(final PropertyChangeEvent event)
			{
				VerticalLabelIcon.this.propertyChange(event);
			}
		});
	}
	
	/**
	 * Event.
	 * 
	 * @param event event
	 */
	private void propertyChange(final PropertyChangeEvent event)
	{
		if ("font".equals(event.getPropertyName()))
		{
			renderImage();
			_component.invalidate();
		}
	}

	
	/**
	 * Renders the icon image
	 */
	private void renderImage()
	{
		final FontRenderContext renderContext = new FontRenderContext(new AffineTransform(), true, true);
		final Rectangle2D labelBounds = _component.getFont().getStringBounds(_label, 0, _label.length(), renderContext);
		
		AffineTransform transform = new AffineTransform();
		transform.rotate(_rotateLeft?-Math.PI/2:Math.PI/2);
		
		final Point2D.Double resultMin = new Point2D.Double(0, 0);
		final Point2D.Double resultMax = new Point2D.Double(0, 0);
		transform.transform(new Point2D.Double(labelBounds.getMinX(), labelBounds.getMinY()), resultMin);
		transform.transform(new Point2D.Double(labelBounds.getMaxX(), labelBounds.getMaxY()), resultMax);
		
		final Rectangle2D resultRectangle = new Rectangle2D.Double(Math.min(resultMin.getX(), resultMax.getX()), Math.min(resultMin.getY(), resultMax.getY()), Math.abs(resultMax.getX() - resultMin.getX()), Math.abs(resultMax.getY() - resultMin.getY()));
		
		transform = new AffineTransform();
		transform.translate(-resultRectangle.getX() + _insets.left, -resultRectangle.getY() + _insets.top);
		transform.rotate(_rotateLeft?-Math.PI/2:Math.PI/2);
		
		final BufferedImage textImg = new BufferedImage((int)Math.ceil(resultRectangle.getWidth()) + _insets.left + _insets.right, (int)Math.ceil(resultRectangle.getHeight()) + _insets.top + _insets.bottom, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = textImg.createGraphics();
		
		transform.transform(new Point2D.Double(labelBounds.getMaxX(), labelBounds.getMaxY()), resultMax);
		transform.transform(new Point2D.Double(labelBounds.getMinX(), labelBounds.getMinY()), resultMin);
		
		graphics.setFont(_component.getFont());
		graphics.setColor(_component.getForeground());
		graphics.setBackground(_component.getBackground());
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setTransform(transform);
		graphics.drawString(_label, 0, 0);
		
		//graphics.setTransform(new AffineTransform());
		//graphics.drawRect(0, 0, textImg.getWidth()-1, textImg.getHeight()-1);
		
		graphics.dispose();
		
		_cachedImage = textImg;
	}

	@Override
	public void paintIcon(final Component c, final Graphics g, final int x, final int y)
	{
		g.drawImage(_cachedImage, x, y, c);
	}

	@Override
	public int getIconWidth()
	{
		return _cachedImage.getWidth(_component);
	}

	@Override
	public int getIconHeight()
	{
		return _cachedImage.getHeight(_component);
	}
}
