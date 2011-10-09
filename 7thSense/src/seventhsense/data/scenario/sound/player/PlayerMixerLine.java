/*
 * PlayerMixerLine.java
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import seventhsense.data.IPropertyChangedListener;
import seventhsense.data.eventlist.EventList;
import seventhsense.data.fx.ITransitionReversible;
import seventhsense.data.fx.transitions.ExpTransition;

/**
 * @author Parallan
 *
 */
public class PlayerMixerLine
{
	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_VOLUME = "volume";
	
	/**
	 * Exponent for volume calculation from linear scala
	 */
	private static final double VOLUME_EXPONENT = 10.0;
	
	/**
	 * Factor for volume calculation from linear scala
	 */
	private static final double VOLUME_MUL = 20.0;
	
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger(PlayerMixerLine.class.getName());
	
	/**
	 * Volume sounds better with an additional exponential scala
	 */
	private final ITransitionReversible _volumeFactor = new ExpTransition(10);
	
	/**
	 * The parent mixer
	 */
	private final PlayerMixer _mixer;
	
	/**
	 * The data line to manage
	 */
	private final SourceDataLine _sourceDataLine;
	
	private final FloatControl _gainControl;
	
	/**
	 * Stores current volume
	 */
	private double _volume;
	
	private final IPropertyChangedListener<PlayerMixer> _mixerListener;
	
	/**
	 * List for listeners
	 */
	private final EventList<IPropertyChangedListener<PlayerMixerLine>> _listeners = new EventList<IPropertyChangedListener<PlayerMixerLine>>();
	
	/**
	 * Create a source data line
	 * 
	 * @param mixer the controlling mixer
	 * @param sourceDataLine the managed line
	 */
	public PlayerMixerLine(final PlayerMixer mixer, final SourceDataLine sourceDataLine)
	{
		_mixer = mixer;
		_sourceDataLine = sourceDataLine;
		_gainControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
		_volume = gainToVolume(_gainControl.getValue());
		
		_mixerListener = new IPropertyChangedListener<PlayerMixer>()
		{
			@Override
			public void propertyChanged(final PlayerMixer caller, final String property)
			{
				onMixerPropertyChanged(property);
			}
		};
		
		_mixer.addListener(_mixerListener);
	}
	
	/**
	 * Event.
	 * 
	 * @param property property
	 */
	private void onMixerPropertyChanged(final String property)
	{
		if(PlayerMixer.PROPERTY_VOLUME.equals(property))
		{
			//update volume
			setVolume(getVolume());
		}
	}

	/**
	 * Converts gain to volume
	 * 
	 * @param gain gain
	 * @return volume
	 */
	private double gainToVolume(final float gain)
	{
		final double value = Math.max(Math.min(Math.pow(VOLUME_EXPONENT, gain / VOLUME_MUL), 1), 0);
		return _volumeFactor.getValueReverse(value);
	}
	
	/**
	 * Converts volume to gain
	 * 
	 * @param volume volume
	 * @return gain
	 */
	private float volumeToGain(final double volume)
	{
		final double volumeValue = Math.min(Math.max(_volumeFactor.getValue(volume), 0.0001), 1.0);
		return (float) Math.min(Math.max(Math.log(volumeValue) / Math.log(VOLUME_EXPONENT) * VOLUME_MUL, _gainControl.getMinimum()), 0);
	}
	
	/**
	 * Sets the gain from volume
	 * 
	 * @param volume volume [0, 1]
	 */
	public void setVolume(final double volume)
	{
		_volume = Math.min(Math.max(0, volume), 1);
		_gainControl.setValue(volumeToGain(volume * _mixer.getVolume()));
		LOGGER.log(Level.FINEST, "set volume to " + (volume*100) + "%");
		fireChanged(PROPERTY_VOLUME);
	}
	
	/**
	 * Gets the volume from gain
	 * 
	 * @return volume [0, 1]
	 */
	public double getVolume()
	{
		return _volume;
	}
	
	/**
	 * Add a listener
	 * 
	 * @param listener listener
	 */
	public void addListener(final IPropertyChangedListener<PlayerMixerLine> listener)
	{
		_listeners.add(listener);
	}
	
	/**
	 * Remove a listener
	 * 
	 * @param listener listener
	 */
	public void removeListener(final IPropertyChangedListener<PlayerMixerLine> listener)
	{
		_listeners.remove(listener);
	}
	
	
	/**
	 * Fires the changed event.
	 * 
	 * @param property property that changed
	 */
	private void fireChanged(final String property)
	{
		for(IPropertyChangedListener<PlayerMixerLine> listener : _listeners.iterateEvents())
		{
			listener.propertyChanged(this, property);
		}
	}

	/**
	 * 
	 * @see javax.sound.sampled.DataLine#drain()
	 */
	public void drain()
	{
		_sourceDataLine.drain();
	}

	/**
	 * 
	 * @see javax.sound.sampled.DataLine#flush()
	 */
	public void flush()
	{
		_sourceDataLine.flush();
	}

	/**
	 * 
	 * @see javax.sound.sampled.DataLine#start()
	 */
	public void start()
	{
		_sourceDataLine.start();
	}

	/**
	 * 
	 * @see javax.sound.sampled.DataLine#stop()
	 */
	public void stop()
	{
		_sourceDataLine.stop();
	}

	/**
	 * 
	 * @see javax.sound.sampled.Line#close()
	 */
	public void close()
	{
		_sourceDataLine.close();
		_mixer.removeListener(_mixerListener);
	}

	/**
	 * @return
	 * @see javax.sound.sampled.DataLine#isRunning()
	 */
	public boolean isRunning()
	{
		return _sourceDataLine.isRunning();
	}

	/**
	 * @return
	 * @see javax.sound.sampled.DataLine#getBufferSize()
	 */
	public int getBufferSize()
	{
		return _sourceDataLine.getBufferSize();
	}

	/**
	 * @return
	 * @see javax.sound.sampled.DataLine#available()
	 */
	public int available()
	{
		return _sourceDataLine.available();
	}

	/**
	 * @return
	 * @see javax.sound.sampled.DataLine#getFramePosition()
	 */
	public int getFramePosition()
	{
		return _sourceDataLine.getFramePosition();
	}

	/**
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @see javax.sound.sampled.SourceDataLine#write(byte[], int, int)
	 */
	public int write(final byte[] b, final int off, final int len)
	{
		return _sourceDataLine.write(b, off, len);
	}

	/**
	 * @return
	 * @see javax.sound.sampled.DataLine#getLongFramePosition()
	 */
	public long getLongFramePosition()
	{
		return _sourceDataLine.getLongFramePosition();
	}

	/**
	 * @return
	 * @see javax.sound.sampled.DataLine#getFormat()
	 */
	public AudioFormat getFormat()
	{
		return _sourceDataLine.getFormat();
	}
}
