/*
 * IAudioStream.java
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
package seventhsense.sound.engine.input;

import java.io.IOException;

/**
 * Audio input stream interface for decoding audio files and streaming audio data.
 * 
 * @author Parallan
 *
 */
public interface IAudioInputStream
{
	/**
	 * Read decoded pcm audio data
	 * 
	 * @param buffer destination buffer
	 * @param off offset in destination buffer
	 * @param len number of bytes to read
	 * @return true number of bytes, that could be read
	 * @throws IOException on read error
	 */
	int read(byte[] buffer, int off, int len) throws IOException;
	
	/**
	 * Close the stream
	 */
	void close();
	
	/**
	 * Gets the current position in the stream
	 * 
	 * @return position in bytes
	 */
	long getPosition();
	
	/**
	 * Sets the position in the stream
	 * 
	 * @param position position in bytes
	 * @throws IOException on read error
	 */
	void setPosition(long position) throws IOException;
	
	/**
	 * Gets the length of the stream
	 * 
	 * @return length of decoded audio data in bytes
	 */
	long getLength();
	
	/**
	 * Gets the size of one sample
	 * 
	 * @return size of one sample
	 */
	int getSampleSize();
	
	/**
	 * Gets the size of one frame
	 * 
	 * @return size of one frame (sample size * channels)
	 */
	int getFrameSize();
	
	/**
	 * Gets the number of channels
	 * 
	 * @return channels
	 */
	int getChannels();

	/**
	 * Gets the sample rate
	 * 
	 * @return samplerate
	 */
	int getSampleRate();

	/**
	 * Gets the name of the codec.
	 * toString() might provide informations on the actual file.
	 * 
	 * @return name of the codec
	 */
	String getName();
}
