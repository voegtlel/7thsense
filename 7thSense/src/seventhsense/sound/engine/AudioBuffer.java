/*
 * AudioBuffer.java
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
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import seventhsense.data.fx.ITransitionReversible;
import seventhsense.data.fx.transitions.ExpTransition;
import seventhsense.sound.engine.input.IAudioInputStream;

import com.jogamp.openal.AL;

/**
 * @author Parallan
 *
 */
public class AudioBuffer
{
	private static final Logger LOGGER = Logger.getLogger(AudioBuffer.class.getName());
	
	private static enum BufferState
	{
		Normal,
		Truncated,
		Empty
	}
	
	private final AL _al;
	
	private final int[] _buffers = new int[4];
	private final int _source;
	
	private final int _format;
	
	private final byte[] _pcmBuffer;

	private final IAudioInputStream _sourceStream;
	
	private boolean _holdPlaying;
	
	private final int[] _intBuffer = new int[1];
	private final float[] _floatBuffer = new float[1];
	
	private boolean _isPlaying = false;
	private boolean _isPaused = false;
	
	private int _lastBufferSize = 0;
	
	private final ITransitionReversible _volumeFactor = new ExpTransition(20.0);
	
	/**
	 * Creates an audio buffer for draining and pushing data
	 */
	public AudioBuffer(final IAudioInputStream sourceStream)
	{
		LOGGER.log(Level.FINE, "create AudioBuffer");
		
		_al = PlayerMixer.get().getAl();
		_sourceStream = sourceStream;
		
		_al.alGenBuffers(_buffers.length, _buffers, 0);
		AlUtil.checkError(_al);
		
		_al.alGenSources(1, _intBuffer, 0);
		AlUtil.checkError(_al);
		_source = _intBuffer[0];
		
		/*_al.alSource3f(_source, AL.AL_POSITION, 0.0f, 0.0f, 0.0f);
		_al.alSource3f(_source, AL.AL_VELOCITY, 0.0f, 0.0f, 0.0f);
		_al.alSource3f(_source, AL.AL_DIRECTION, 0.0f, 0.0f, 0.0f);
		_al.alSourcef(_source, AL.AL_ROLLOFF_FACTOR, 0.0f);
		_al.alSourcei(_source, AL.AL_SOURCE_RELATIVE, AL.AL_TRUE);*/
		
		if((sourceStream.getSampleSize() == 1) && (sourceStream.getChannels() == 1))
		{
			_format = AL.AL_FORMAT_MONO8;
			LOGGER.log(Level.FINE, "Mono 8");
		}
		else if((sourceStream.getSampleSize() == 1) && (sourceStream.getChannels() == 2))
		{
			_format = AL.AL_FORMAT_STEREO8;
			LOGGER.log(Level.FINE, "Stereo 8");
		}
		else if((sourceStream.getSampleSize() == 2) && (sourceStream.getChannels() == 1))
		{
			_format = AL.AL_FORMAT_MONO16;
			LOGGER.log(Level.FINE, "Mono 16");
		}
		else if((sourceStream.getSampleSize() == 2) && (sourceStream.getChannels() == 2))
		{
			_format = AL.AL_FORMAT_STEREO16;
			LOGGER.log(Level.FINE, "Stereo 16");
		}
		else
		{
			throw new IllegalArgumentException("Invalid source stream format");
		}
		
		_pcmBuffer = new byte[_sourceStream.getSampleRate() * _sourceStream.getFrameSize()]; // 1 sec buffer
		
		LOGGER.log(Level.FINE, "AudioBuffer created");
	}
	
	public boolean isPlaying()
	{
		//final int state = getSourceState();
		//return (state == AL.AL_PLAYING) || (state == AL.AL_PAUSED);
		return _isPlaying;
	}
	
	public boolean isPaused()
	{
		//return getSourceState() == AL.AL_PAUSED;
		return _isPaused;
	}
	
	/**
	 * Start/Restart playback
	 * @throws IOException 
	 */
	public void play() throws IOException
	{
		stop();
		
		fillBuffers();
		
		_al.alSourcePlay(_source);
		AlUtil.checkError(_al);
		_holdPlaying = true;
		_isPlaying = true;
		_isPaused = false;
	}
	
