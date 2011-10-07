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
package seventhsense.data.scenario.sound.player;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import seventhsense.data.eventlist.EventList;
import seventhsense.data.scenario.sound.SoundException;

/**
 * This class creates a thread for playing the specified sound file.
 * The thread can't restart.
 * 
 * @author Parallan, Drag-On
 *
 */
public class SoundThread
{
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger(SoundThread.class.getName());
	
	/**
	 * File to play (required for reopening)
	 */
	private final File _file;
	
	/**
	 * Contains the source file audio format
	 */
	private final AudioFileFormat _fileFormat;	
	
	/**
	 * The output-line
	 */
	private final SourceDataLine _line;
	
	/**
	 * the source-stream
	 */
	private AudioInputStream _audioStream;
	
	/**
	 * Main thread
	 */
	private final Thread _thread;
	
	/**
	 * Lock-Object for safe threading
	 */
	private final Object _threadLock = new Object();
	
	/**
	 * Contains the current play-state (also true, when paused)
	 */
	private boolean _isRunning = false;
	
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
	private final EventList<ISoundListener<SoundThread>> _listeners = new EventList<ISoundListener<SoundThread>>();
	
	/**
	 * Contains the current position in the streamReader
	 */
	private int _sampleWritePosition;
	/**
	 * Contains the start position when the line was started with a new reader
	 */
	private long _lineFrameStartPosition;
	
	/**
	 * Creates a sound-thread for the specified sound-file.
	 * Exceptions are fired, when an error while opening occurred.
	 * 
	 * @param file file to play
	 * @param fileFormat format of the sound file
	 * @param line destination for audio data
	 * @throws SoundException
	 */
	public SoundThread(final File file, final AudioFileFormat fileFormat, final SourceDataLine line) throws SoundException
	{
		LOGGER.log(Level.FINE, "Create (" + file + ")");
		_file = file;
		_fileFormat = fileFormat;
		_line = line;

		try
		{
			// Open file
			final AudioInputStream stream = AudioSystem.getAudioInputStream(_file);
			// Create read stream
			_audioStream = AudioSystem.getAudioInputStream(_line.getFormat(), stream);
			
			// reset position
			_sampleWritePosition = 0;
			_lineFrameStartPosition = _line.getLongFramePosition();
			
			LOGGER.log(Level.FINE, "Opened Stream at " + _lineFrameStartPosition);
		}
		catch (UnsupportedAudioFileException e)
		{
			throw new SoundException(e);
		}
		catch (IOException e)
		{
			throw new SoundException(e);
		}
		
		_thread = new Thread(new Runnable()
		{
			public void run()
			{
				SoundThread.this.run();
			}
		});
	}

