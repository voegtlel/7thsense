/*
 * SoundFactory.java
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
package seventhsense.sound;

import java.io.File;
import java.io.IOException;

import seventhsense.sound.engine.AudioBuffer;
import seventhsense.sound.engine.AudioThread;
import seventhsense.sound.engine.GlobalVolumeFilter;
import seventhsense.sound.engine.IPlayer;
import seventhsense.sound.engine.PlayerMixer;
import seventhsense.sound.engine.SoundException;
import seventhsense.sound.engine.input.JavaSoundDecoderStream;

/**
 * Static class for creating a IPlayer from a file and a mixer
 * 
 * @author Parallan
 *
 */
public final class SoundFactory
{
	/**
	 * Empty ctor
	 */
	private SoundFactory()
	{
		// Can't instantiate
	}
	
	public static IPlayer createPlayer(final File file, final PlayerMixer mixer) throws SoundException
	{
		try
		{
			return new GlobalVolumeFilter(mixer,
					new AudioThread(
							new AudioBuffer(mixer.getAl(),
									new JavaSoundDecoderStream(file)
							)
						)
					);
		}
		catch (IOException e)
		{
			throw new SoundException(e);
		}
	}
}
