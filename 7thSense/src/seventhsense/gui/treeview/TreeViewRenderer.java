/*
 * TreeViewRenderer.java
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
package seventhsense.gui.treeview;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import seventhsense.data.ILinkNode;
import seventhsense.data.INode;

/**
 * Renderer for treeview using own icons
 * 
 * @author Drag-On, Parallan
 *
 */
public class TreeViewRenderer extends DefaultTreeCellRenderer
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Icon used for linked nodes
	 */
	private final Icon _linkNodeIcon;

	/**
	 * Icon used for invalid nodes
	 */
	private final Icon _warningNodeIcon;
	
	/**
	 * If true, the link node icon should be used
	 */
	private boolean _useLinkNodeIcon;

	/**
	 * Constructor
	 */
	public TreeViewRenderer()
	{
		super();
		_linkNodeIcon = new ImageIcon(TreeViewRenderer.class.getResource("/seventhsense/resources/LinkNodeIcon_16.png"));
		_warningNodeIcon = new ImageIcon(TreeViewRenderer.class.getResource("/seventhsense/resources/Warning_16.png"));
	}
	
	/**
	 * Sets if the renderer should use the special link node icon
	 * 
	 * @param useLinkNodeIcon renderer should use the special link node icon
	 */
	public void setUseLinkNodeIcon(final boolean useLinkNodeIcon)
	{
		_useLinkNodeIcon = useLinkNodeIcon;
	}

	/**
	 * Get the renderer component
	 * 
	 * @param tree
	 * @param value
	 * @param sel
	 * @param expanded
	 * @param leaf
	 * @param row
	 * @param hasFocus
	 */
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded,
			final boolean leaf, final int row, final boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if ((value instanceof ILinkNode) && _useLinkNodeIcon)
		{
			setIcon(_linkNodeIcon);
			setToolTipText("This node links to another node.");
		}
		else if (leaf)
		{
			setToolTipText("This is a scenario node.");
		}
		else
		{
			setToolTipText("This is a folder node.");
		}
		if(value instanceof ILinkNode)
		{
			INode curNode = (INode) value;
			while(curNode instanceof ILinkNode)
			{
				if(!curNode.isValid())
				{
					setToolTipText("WARNING: The referenced node has been deleted, please delete this node.");
					setIcon(_warningNodeIcon);
					break;
				}
				curNode = ((ILinkNode)curNode).getWrappedNode();
			}
			if(!(curNode instanceof ILinkNode) && !curNode.isValid())
			{
				setIcon(_warningNodeIcon);
				setToolTipText("WARNING: This node has errors and may not behave as expected.");
			}
		}
		else if ((value instanceof INode) && !((INode) value).isValid())
		{
			setIcon(_warningNodeIcon);
			setToolTipText("WARNING: This node has errors and may not behave as expected.");
		}
		return this;
	}
}