	/**
	 * This will be processed parallel
	 */
	private void run()
	{
		try
		{
			int numRead = 1;
			final byte[] buf = new byte[_line.getBufferSize()];
			final int sampleSize = _line.getFormat().getFrameSize();
			LOGGER.log(Level.FINE, "thread run (" + _file + ")");
			boolean firstWrite = true;
			while (numRead >= 0)
			{
				synchronized (_threadLock)
				{
					_mayInterrupt = true;
					numRead = _audioStream.read(buf, 0, buf.length);
				}
				int offset = 0;
				while (offset < numRead)
				{
					if ((_line.available() >= _line.getBufferSize()) && !firstWrite)
					{
						LOGGER.log(Level.WARNING, "Buffer Underrun: " + _line.available() + "/" + _line.getBufferSize());
					}
					
					final int written = _line.write(buf, offset, numRead - offset);
					offset += written;
					
					firstWrite = false;
					
					synchronized(_threadLock)
					{
						_sampleWritePosition += written / sampleSize;
						
						//when thread should be closed
						if (Thread.interrupted() || !_isRunning)
						{
							LOGGER.log(Level.FINER, "interrupted (1)");
							_mayInterrupt = false;
							return;
						}
						//when line has stopped playing (paused)
						if(_isPaused)
						{
							try
							{
								LOGGER.log(Level.FINER, "wait");
								_threadLock.wait();
								LOGGER.log(Level.FINER, "wait done");
							}
							catch (InterruptedException e)
							{
								LOGGER.log(Level.FINER, "interrupted (2)");
								_mayInterrupt = false;
								return;
							}
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		LOGGER.log(Level.FINER, "thread drain (" + _file + ")");
		while((_line.getLongFramePosition() - _lineFrameStartPosition) < _sampleWritePosition)
		{
			synchronized(_threadLock)
			{
				if (Thread.interrupted() || !_isRunning)
				{
					LOGGER.log(Level.FINER, "interrupted (4)");
					_mayInterrupt = false;
					return;
				}
				try
				{
					_threadLock.wait(5);
				}
				catch (InterruptedException e)
				{
					LOGGER.log(Level.FINER, "interrupted (3)");
					_mayInterrupt = false;
					return;
				}
			}
		}
		synchronized (_threadLock)
		{
			_mayInterrupt = false;
		}
		//_line.drain(); //Not working properly!
		LOGGER.log(Level.FINE, "thread finished (" + _file + ")");
		synchronized(_threadLock)
		{
			_line.stop();
			_line.flush();
		}
		fireEvent(SoundEventType.Finished);
		synchronized(_threadLock)
		{
			_isRunning = false;
			
			try
			{
				_audioStream.close();
			}
			catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
			_listeners.clear();
		}		
	}
	
	/**
	 * get current play time
	 * 
	 * @return current time in output line
	 */
	public double getTime()
	{
		return (double)(_line.getLongFramePosition() - _lineFrameStartPosition) / _line.getFormat().getFrameRate();
	}
	
	/**
	 * Gets the duration of the sound if available
	 * 
	 * @return duration if available or 0
	 */
	public double getDuration()
	{
		if (_fileFormat instanceof TAudioFileFormat)
		{
			final Map<?, ?> props = ((TAudioFileFormat) _fileFormat).properties();
			final long uSec = ((Long)props.get("duration")).longValue();
			return (double) Math.round((double)uSec * 1.0e-6);
		}
		else if(_fileFormat.getFrameLength() > 0)
		{
			return _fileFormat.getFrameLength() / _fileFormat.getFormat().getFrameRate();
		}
		else if((_audioStream != null) && (_audioStream.getFrameLength() > 0))
		{
			return _audioStream.getFrameLength() / _audioStream.getFormat().getFrameRate();
		}
		return 0;
	}
	
	/**
	 * Set time for playing
	 * 
	 * @param time time to seek to
	 * @return returns the really skipped time
	 * @throws SoundException 
	 */
	public double setTime(final double time) throws SoundException
	{
		if(_isRunning || isClosed())
		{
			throw new IllegalStateException("Can't seek playing thread");
		}
		double seekedTime;
		synchronized (_threadLock)
		{
			final int framesToSkip = (int) (time * _line.getFormat().getFrameRate());
			final int bytesToSkip = framesToSkip * _line.getFormat().getFrameSize();
			final byte[] dummyBuffer = new byte[_line.getBufferSize()]; // 1k dummy buffer
			int skippedBytes = 0;
			LOGGER.log(Level.FINE, "seek " + time + ", " + framesToSkip + " Frames, " + bytesToSkip + " bytes");
			try
			{
				while(skippedBytes < bytesToSkip)
				{
					final int bytesToRead = Math.min(dummyBuffer.length, bytesToSkip - skippedBytes);
					final int readBytes = _audioStream.read(dummyBuffer, 0, bytesToRead);
					if(readBytes < 0)
					{
						LOGGER.log(Level.FINE, "read < 0: " + readBytes);
						break;
					}
					//LOGGER.log(Level.INFO, "read: " + readBytes + " / " + bytesToRead + " (" + (bytesToSkip - skippedBytes) + ")");
					skippedBytes += readBytes;
				}
				final int skippedFrames = skippedBytes / _line.getFormat().getFrameSize();
				_lineFrameStartPosition -= skippedFrames;
				_sampleWritePosition += skippedFrames;
				seekedTime = skippedFrames / _line.getFormat().getFrameRate();
			}
			catch (IOException e)
			{
				throw new SoundException(e);
			}
		}
		fireEvent(SoundEventType.Seeked);
		LOGGER.log(Level.FINE, "skipped " + seekedTime);
		return seekedTime;
	}

	/**
	 * Starts playing
	 * 
	 * @throws SoundException 
	 */
	public void play() throws SoundException
	{
		synchronized (_threadLock)
		{
			if(_isRunning || isClosed())
			{
				throw new IllegalStateException("Can't restart closed thread");
			}
			_isRunning = true;
			_isPaused = false;
			_line.start();
			_thread.start();
		}
		fireEvent(SoundEventType.Started);
	}
	
	/**
	 * Stop playing
	 */
	public void stop()
	{
		synchronized (_threadLock)
		{
			if(!_isRunning || isClosed())
			{
				throw new IllegalStateException("Can't stop closed or unstarted thread");
			}
			
			_isRunning = false;
			_isPaused = false;
			if(_mayInterrupt)
			{
				_thread.interrupt();
			}
			_line.stop();
			_line.flush();
			_threadLock.notifyAll();
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
			LOGGER.log(Level.SEVERE, "Can't stop play thread (" + _file + ")");
		}
		fireEvent(SoundEventType.Stopped);
	}

	/**
	 * Pause playing
	 */
	public void pause()
	{
		synchronized (_threadLock)
		{
			if(!_isRunning || isClosed())
			{
				throw new IllegalStateException("Can't pause closed or unstarted thread");
			}
		}
		if(!_isPaused)
		{
			synchronized (_threadLock)
			{
				LOGGER.log(Level.FINE, "pause");
				_isPaused = true;
				_line.stop();
			}
			fireEvent(SoundEventType.Paused);
		}
	}

	/**
	 * Resume playing
	 */
	public void resume()
	{
		synchronized (_threadLock)
		{
			if(!_isRunning || isClosed())
			{
				throw new IllegalStateException("Can't resume closed or unstarted thread");
			}
		}
		if(_isPaused)
		{
			LOGGER.log(Level.FINE, "resume");
			synchronized (_threadLock)
			{
				_isPaused = false;
				_threadLock.notifyAll();
				_line.start();
			}
			fireEvent(SoundEventType.Resumed);
		}
	}
	
	/**
	 * returns the playing state
	 * 
	 * @return true, if thread is not stopped (playing or paused)
	 */
	public boolean isPlaying()
	{
		synchronized (_threadLock)
		{
			return _isRunning;
		}
	}
	
	/**
	 * returns the paused state
	 * 
	 * @return true, if thread is paused (not stopped)
	 */
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
			return !_thread.isAlive() && (_thread.getState() != State.NEW);
		}
	}
	
	/**
	 * Remove sound listener
	 * 
	 * @param listener listener
	 */
	public void removeSoundListener(final ISoundListener<SoundThread> listener)
	{
		_listeners.remove(listener);
	}

	/**
	 * Add sound listener
	 * @param listener listener
	 */
	public void addSoundListener(final ISoundListener<SoundThread> listener)
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
		for (ISoundListener<SoundThread> listener : _listeners.iterateEvents())
		{
			listener.soundEvent(this, eventType);
		}
	}
}
