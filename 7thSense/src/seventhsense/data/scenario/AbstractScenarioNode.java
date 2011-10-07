/*
 * AbstractScenarioNode.java
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
package seventhsense.data.scenario;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.UUID;

import javax.swing.tree.TreeNode;

import seventhsense.data.AbstractNode;
import seventhsense.data.INode;
import seventhsense.data.scenario.sound.IPlayable;

/**
 * Class for scenarios.
 * Leaf.
 * 
 * @author Parallan
 *
 */
public abstract class AbstractScenarioNode extends AbstractNode implements IPlayable
{
	/**
	 * Default serial version for serializable
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * @param name Name of this scenario
	 */
	public AbstractScenarioNode(final String name)
	{
		super(name);
	}
	
	/**
	 * Sets the fadetime
	 * @param fadeTime
	 */
	public abstract void setFadeTime(double fadeTime);

	@Override
	public TreeNode getChildAt(final int index)
	{
		throw new IllegalStateException("leaf has no children");
	}

	@Override
	public int getChildCount() //NOPMD
	{
		return 0;
	}

	@Override
	public int getIndex(final TreeNode node)
	{
		throw new IllegalStateException("leaf has no children");
	}

	@Override
	public boolean getAllowsChildren() //NOPMD
	{
		return false;
	}

	@Override
	public boolean isLeaf() //NOPMD
	{
		return true;
	}

	@Override
	public Enumeration<?> children()
	{
		throw new IllegalStateException("leaf has no children");
	}

	@Override
	public Iterator<INode> iterator()
	{
		return new Iterator<INode>()
		{
			@Override
			public boolean hasNext()
			{
				return false;
			}

			@Override
			public INode next()
			{
				return null;
			}

			@Override
			public void remove()
			{
				// not required
			}
		};
	}

	@Override
	public void addNode(final int index, final INode node)
	{
		throw new IllegalStateException("leaf has no children");
	}

	@Override
	public void removeNode(final INode node)
	{
		throw new IllegalStateException("leaf has no children");
	}
	
	@Override
	public void removeNode(int index)
	{
		throw new IllegalStateException("leaf has no children");
	}
	
	@Override
	public INode findNode(final UUID uuid) //NOPMD
	{
		return null;
	}
}
