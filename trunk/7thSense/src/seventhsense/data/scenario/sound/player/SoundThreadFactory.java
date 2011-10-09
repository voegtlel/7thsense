/*
 * SoundThreadFactory.java
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import seventhsense.data.scenario.sound.SoundException;

/**
 * Factory class for creating a sound player for a sound file
 * 
 * @author Parallan
 *
 */
public class SoundThreadFactory
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
	 * Mixer used for creating the line
	 */
	private final PlayerMixer _mixer;

	/**
	 * Format for the line
	 */
	private final AudioFormat _decodedFormat;

	/**
	 * Format of the input file
	 */
	private final AudioFileFormat _fileFormat;

	/**
	 * Size of the audio buffer
	 */
	private final int _bufferSize;
	
	/**
	 * Creates a sound-thread for the specified sound-file.
	 * Exceptions are fired, when an error while opening occurred.
	 * 
	 * @param file file to load
	 * @param mixer mixer to use
	 * @throws SoundException
	 */
	public SoundThreadFactory(final File file, final PlayerMixer mixer) throws SoundException
	{
		LOGGER.log(Level.FINE, "Create (" + file + ")");
		_file = file;
		_mixer = mixer;

		try
		{
			_fileFormat = AudioSystem.getAudioFileFormat(_file);
			final AudioFormat baseFormat = _fileFormat.getFormat();
			LOGGER.log(Level.FINE, "Source Format: " + baseFormat);
			int sampleSize;
			if(baseFormat.getSampleSizeInBits() == 8)
			{
				// Only if 8 is specified, use 8
				sampleSize = 8;
			}
			else// if((baseFormat.getSampleSizeInBits() == AudioSystem.NOT_SPECIFIED) || (baseFormat.getSampleSizeInBits() == 16))
			{
				sampleSize = 16;
			}
			_decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, // Encoding to use
					baseFormat.getSampleRate(), // sample rate (same as base format)
					sampleSize, // sample size in bits
					baseFormat.getChannels(), // # of Channels
					baseFormat.getChannels() * sampleSize / 8, // Frame Size
					baseFormat.getSampleRate(), // Frame Rate
					baseFormat.isBigEndian() // Big Endian
			);
			LOGGER.log(Level.FINE, "Play Format: " + _decodedFormat);

			_bufferSize = (int)(_decodedFormat.getFrameRate() * _decodedFormat.getFrameSize() * 4); // 4 sec buffer
			LOGGER.log(Level.FINE, "Buffer size: " + _bufferSize + " bytes");
		}
		catch (UnsupportedAudioFileException e)
		{
			throw new SoundException(e);
		}
		catch (IOException e)
		{
			throw new SoundException(e);
		}
	}
	
	/**
	 * Creates a separated player for the file
	 * 
	 * @return a new player
	 * @throws SoundException
	 */
	public IPlayer createFilePlayer() throws SoundException
	{
		return new SoundFile(_file, _fileFormat, _mixer.createLine(_decodedFormat, _bufferSize));
	}
	
	/**
	 * Creates a separated player for the file with fading
	 * 
	 * @return a new player
	 * @throws SoundException
	 */
	public IPlayerFade createFilePlayerFade() throws SoundException
	{
		return new SoundFadeFile(new SoundFile(_file, _fileFormat, _mixer.createLine(_decodedFormat, _bufferSize)));
	}
}
