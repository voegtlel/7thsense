/*
 * ElasticTransition.java
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
package seventhsense.data.fx.transitions;

import seventhsense.data.fx.ITransition;

/**
 * Provides exponential transition
 * 
 * @author Parallan
 *
 */
public class ElasticTransition implements ITransition
{
	/**
	 * Parameter for calculation
	 */
	private final double _elasticity;
	
	/**
	 * Creates an elastic transition
	 * 
	 * @param elasticity value for calculation (default 1)
	 */
	public ElasticTransition(final double elasticity /* = 1*/)
	{
		_elasticity = elasticity;
	}
	
	/**
	 * Creates an elastic transition
	 */
	public ElasticTransition()
	{
		_elasticity = 1;
	}
	
	@Override
	public double getValue(final double value)
	{
		return Math.pow(2, 10 * (value - 1)) * Math.cos(20 * (value - 1) * Math.PI * (_elasticity) / 3);
	}
}
