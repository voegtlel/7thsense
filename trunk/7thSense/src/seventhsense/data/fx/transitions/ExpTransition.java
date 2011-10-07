/*
 * ExpTransition.java
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

import seventhsense.data.fx.ITransitionReversible;

/**
 * Provides exponential transition
 * 
 * @author Parallan
 *
 */
public class ExpTransition implements ITransitionReversible
{
	/**
	 * Base of the exponential function
	 */
	private final double _base;
	
	/**
	 * Creates an exponential transition
	 * 
	 * @param base base of the exponential function
	 */
	public ExpTransition(final double base)
	{
		_base = base;
	}
	
	@Override
	public double getValue(final double value)
	{
		return (Math.pow(_base, value) - 1) / (_base - 1);
	}
	
	@Override
	public double getValueReverse(final double value)
	{
		return Math.log(value * (_base - 1) + 1) / Math.log(_base);
	}
}
