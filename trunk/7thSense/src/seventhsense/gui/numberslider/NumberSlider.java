/*
 * NumberSlider.java
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
package seventhsense.gui.numberslider;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import seventhsense.data.eventlist.EventList;

/**
 * Panel with a synchronized slider and spinner
 * 
 * @author Parallan
 *
 */
public class NumberSlider extends JPanel
{
	private static final Logger LOGGER = Logger.getLogger(NumberSlider.class.getName());
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	// GUI Components
	private final JSlider _slider;
	private final JSpinner _spinner;
	private final SpinnerNumberModel _spinnerModel;
	
	/**
	 * Used for events to prevent recursion
	 */
	private boolean _isChanging = false;
	
	/**
	 * Listeners for value changed
	 */
	private final EventList<ChangeListener> _changedListeners = new EventList<ChangeListener>();

	/**
	 * Create the panel.
	 */
	public NumberSlider()
	{
		super();
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		_slider = new JSlider();
		_slider.setMinimum(0);
		_slider.setMaximum(10000);
		_slider.setValue(0);
		_slider.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent event) {
				NumberSlider.this.sliderStateChanged();
			}
		});
		final GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.insets = new Insets(0, 0, 0, 5);
		gbc_slider.gridx = 0;
		gbc_slider.gridy = 0;
		add(_slider, gbc_slider);
		
		_spinner = new JSpinner();
		_spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent event) {
				NumberSlider.this.spinnerStateChanged();
			}
		});
		_spinnerModel = new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0);
		_spinner.setModel(_spinnerModel);
		_spinner.setLocale(Locale.ROOT);
		//spinner.setEditor(new JSpinner.NumberEditor(spinner, ""));
		final GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 0;
		add(_spinner, gbc_spinner);
	}
	
	@Override
	public void setEnabled(final boolean enabled)
	{
		super.setEnabled(enabled);
		_slider.setEnabled(enabled);
		_spinner.setEnabled(enabled);
	}
	
	/**
	 * Sets the time range for the spinner and slider
	 * 
	 * @param min minimum value (left)
	 * @param max maximum value (right) must be > min
	 * @param step step used for the spinner
	 */
	public void setRange(final double min, final double max, final double step)
	{
		_spinnerModel.setMinimum(min);
		_spinnerModel.setMaximum(max);
		_spinnerModel.setStepSize(step);
		setValueToSlider(_spinnerModel.getNumber().doubleValue());
	}
	
	/**
	 * Gets the time from the slider value
	 * 
	 * @return time in sec
	 */
	private double getValueFromSlider()
	{
		return (Double)_spinnerModel.getMinimum() + ((double)_slider.getValue() / (double)_slider.getMaximum() * ((Double)_spinnerModel.getMaximum() - (Double)_spinnerModel.getMinimum()));
	}
	
	/**
	 * Sets the slider position from value
	 * 
	 * @param value value to set
	 */
	private void setValueToSlider(final double value)
	{
		_slider.setValue((int)((value - (Double)_spinnerModel.getMinimum()) * (double)_slider.getMaximum() / ((Double)_spinnerModel.getMaximum() - (Double)_spinnerModel.getMinimum())));
	}

	/**
	 * Event for slider when state was changed.
	 * Refreshes the spinner value
	 */
	private void sliderStateChanged()
	{
		if(!_isChanging)
		{
			final double newValue = getValueFromSlider();
			_isChanging = true;
			_spinnerModel.setValue(newValue);
			_isChanging = false;
			LOGGER.log(Level.FINER, "sliderStateChanged: " + newValue);
			fireChangeListener();
		}
	}
	
	/**
	 * Event for spinner when state was changed.
	 * Refreshes the slider value
	 */
	private void spinnerStateChanged()
	{
		if(!_isChanging)
		{
			final double newValue = _spinnerModel.getNumber().doubleValue();
			_isChanging = true;
			setValueToSlider(newValue);
			_isChanging = false;
			LOGGER.log(Level.FINER, "spinnerStateChanged: " + newValue);
			fireChangeListener();
		}
	}
	
	/**
	 * Gets the selected time in seconds
	 * 
	 * @return selected time in seconds
	 */
	public double getValue()
	{
		return _spinnerModel.getNumber().doubleValue();
	}
	
	/**
	 * Sets the selected time in seconds
	 * 
	 * @param value selected time in seconds
	 */
	public void setValue(final double value)
	{
		_spinnerModel.setValue(value);
	}
	
	/**
	 * Adds a ChangeListener
	 * 
	 * @param listener listener to add
	 */
	public void addChangeListener(final ChangeListener listener)
	{
		_changedListeners.add(listener);
	}
	
	/**
	 * Removes a ChangeListener
	 * 
	 * @param listener listener to remove
	 */
	public void removeChangeListener(final ChangeListener listener)
	{
		_changedListeners.remove(listener);
	}
	
	/**
	 * Fires the change event
	 */
	protected void fireChangeListener()
	{
		for(ChangeListener listener : _changedListeners.iterateEvents())
		{
			listener.stateChanged(new ChangeEvent(this));
		}
	}
	
	@Override
	public void setToolTipText(final String text)
	{
		super.setToolTipText(text);
		_slider.setToolTipText(text);
		_spinner.setToolTipText(text);
	}
}
