/*
 * SoundThread.java
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
package seventhsense.sound.engine;

import java.io.IOException;
import java.lang.Thread.State;
import java.util.logging.Level;
import java.util.logging.Logger;

import seventhsense.data.eventlist.EventList;

/**
 * This class creates a thread for playing the specified sound file.
 * The thread can't restart.
 * 
 * @author Parallan, Drag-On
 *
 */
public class AudioThread implements IPlayer
{
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger(AudioThread.class.getName());
	
	/**
	 * The output-line
	 */
	private final AudioBuffer _playBuffer;
	
	/**
	 * Main thread
	 */
	private final Thread _thread;
	
	/**
	 * Lock-Object for safe threading
	 */
	private final Object _threadLock = new Object();
	
	/**
	 * Contains the current thread-state
	 */
	private boolean _isRunning = false;
	
	/**
	 * Contains the current play-state (also true, when paused)
	 */
	private boolean _isPlaying = false;
	
	/**
	 * Contains the paused state (can only be true, if not stopped)
	 */
	private boolean _isPaused = false;
	
	/**
	 * True, if the thread is in a state where interrupting is possible
	 */
	private boolean _mayInterrupt = false;
	
	/**
	 * Listeners
	 */
	private final EventList<ISoundListener<IPlayer>> _listeners = new EventList<ISoundListener<IPlayer>>();
	
	/**
	 * Creates a sound-thread for the specified sound-file.
	 * Exceptions are fired, when an error while opening occurred.
	 * 
	 * @param file file to play
	 * @param fileFormat format of the sound file
	 * @param line destination for audio data
	 * @throws SoundException
	 */
	public AudioThread(final AudioBuffer playBuffer) throws SoundException
	{
		LOGGER.log(Level.FINE, "Create (" + playBuffer + ")");
		_playBuffer = playBuffer;
		
		_thread = new Thread(new Runnable()
		{
			public void run()
			{
				AudioThread.this.run();
			}
		});
		
		startThread();
	}
	
	/**
	 * Starts the audio thread
	 */
	private void startThread()
	{
		_isRunning = true;
		_thread.start();
		LOGGER.log(Level.FINE, "thread started");
	}
	
