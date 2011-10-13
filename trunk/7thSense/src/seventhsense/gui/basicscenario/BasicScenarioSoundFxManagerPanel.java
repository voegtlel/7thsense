/*
 * BasicScenarioSoundFxManagerPanel.java
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
package seventhsense.gui.basicscenario;

import seventhsense.data.scenario.basicscenario.SoundFxManager;
import seventhsense.data.scenario.sound.SoundFxItem;
import seventhsense.gui.soundfx.SoundFxView;
import seventhsense.sound.engine.PlayerMixer;

/**
 * Panel for BasicScenarioSoundFxManager
 * 
 * @author Parallan
 *
 */
public class BasicScenarioSoundFxManagerPanel extends AbstractBasicScenarioManagerPanel<SoundFxItem, SoundFxManager>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private SoundFxManager _data;

	/**
	 * 
	 */
	public BasicScenarioSoundFxManagerPanel(final PlayerMixer mixer)
	{
		super(new BasicScenarioSoundFxTablePanel(), new SoundFxView(mixer));
	}

	/**
	 * Sets the model
	 * 
	 * @param data model
	 */
	public void setModel(final SoundFxManager data)
	{
		if (_data != data)
		{
			_data = data;
			super.setModel(data);
			setEnabled(_data != null);
		}
	}
}
