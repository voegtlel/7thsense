/*
 * TimeSlider.java
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
package seventhsense.gui.timeslider;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import seventhsense.data.eventlist.EventList;

/**
 * Simple class for displaying a timer slider
 * 
 * @author Parallan
 *
 */
public class TimeSlider extends JPanel
{
	private static final Logger LOGGER = Logger.getLogger(TimeSlider.class.getName());
	
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	// GUI Components
	private final JSlider _slider;
	private final TimeSliderSpinnerModel _spinnerModel;
	private final JSpinner _spinner;
	
	private double _timeExponent = 3.5563025007672872650175335959592;
	private double _timeBase = 10.0;
	private double _timeDivider = 2.0;
	private double _timeMaximum = 60.0*30.0;
	
	/**
	 * Used for events to prevent recursion
	 */
	private boolean _isChanging = false;
	
	private boolean _maxIsInf = true;
	
	/**
	 * Listeners for value changed
	 */
	private final EventList<ChangeListener> _changedListeners = new EventList<ChangeListener>();

	/**
	 * Create the panel.
	 */
	public TimeSlider()
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
				TimeSlider.this.sliderStateChanged();
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
				TimeSlider.this.spinnerStateChanged();
			}
		});
		final TimeSliderSpinnerEditor spinnerEditor = new TimeSliderSpinnerEditor(_spinner);
		_spinner.setEditor(spinnerEditor);
		_spinnerModel = new TimeSliderSpinnerModel(spinnerEditor.getFormatter());
		_spinner.setModel(_spinnerModel);
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
	 * Sets the time range for the logarithmic scala
	 * 
	 * @param min minimum value (left) must be > 0
	 * @param max maximum value (right) must be > min
	 * @param exponent exponent to use as skala
	 */
	public void setTimeRange(final double min, final double max, final double exponent)
	{
		_timeDivider = 1.0 / min;
		_timeMaximum = max;
		_timeBase = exponent;
		_timeExponent = Math.log(_timeMaximum * _timeDivider) / Math.log(_timeBase);
	}
	
	/**
	 * Gets the time from the slider value
	 * 
	 * @return time in sec
	 */
	private double getTimeFromSlider()
	{
		if(_slider.getValue() == 0)
		{
			return 0;
		}
		else if(_maxIsInf && (_slider.getValue() == _slider.getMaximum()))
		{
			return Double.POSITIVE_INFINITY;
		}
		else
		{
			return Math.pow(_timeBase, (double)_slider.getValue() / (double)_slider.getMaximum() * _timeExponent) / _timeDivider;
		}
	}
	
	/**
	 * Sets the slider position from time
	 * 
	 * @param time time in sec
	 */
	private void setTimeToSlider(final double time)
	{
		if(time == 0)
		{
			_slider.setValue(0);
		}
		else if(time >= _timeMaximum)
		{
			_slider.setValue(_slider.getMaximum());
		}
		else
		{
			_slider.setValue((int)(Math.log(_timeDivider * time) / Math.log(_timeBase) * (double)_slider.getMaximum() / _timeExponent));
		}
	}
	
	/**
	 * Sets if the maximum of the slider should be treated as infinity
	 * 
	 * @param maxIsInf maximum of the slider should be treated as infinity
	 */
	public void setMaxInf(final boolean maxIsInf)
	{
		_maxIsInf = maxIsInf;
	}
	
	/**
	 * Returns if the maximum of the slider is treated as infinity
	 * 
	 * @return maximum of the slider is treated as infinity
	 */
	public boolean isMaxInf()
	{
		return _maxIsInf;
	}

	/**
	 * Event for slider when state was changed.
	 * Refreshes the spinner value
	 */
	private void sliderStateChanged()
	{
		if(!_isChanging)
		{
			final double newValue = getTimeFromSlider();
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
			final double newValue = _spinnerModel.getDoubleValue();
			_isChanging = true;
			setTimeToSlider(newValue);
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
		return _spinnerModel.getDoubleValue();
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
