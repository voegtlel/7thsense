/*
 * DelayThread.java
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
package seventhsense.data.scenario.sound;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread for delaying an action
 * 
 * @author Parallan, Drag-On
 *
 */
public class DelayThread
{
	/**
	 * Logger for debugging purposes
	 */
	private static final Logger LOGGER = Logger.getLogger(DelayThread.class.getName());
	
	/**
	 * The thread that is doing the delay
	 */
	private final Thread _thread;
	/**
	 * The action to perform when the delay has elapsed 
	 */
	private final Runnable _afterFadeAction;
	
	/**
	 * Current state of the thread
	 */
	private boolean _isWaiting = false;

	/**
	 * Input delay time
	 */
	private final long _delayTime;
	
	/**
	 * Remaining delay time (used for pausing)
	 */
	private long _restDelayTime;
	
	/**
	 * Thread start time (used for pausing)
	 */
	private long _threadStart = 0;
	
	/**
	 * Paused state
	 */
	private boolean _isPaused = false;
	
	/**
	 * Resume state
	 */
	private boolean _shouldResume = false;
	
	/**
	 * specifies, if the action should be looped
	 */
	private boolean _loopAction = false;
	
	/**
	 * Creates a delay thread
	 * 
	 * @param delayTime time to delay for the action
	 * @param afterFadeAction action to run after the delay has elapsed
	 */
	public DelayThread(final double delayTime, final Runnable afterFadeAction)
	{
		_delayTime = _restDelayTime = (long)(delayTime * 1000.0);
		_thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				DelayThread.this.run();
			}
		});
		_afterFadeAction = afterFadeAction;
	}
	
	/**
	 * Thread main-routine
	 */
	private void run()
	{
		try
		{
			LOGGER.log(Level.FINE, "run start " + _restDelayTime);
			boolean loop;
			do
			{
				_isWaiting = true;
				_restDelayTime = _delayTime;
				synchronized (_thread)
				{
					do
					{
						if(_shouldResume)
						{
							_isPaused = false;
						}
						if(_restDelayTime > 0)
						{
							_thread.wait(_restDelayTime);
						}
					} while(_isPaused);
					_isWaiting = false;
				}
				_afterFadeAction.run();
				synchronized (_thread)
				{
					loop = _loopAction;
				}
			}
			while(loop);
			LOGGER.log(Level.FINE, "run done " + _restDelayTime);
		}
		catch (InterruptedException e)
		{
			synchronized (_thread)
			{
				_isWaiting = false;
			}
		}
	}
	
	/**
	 * start the thread
	 */
	public void start()
	{
		_isWaiting = true;
		_threadStart = System.currentTimeMillis();
		_thread.start();
	}
	
	/**
	 * pause the thread
	 */
	public void pause()
	{
		synchronized (_thread)
		{
			_isPaused = true;
			_shouldResume = false;
			final long time = System.currentTimeMillis();
			final long timeDiff = time - _threadStart;
			_threadStart = time;
			_restDelayTime -= timeDiff;
		}
	}
	
	/**
	 * resume from paused state
	 */
	public void resume()
	{
		synchronized (_thread)
		{
			_shouldResume = true;
			_thread.notifyAll();
		}
	}
	
	/**
	 * stop the thread "soft"
	 */
	public void stop()
	{
		_loopAction = false;
		if(_isWaiting)
		{
			_thread.interrupt();
		}
		if(!Thread.currentThread().equals(_thread))
		{
			try
			{
				_thread.join(500);
			}
			catch (InterruptedException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
			if(_thread.isAlive())
			{
				LOGGER.log(Level.SEVERE, "thread not terminating");
			}
		}
	}

	/**
	 * Returns if the action is repeating
	 * @return is loop action
	 */
	public boolean isLoopAction()
	{
		return _loopAction;
	}

	/**
	 * If set to true, the action is repeated
	 * 
	 * @param loopAction sets if the thread is to repeat the action
	 */
	public void setLoopAction(final boolean loopAction)
	{
		_loopAction = loopAction;
	}
}
