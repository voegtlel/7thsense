/*
 * SoundFxItem.java
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

import java.util.logging.Logger;

import seventhsense.data.file.FileReference;
import seventhsense.data.file.FileReferenceManager;
import seventhsense.data.scenario.ScriptItem;

/**
 * Represents a single sfx of a scenario
 * 
 * @author Drag-On, Parallan
 * 
 */
public class SoundFxItem extends AbstractSoundItem<SoundFxItem>
{
	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_FINISH_SCRIPT = "finishScript";

	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_INIT_SCRIPT = "initScript";

	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_SCRIPT_NAME = "scriptName";

	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_MAX_REPLAY_WAIT_TIME = "maxReplayWaitTime";

	/**
	 * Property constant for events
	 */
	public static final String PROPERTY_MIN_REPLAY_WAIT_TIME = "minReplayWaitTime";

	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger(SoundFxItem.class.getName());

	/**
	 * Defines the lower border of the range when the sound may be played back again
	 */
	private double _minReplayWaitTime;

	/**
	 * Defines the upper border of the range when the sound may be played back again
	 */
	private double _maxReplayWaitTime;

	/**
	 * Name of the item in scripts
	 */
	private String _scriptName;
	
	/**
	 * Script to call on initialization
	 */
	private ScriptItem _initScript;
	
	/**
	 * Script to call when finished
	 */
	private ScriptItem _finishScript;

	/**
	 * Constructor
	 */
	public SoundFxItem()
	{
		this(0.0, 0.0, 1.0, FileReferenceManager.get().getEmptyReference(), null, null, null);
	}

	/**
	 * Constructor
	 * 
	 * @param minReplayWaitTime Lower border for replay time frame
	 * @param maxReplayWaitTime Upper border for replay time frame
	 * @param volume relative volume for the sound (1.0 - 100.0)
	 * @param file sound media file
	 * @param scriptName name in scripts
	 * @param initScript initialization script (or null)
	 * @param finishScript finish script (or null)
	 */
	public SoundFxItem(final double minReplayWaitTime, final double maxReplayWaitTime, final double volume, final FileReference file,
			final String scriptName, final ScriptItem initScript, final ScriptItem finishScript)
	{
		super(FadeType.NoFade, volume, file);
		_minReplayWaitTime = minReplayWaitTime;
		_maxReplayWaitTime = maxReplayWaitTime;
		_scriptName = scriptName;
		_initScript = initScript;
		_finishScript = finishScript;
	}

	/**
	 * Gets the minimum replay wait time
	 * 
	 * @return minReplayWaitTime
	 */
	public double getMinReplayWaitTime()
	{
		return _minReplayWaitTime;
	}

	/**
	 * Sets the minimum replay wait time
	 * 
	 * @param minReplayWaitTime minReplayWaitTime to set
	 */
	public void setMinReplayWaitTime(final double minReplayWaitTime)
	{
		_minReplayWaitTime = minReplayWaitTime;
		fireChanged(PROPERTY_MIN_REPLAY_WAIT_TIME);
	}

	/**
	 * Gets the maximum replay wait time
	 * 
	 * @return maxReplayWaitTime
	 */
	public double getMaxReplayWaitTime()
	{
		return _maxReplayWaitTime;
	}

	/**
	 * Sets the maximum replay wait time
	 * 
	 * @param maxReplayWaitTime maxReplayWaitTime to set
	 */
	public void setMaxReplayWaitTime(final double maxReplayWaitTime)
	{
		_maxReplayWaitTime = maxReplayWaitTime;
		fireChanged(PROPERTY_MAX_REPLAY_WAIT_TIME);
	}

	/**
	 * Sets the name of this item in scripts
	 * 
	 * @param scriptName Script name or null
	 */
	public void setScriptName(final String scriptName)
	{
		_scriptName = scriptName;
		fireChanged(PROPERTY_SCRIPT_NAME);
	}

	/**
	 * Gets the name of this item in scripts
	 * 
	 * @return script name or null
	 */
	public String getScriptName()
	{
		return _scriptName;
	}

	/**
	 * Sets the init-script for this item
	 * 
	 * @param initScript script or null
	 */
	public void setInitScript(final ScriptItem initScript)
	{
		_initScript = initScript;
		fireChanged(PROPERTY_INIT_SCRIPT);
	}

	/**
	 * Returns the init-script for this item
	 * 
	 * @return script or null
	 */
	public ScriptItem getInitScript()
	{
		return _initScript;
	}

	/**
	 * Sets the finish-script for this item
	 * 
	 * @param finishScript script or null
	 */
	public void setFinishScript(final ScriptItem finishScript)
	{
		_finishScript = finishScript;
		fireChanged(PROPERTY_FINISH_SCRIPT);
	}

	/**
	 * Returns the finish-script for this item
	 * 
	 * @return script or null
	 */
	public ScriptItem getFinishScript()
	{
		return _finishScript;
	}

	/**
	 * Creates a clone of this object
	 * 
	 * @return cloned instance
	 */
	@Override
	public SoundFxItem deepClone()
	{
		return new SoundFxItem(_minReplayWaitTime, _maxReplayWaitTime, _volume, _file, _scriptName, (_initScript == null ? null
				: _initScript.deepClone()), (_finishScript == null ? null : _finishScript.deepClone()));
	}
}
