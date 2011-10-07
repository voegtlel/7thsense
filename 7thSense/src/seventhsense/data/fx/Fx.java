/*
 * Fx.java
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
package seventhsense.data.fx;

import java.util.logging.Level;
import java.util.logging.Logger;

import seventhsense.data.eventlist.EventList;

/**
 * Class for performing a fx: A thread fires the update routine in the fx setters using the given transitions.
 * 
 * Inspired by mootools
 * 
 * @author Parallan
 *
 */
public class Fx
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(Fx.class.getName());

	/**
	 * The thread that runs the updates
	 */
	private Thread _thread;
	
	/**
	 * Lock object for the thread
	 */
	private final Object _threadLock = new Object();

	/**
	 * State of the thread
	 */
	private boolean _running = false;

	/**
	 * Listeners
	 */
	private final EventList<IFxListener> _listeners = new EventList<IFxListener>();
	
	/**
	 * Setters for new values
	 */
	private final EventList<IFxSetter> _fxSetter = new EventList<IFxSetter>();
	
	/**
	 * start value for transition
	 */
	private double _from;
	
	/**
	 * destination value for transition
	 */
	private double _to;
	
	/**
	 * current value
	 */
	private double _value;
	
	/**
	 * duration of transition
	 */
	private double _duration;
	
	/**
	 * delay between frames
	 */
	private int _frameDelay;
	
	/**
	 * time when the fx was started
	 */
	private long _startTime;
	
	/**
	 * the transition used to transform the value
	 */
	private final ITransition _transition;

	/**
	 * create a fx
	 * 
	 * @param fps fps for fx
	 * @param transition transition for fx
	 */
	public Fx(final double fps, final ITransition transition)
	{
		_frameDelay = (int) (1000.0 / fps);
		if(_frameDelay <= 10)
		{
			_frameDelay = 10;
		}
		_transition = transition;
	}
	
	/**
	 * Create a fx with default 50 fps
	 * 
	 * @param transition transition to use
	 */
	public Fx(final ITransition transition)
	{
		this(50, transition);
	}
	
	/**
	 * Stops the thread and cancels the fx
	 */
	public void stop()
	{
		LOGGER.log(Level.FINE, "stop");
		Thread thread;
		synchronized (_threadLock)
		{
			thread = _thread;
			if (_thread != null)
			{
				_running = false;
				_thread = null;
				_threadLock.notifyAll();
				LOGGER.log(Level.FINER, "Lock notified");
			}
		}
		if (thread != null)
		{
			try
			{
				thread.join(100);
			}
			catch (InterruptedException e)
			{
			}
		}
		fireFxCancelled();
	}

	/**
	 * Returns true, if the thread is running
	 * 
	 * @return running state
	 */
	public boolean isRunning()
	{
		synchronized (_threadLock)
		{
			return _running;
		}
	}
	
	/**
	 * Sets new fade parameters. If no fading is active, the to value will be set immediately
	 * 
	 * @param from new from-value
	 * @param to new to-value
	 */
	public void setRange(final double from, final double to)
	{
		synchronized (_threadLock)
		{
			_from = from;
			_to = to;
		}
	}
	
	/**
	 * Sets the total duration of the active process
	 * 
	 * @param duration duration
	 */
	public void setDuration(final double duration)
	{
		synchronized (_threadLock)
		{
			_duration = duration;
		}
	}
	
	/**
	 * Gets the total duration to a new value
	 * 
	 * @return duration
	 */
	public double getDuration()
	{
		synchronized (_threadLock)
		{
			return _duration;
		}
	}
	
	/**
	 * Gets the total duration to a new value
	 * 
	 * @return duration
	 */
	public double getRemainingDuration()
	{
		synchronized (_threadLock)
		{
			return _duration - (double)(System.nanoTime() - _startTime) * 1.0e-9;
		}
	}
	
	/**
	 * Sets the total duration to a new value
	 * 
	 * @param duration duration
	 */
	public void setRemainingDuration(final double duration)
	{
		synchronized (_threadLock)
		{
			_duration = duration + (double)(System.nanoTime() - _startTime) * 1.0e-9;
		}
	}
	
	/**
	 * Start the fx
	 * 
	 * @param from start value
	 * @param to destination value
	 * @param duration duration
	 */
	public void start(final double from, final double to, final double duration)
	{
		LOGGER.log(Level.FINE, "Fx from " + from + " to " + to + " in " + duration + " sec");
		synchronized (_threadLock)
		{
			_from = from;
			_to = to;
			_duration = duration;
			_running = true;
			_startTime = System.nanoTime();

			if (_thread == null)
			{
				LOGGER.log(Level.FINER, "New thread");
				_thread = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						Fx.this.run();
					}
				});
				_thread.start();
			}
		}
	}
	
	/**
	 * Start the fx
	 * 
	 * @param to destination value
	 * @param duration duration
	 */
	public void start(final double to, final double duration)
	{
		start(getValue(), to, duration);
	}
	
	/**
	 * Cancels the thread and sets the value
	 * 
	 * @param value new value
	 */
	public void setValue(final double value)
	{
		stop();
		synchronized (_threadLock)
		{
			_value = value;
		}
		fireFxSetter(value);
	}
	
	/**
	 * getter for value
	 * 
	 * @return value
	 */
	public double getValue()
	{
		synchronized (_threadLock)
		{
			return _value;
		}
	}
	
	/**
	 * Performs the update to value
	 * 
	 * @param factor time-factor [0, 1]
	 */
	protected void step(final double factor)
	{
		final double delta = _transition.getValue(factor);
		_value = (_to - _from) * delta + _from;
		fireFxSetter(_value);
	}

	/**
	 * Thread-routine
	 */
	private void run()
	{
		LOGGER.log(Level.FINE, "run");
		
		double totalTime = 0;
		
		fireFxStarted();
		
		double duration = _duration;
		boolean running = _running;
		while (running && (totalTime < duration))
		{
			synchronized (_threadLock)
			{
				// perform step
				step(totalTime / _duration);
				// Wait for frame
				try
				{
					if (!_running)
					{
						break;
					}
					_threadLock.wait(_frameDelay);
				}
				catch (InterruptedException e)
				{
					LOGGER.log(Level.SEVERE, e.toString(), e);
					break;
				}
				// Update time
				final long curTime = System.nanoTime();
				totalTime = ((double) (curTime - _startTime) * 1.0e-9);
				
				// Update state
				running = _running;
				duration = _duration;
			}
		}
		LOGGER.log(Level.FINE, "thread finished");
		// Finish thread if it was not cancelled
		if(running)
		{
			synchronized (_threadLock)
			{
				step(1.0);
				_running = false;
				_thread = null;
			}
			fireFxFinished();
		}
	}
	
	/**
	 * Add a setter, that will be called on each update for a new value
	 * 
	 * @param setter setter
	 */
	public void addFxSetter(final IFxSetter setter)
	{
		_fxSetter.add(setter);
	}
	
	/**
	 * Remove a setter
	 * 
	 * @param setter setter
	 */
	public void removeFxSetter(final IFxSetter setter)
	{
		_fxSetter.remove(setter);
	}

	/**
	 * Add a listener
	 * 
	 * @param listener listener
	 */
	public void addListener(final IFxListener listener)
	{
		_listeners.add(listener);
	}

	/**
	 * Remove a listener
	 * 
	 * @param listener listener
	 */
	public void removeListener(final IFxListener listener)
	{
		_listeners.remove(listener);
	}

	/**
	 * Fire event
	 */
	private void fireFxFinished()
	{
		for (IFxListener listener : _listeners.iterateEvents())
		{
			listener.fxFinished();
		}
	}
	
	/**
	 * Fire event
	 */
	private void fireFxStarted()
	{
		for (IFxListener listener : _listeners.iterateEvents())
		{
			listener.fxStarted();
		}
	}
	
	/**
	 * Fire event
	 */
	private void fireFxCancelled()
	{
		for (IFxListener listener : _listeners.iterateEvents())
		{
			listener.fxCancelled();
		}
	}
	
	/**
	 * Fire event
	 * 
	 * @param value value
	 */
	private void fireFxSetter(final double value)
	{
		for (IFxSetter setter : _fxSetter.iterateEvents())
		{
			setter.setValue(value);
		}
	}
}