	/**
	 * Starts the audio thread
	 */
	private void stopThread()
	{
		synchronized (_threadLock)
		{
			LOGGER.log(Level.FINE, "stop thread");
			if(!isClosed())
			{
				LOGGER.log(Level.FINER, "stopping thread");
				_isRunning = false;
				_isPlaying = false;
				_isPaused = false;
				if(_mayInterrupt)
				{
					LOGGER.log(Level.FINER, "interrupt thread");
					_thread.interrupt();
				}
				_playBuffer.stop();
				_threadLock.notifyAll();
			}
		}
		try
		{
			_thread.join(500);
		}
		catch (InterruptedException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		if (_thread.isAlive())
		{
			LOGGER.log(Level.SEVERE, "Can't stop play thread (" + _playBuffer + ")");
		}
		LOGGER.log(Level.FINE, "thread closed");
		
		fireEvent(SoundEventType.Closed);
	}

	/**
	 * The method for draining and filling the audio buffer
	 */
	private void run()
	{
		try
		{
			LOGGER.log(Level.FINE, "thread run (" + _playBuffer + ")");
			boolean isRunning = true;
			while(isRunning && !Thread.interrupted())
			{
				boolean isFinished;
				synchronized (_threadLock)
				{
					// Perform the buffer update and drain data
					isFinished = !_playBuffer.update();
					if(isFinished)
					{
						_isPlaying = false;
						_isPaused = false;
					}
				}
				if(isFinished)
				{
					LOGGER.log(Level.FINE, "playing finished (" + _playBuffer + ")");
					fireEvent(SoundEventType.Finished);
				}
				fireEvent(SoundEventType.Update);
				synchronized (_threadLock)
				{
					// Check state before delay
					if(!_isRunning)
					{
						isRunning = _isRunning;
						break;
					}
					
					// Determine delay
					int threadDelay = 20;
					if(_isPaused || !_isPlaying)
					{
						//Save performance: Pause thread for infinite
						threadDelay = 0;
					}
					// Perform delay
					_mayInterrupt = true;
					try
					{
						if(threadDelay == 0)
						{
							LOGGER.log(Level.FINER, "wait begin: " + _isPaused + " " + _isPlaying);
						}
						_threadLock.wait(threadDelay);
						if(threadDelay == 0)
						{
							LOGGER.log(Level.FINER, "wait end");
						}
					}
					catch (InterruptedException e)
					{
						_isRunning = false;
					}
					_mayInterrupt = false;
					
					//update running state
					isRunning = _isRunning;
				}
			}
			synchronized(_threadLock)
			{
				//_listeners.clear();
				_isRunning = false;
			}
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		LOGGER.log(Level.FINE, "thread finished");
	}
	
	@Override
	public double getTime()
	{
		synchronized (_threadLock)
		{
			return _playBuffer.getTime();
		}
	}
	
	@Override
	public double getDuration()
	{
		synchronized (_threadLock)
		{
			return _playBuffer.getDuration();
		}
	}

	@Override
	public void setTime(final double time) throws SoundException
	{
		synchronized (_threadLock)
		{
			checkThread();
			try
			{
				_playBuffer.setTime(time);
			}
			catch (IOException e)
			{
				throw new SoundException(e);
			}
		}
	}
	
	@Override
	public void setVolume(final double volume)
	{
		synchronized (_threadLock)
		{
			checkThread();
			_playBuffer.setVolume(volume);
		}
	}
	
	@Override
	public double getVolume()
	{
		synchronized (_threadLock)
		{
			checkThread();
			return _playBuffer.getVolume();
		}
	}

	@Override
	public void play() throws SoundException
	{
		synchronized (_threadLock)
		{
			checkThread();
			LOGGER.log(Level.FINE, "play");
			_isPlaying = true;
			_isPaused = false;
			_threadLock.notifyAll();
			try
			{
				_playBuffer.play();
			}
			catch (IOException e)
			{
				throw new SoundException(e);
			}
		}
		fireEvent(SoundEventType.Started);
	}
	
	@Override
	public void stop()
	{
		synchronized (_threadLock)
		{
			checkThread();
			LOGGER.log(Level.FINE, "stop");
			_isPlaying = false;
			_isPaused = false;
			_playBuffer.stop();
		}
	}

	@Override
	public void pause()
	{
		synchronized (_threadLock)
		{
			checkThread();
			if(_isPlaying)
			{
				LOGGER.log(Level.FINE, "pause");
				_isPaused = true;
				_playBuffer.pause();
			}
		}
		fireEvent(SoundEventType.Paused);
	}

	@Override
	public void resume() throws SoundException
	{
		synchronized (_threadLock)
		{
			checkThread();
			LOGGER.log(Level.FINE, "resume");
			_isPlaying = true;
			_isPaused = false;
			_threadLock.notifyAll();
			try
			{
				_playBuffer.resume();
			}
			catch (IOException e)
			{
				throw new SoundException(e);
			}
		}
		fireEvent(SoundEventType.Resumed);
	}
	
	@Override
	public boolean isPlaying()
	{
		synchronized (_threadLock)
		{
			return _isPlaying;
		}
	}
	
	@Override
	public boolean isPaused()
	{
		synchronized (_threadLock)
		{
			return _isPaused;
		}
	}
	
	/**
	 * Returns true, if the thread has finished and this class cannot be reused
	 * 
	 * @return closed state
	 */
	public boolean isClosed()
	{
		synchronized (_threadLock)
		{
			return !_isRunning || (!_thread.isAlive() && (_thread.getState() != State.NEW));
		}
	}
	
	/**
	 * Checks the threads state and throws an exception if it is not running
	 */
	private void checkThread()
	{
		if(isClosed())
		{
			throw new IllegalStateException("Audio thread was closed");
		}
	}
	
	@Override
	public void removeSoundListener(final ISoundListener<IPlayer> listener)
	{
		_listeners.remove(listener);
	}

	@Override
	public void addSoundListener(final ISoundListener<IPlayer> listener)
	{
		_listeners.add(listener);
	}

	/**
	 * Fire sound event
	 * 
	 * @param eventType event type
	 */
	private void fireEvent(final SoundEventType eventType)
	{
		for (ISoundListener<IPlayer> listener : _listeners.iterateEvents())
		{
			listener.soundEvent(this, eventType);
		}
	}

	@Override
	public void close()
	{
		stopThread();
	}
}