	/**
	 * Stop playback and reset source
	 */
	public void stop()
	{
		_al.alSourceStop(_source);
		AlUtil.checkError(_al);
		_holdPlaying = false;
		_isPlaying = false;
		_isPaused = false;
		// Reset stream
		try
		{
			_sourceStream.setPosition(0);
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		// Reset buffers
		flushBuffers();
	}
	
	/**
	 * Resume or start playback
	 * @throws IOException 
	 */
	public void resume() throws IOException
	{
		if(!_isPlaying)
		{
			play();
		}
		else if(_isPaused)
		{
			_isPaused = false;
			_al.alSourcePlay(_source);
			AlUtil.checkError(_al);
			_holdPlaying = true;
		}
	}
	
	/**
	 * Pause playback
	 */
	public void pause()
	{
		if(_isPlaying && !_isPaused)
		{
			_holdPlaying = false;
			_isPaused = true;
			_al.alSourcePause(_source);
		}
	}
	
	/**
	 * Gets the current playing position
	 * 
	 * @return current position in bytes
	 */
	public long getPosition()
	{
		// Playing has just finished!
		if(getSourceState() == AL.AL_STOPPED)
		{
			return _sourceStream.getPosition();
		}
		// Is playing or stopped
		final int buffersQueued = getBuffersQueued();
		final int queuedBuffersSize = (buffersQueued > 0?(buffersQueued - 1) * _pcmBuffer.length + _lastBufferSize:0);
		final int byteOffset = getByteOffset();
		LOGGER.log(Level.FINEST, "queued: " + queuedBuffersSize + ", source: " + _sourceStream.getPosition() + ", play: " + byteOffset);
		return byteOffset + _sourceStream.getPosition() - queuedBuffersSize;
	}
	
	/**
	 * Sets the current playing position. Sets the state to playing.
	 * 
	 * @param position current position in bytes
	 * @throws IOException 
	 */
	public void setPosition(final long position) throws IOException
	{
		boolean playAfter = !_isPaused && _isPlaying;
		
		// At first stop it
		_holdPlaying = false;
		_isPaused = true;
		_al.alSourceStop(_source);
		AlUtil.checkError(_al);
		// flush buffers
		flushBuffers();
		
		// Now seek
		_sourceStream.setPosition(position);
		// fill buffers
		fillBuffers();
		_isPlaying = true;
		if(playAfter)
		{
			// start playing again
			resume();
		}
	}
	
	/**
	 * Gets the stream length in bytes
	 * 
	 * @return stream length in bytes
	 */
	public long getLength()
	{
		return _sourceStream.getLength();
	}
	
	/**
	 * Gets the stream length in seconds
	 * 
	 * @return stream length in seconds
	 */
	public double getDuration()
	{
		return (double)getLength() / (_sourceStream.getFrameSize() * _sourceStream.getSampleRate());
	}
	
	/**
	 * Gets the stream time in seconds
	 * 
	 * @return stream time in seconds
	 */
	public double getTime()
	{
		return (double)getPosition() / (_sourceStream.getFrameSize() * _sourceStream.getSampleRate());
	}
	
	/**
	 * Sets the stream time in seconds
	 * 
	 * @param time stream time in seconds
	 * @throws IOException 
	 */
	public void setTime(final double time) throws IOException
	{
		long bytePosition = (long) (time * _sourceStream.getFrameSize() * _sourceStream.getSampleRate());
		setPosition(bytePosition - (bytePosition % _sourceStream.getFrameSize()));
	}
	
	/**
	 * Sets the volume
	 * 
	 * @param volume volume
	 */
	public void setVolume(final double volume)
	{
		_al.alSourcef(_source, AL.AL_GAIN, (float) _volumeFactor.getValue(volume));
		AlUtil.checkError(_al);
	}
	
	/**
	 * Gets the volume
	 * 
	 * @return volume
	 */
	public double getVolume()
	{
		_al.alGetSourcef(_source, AL.AL_GAIN, _floatBuffer, 0);
		return _volumeFactor.getValueReverse(_floatBuffer[0]);
	}
	
	/**
	 * Gets byte offset
	 * 
	 * @return byte offset
	 */
	private int getByteOffset()
	{
		_al.alGetSourcei(_source, AL.AL_BYTE_OFFSET, _intBuffer, 0);
		return _intBuffer[0];
	}
	
	/**
	 * Gets buffers queued
	 * 
	 * @return buffers queued
	 */
	private int getBuffersQueued()
	{
		_al.alGetSourcei(_source, AL.AL_BUFFERS_QUEUED, _intBuffer, 0);
		return _intBuffer[0];
	}
	
	/**
	 * Gets buffers processed
	 * 
	 * @return buffers processed
	 */
	private int getBuffersProcessed()
	{
		_al.alGetSourcei(_source, AL.AL_BUFFERS_PROCESSED, _intBuffer, 0);
		return _intBuffer[0];
	}
	
	/**
	 * Gets source state
	 * 
	 * @return source state
	 */
	private int getSourceState()
	{
		_al.alGetSourcei(_source, AL.AL_SOURCE_STATE, _intBuffer, 0);
		return _intBuffer[0];
	}
	
	/**
	 * Fills the given buffer with data from the source stream and queues it to the audio source if it is not empty.
	 * 
	 * @param buffer buffer to fill
	 * @return buffer state
	 * @throws IOException
	 */
	private BufferState fillBuffer(final int buffer) throws IOException
	{
		int size = 0;
		while(size < _pcmBuffer.length)
		{
			final int result = _sourceStream.read(_pcmBuffer, size, _pcmBuffer.length - size);
			if(result < 0)
			{
				break;
			}
			size += result;
		}
		
		LOGGER.log(Level.FINEST, "read " + size + "/" + _pcmBuffer.length);
		
		if(size <= 0)
		{
			return BufferState.Empty;
		}
		
		_al.alBufferData(buffer, _format, ByteBuffer.wrap(_pcmBuffer), size, _sourceStream.getSampleRate());
		AlUtil.checkError(_al);
		
		_intBuffer[0] = buffer;
		_al.alSourceQueueBuffers(_source, 1, _intBuffer, 0);
		AlUtil.checkError(_al);
		
		_lastBufferSize = size;
		
		return (size == _pcmBuffer.length)?BufferState.Normal:BufferState.Truncated;
	}
	
	/**
	 * Updates the buffer data by draining from the source and pushing to the audio buffer
	 * 
	 * @return true, if more data is available or the buffers were not cleared, false if all buffers have finished
	 * @throws IOException
	 */
	public boolean update() throws IOException
	{
		final int buffersProcessed = getBuffersProcessed();
		
		final int[] buffer = new int[1];
		
		for(int i = 0; i < buffersProcessed; i++)
		{
			buffer[0] = 0;
			
			_al.alSourceUnqueueBuffers(_source, 1, buffer, 0);
			AlUtil.checkError(_al);
			
			final BufferState bufferState = fillBuffer(buffer[0]);
			
			LOGGER.log(Level.FINER, "Update " + buffer[0] + " -> " + bufferState);
			
			if(bufferState != BufferState.Normal)
			{
				break;
			}
		}
		
		final int buffersQueued = getBuffersQueued();
		
		if((buffersQueued == 0) && _holdPlaying)
		{
			LOGGER.log(Level.FINE, "sound finish");
			
			_isPlaying = false;
			_isPaused = false;
			_holdPlaying = false;
			_lastBufferSize = 0;
			if(getSourceState() == AL.AL_PLAYING)
			{
				_al.alSourceStop(_source);
			}
			return false;
		}
		
		if(_holdPlaying && (getSourceState() != AL.AL_PLAYING))
		{
			LOGGER.log(Level.WARNING, "Buffer Underrun: Hold Play");
			_al.alSourcePlay(_source);
		}
		
		return true;
	}
	
	/**
	 * Initially fills the buffers
	 * 
	 * @throws IOException
	 */
	private void fillBuffers() throws IOException
	{
		LOGGER.log(Level.FINER, "fill " + _buffers.length + " buffers");
		for(int i = 0; i < _buffers.length; i++)
		{
			final BufferState bufferState = fillBuffer(_buffers[i]);
			if(bufferState != BufferState.Normal)
			{
				break;
			}
		}
	}
	
	/**
	 * Finally removes the buffers
	 */
	private void flushBuffers()
	{
		final int queued = getBuffersQueued();
		LOGGER.log(Level.FINER, "flush " + queued + " buffers");
		for(int i = 0; i < queued; i++)
		{
			_intBuffer[0] = 0;
			_al.alSourceUnqueueBuffers(_source, 1, _intBuffer, 0);
			AlUtil.checkError(_al);
		}
		_lastBufferSize = 0;
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		flushBuffers();
		_al.alDeleteBuffers(_buffers.length, _buffers, 0);
		_al.alDeleteSources(1, new int[]{_source}, 0);
		AlUtil.checkError(_al);
		super.finalize();
	}
}
