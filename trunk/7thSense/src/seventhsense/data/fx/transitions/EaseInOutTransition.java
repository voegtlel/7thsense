/*
 * EaseInOutTransition.java
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
public class EaseInOutTransition implements ITransition
{
	/**
	 * Transition used for easing in
	 */
	private final ITransition _transitionIn;
	/**
	 * Transition used for easing out
	 */
	private final ITransition _transitionOut;
	/**
	 * transition used for combining the in and out transitions
	 */
	private final ITransition _transposeTransition;
	
	/**
	 * Creates a combined transition with easing in and easing out transitions combined with a transpose transition
	 * 
	 * @param transitionIn transition used for easing in
	 * @param transitionOut transition used for easing out
	 * @param transposeTransition transition used for combining the in and out transitions
	 */
	public EaseInOutTransition(final ITransition transitionIn, final ITransition transitionOut, final ITransition transposeTransition)
	{
		_transitionIn = transitionIn;
		_transitionOut = transitionOut;
		_transposeTransition = transposeTransition;
	}
	
	@Override
	public double getValue(final double value)
	{
		final double inValue = _transitionIn.getValue(value);
		final double outValue = 1 - _transitionOut.getValue(1 - value);
		final double transpose = _transposeTransition.getValue(value);
		
		return inValue * transpose + outValue * (1 - transpose);
	}
}
