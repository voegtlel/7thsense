/*
 * SoundFxScriptingPanel.java
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
package seventhsense.gui.soundfx;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import seventhsense.data.scenario.ScriptItem;
import seventhsense.data.scenario.sound.ISoundItemListener;
import seventhsense.data.scenario.sound.SoundFxItem;
import seventhsense.data.scenario.sound.player.SoundEventType;
import javax.swing.ImageIcon;

/**
 * Panel for editing scripts
 * 
 * @author Parallan
 *
 */
public class SoundFxScriptingPanel extends JPanel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel _panelScriptingGeneral;
	private final JTextArea _textAreaFinishScript;
	private final JTextArea _textAreaInitScript;
	private final JTextField _textFieldScriptName;

	private SoundFxItem _data;
	private transient final ISoundItemListener<SoundFxItem> _dataListener;

	/**
	 * 
	 */
	private boolean _performChangeEvents = true;

	/**
	 * Create the panel.
	 */
	public SoundFxScriptingPanel()
	{
		super();

		_dataListener = new ISoundItemListener<SoundFxItem>()
		{
			@Override
			public void changed(final SoundFxItem item, final String property)
			{
				SoundFxScriptingPanel.this.dataChanged(property);
			}

			@Override
			public void soundEvent(final SoundFxItem soundFxItem, final SoundEventType event)
			{
				// TODO Auto-generated method stub
				
			}
		};

		final GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[] { 0, 0 };
		gbl.rowHeights = new int[] { 0, 0, 0 };
		gbl.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		this.setLayout(gbl);

		_panelScriptingGeneral = new JPanel();
		_panelScriptingGeneral.setEnabled(false);
		_panelScriptingGeneral.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_panelScriptingGeneral = new GridBagConstraints();
		gbc_panelScriptingGeneral.insets = new Insets(0, 0, 5, 0);
		gbc_panelScriptingGeneral.fill = GridBagConstraints.BOTH;
		gbc_panelScriptingGeneral.gridx = 0;
		gbc_panelScriptingGeneral.gridy = 0;
		this.add(_panelScriptingGeneral, gbc_panelScriptingGeneral);
		final GridBagLayout gbl_panelScriptingGeneral = new GridBagLayout();
		gbl_panelScriptingGeneral.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelScriptingGeneral.rowHeights = new int[] { 0, 0 };
		gbl_panelScriptingGeneral.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelScriptingGeneral.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		_panelScriptingGeneral.setLayout(gbl_panelScriptingGeneral);

		final JLabel labelScriptingName = new JLabel("");
		labelScriptingName.setToolTipText("Javascript variable name for this sound for referencing in sibling and this scripts");
		labelScriptingName.setIcon(new ImageIcon(SoundFxScriptingPanel.class.getResource("/seventhsense/resources/ScriptName_20.png")));
		labelScriptingName.setEnabled(false);
		final GridBagConstraints gbc_labelScriptingName = new GridBagConstraints();
		gbc_labelScriptingName.insets = new Insets(0, 0, 0, 5);
		gbc_labelScriptingName.anchor = GridBagConstraints.EAST;
		gbc_labelScriptingName.gridx = 0;
		gbc_labelScriptingName.gridy = 0;
		_panelScriptingGeneral.add(labelScriptingName, gbc_labelScriptingName);

		_textFieldScriptName = new JTextField();
		_textFieldScriptName.setEnabled(false);
		_textFieldScriptName.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(final FocusEvent e)
			{
				SoundFxScriptingPanel.this.scriptNameActionPerformed();
			}
		});
		_textFieldScriptName.setInputVerifier(new InputVerifier()
		{
			@Override
			public boolean verify(final JComponent input)
			{
				return SoundFxScriptingPanel.this.verifyScriptInput(true);
			}
		});
		_textFieldScriptName.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(final DocumentEvent e)
			{
				SoundFxScriptingPanel.this.scriptNameChanged();
			}

			@Override
			public void insertUpdate(final DocumentEvent e)
			{
				SoundFxScriptingPanel.this.scriptNameChanged();
			}

			@Override
			public void changedUpdate(final DocumentEvent e)
			{
				SoundFxScriptingPanel.this.scriptNameChanged();
			}
		});
		_textFieldScriptName.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				SoundFxScriptingPanel.this.scriptNameActionPerformed();
			}
		});
		labelScriptingName.setLabelFor(_textFieldScriptName);
		_textFieldScriptName.setToolTipText("Javascript variable name for this sound for referencing in sibling and this scripts");
		final GridBagConstraints gbc__formattedTextFieldScriptingName = new GridBagConstraints();
		gbc__formattedTextFieldScriptingName.fill = GridBagConstraints.HORIZONTAL;
		gbc__formattedTextFieldScriptingName.gridx = 1;
		gbc__formattedTextFieldScriptingName.gridy = 0;
		_panelScriptingGeneral.add(_textFieldScriptName, gbc__formattedTextFieldScriptingName);

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		final GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		this.add(splitPane, gbc_splitPane);

		final JPanel panelScriptingInit = new JPanel();
		splitPane.setLeftComponent(panelScriptingInit);
		panelScriptingInit.setBorder(new TitledBorder(null, "Initialization Script", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_panelScriptingInit = new GridBagLayout();
		gbl_panelScriptingInit.columnWidths = new int[] { 0, 0 };
		gbl_panelScriptingInit.rowHeights = new int[] { 0, 0 };
		gbl_panelScriptingInit.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelScriptingInit.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelScriptingInit.setLayout(gbl_panelScriptingInit);

		final JScrollPane scrollPaneScriptingInit = new JScrollPane();
		final GridBagConstraints gbc_scrollPaneScriptingInit = new GridBagConstraints();
		gbc_scrollPaneScriptingInit.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneScriptingInit.gridx = 0;
		gbc_scrollPaneScriptingInit.gridy = 0;
		panelScriptingInit.add(scrollPaneScriptingInit, gbc_scrollPaneScriptingInit);

		_textAreaInitScript = new JTextArea();
		_textAreaInitScript.setEnabled(false);
		_textAreaInitScript.setEditable(false);
		_textAreaInitScript.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(final FocusEvent event)
			{
				SoundFxScriptingPanel.this.initScriptFocusLost();
			}
		});
		scrollPaneScriptingInit.setViewportView(_textAreaInitScript);

		final JPanel panelScriptingFinish = new JPanel();
		splitPane.setRightComponent(panelScriptingFinish);
		panelScriptingFinish.setBorder(new TitledBorder(null, "Finish Script", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_panelScriptingFinish = new GridBagLayout();
		gbl_panelScriptingFinish.columnWidths = new int[] { 0, 0 };
		gbl_panelScriptingFinish.rowHeights = new int[] { 0, 0 };
		gbl_panelScriptingFinish.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelScriptingFinish.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelScriptingFinish.setLayout(gbl_panelScriptingFinish);

		final JScrollPane scrollPaneScriptingFinish = new JScrollPane();
		final GridBagConstraints gbc_scrollPaneScriptingFinish = new GridBagConstraints();
		gbc_scrollPaneScriptingFinish.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneScriptingFinish.gridx = 0;
		gbc_scrollPaneScriptingFinish.gridy = 0;
		panelScriptingFinish.add(scrollPaneScriptingFinish, gbc_scrollPaneScriptingFinish);

		_textAreaFinishScript = new JTextArea();
		_textAreaFinishScript.setEnabled(false);
		_textAreaFinishScript.setEditable(false);
		_textAreaFinishScript.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(final FocusEvent event)
			{
				SoundFxScriptingPanel.this.finishScriptFocusLost();
			}
		});
		scrollPaneScriptingFinish.setViewportView(_textAreaFinishScript);
	}

	@Override
	public void setEnabled(final boolean enabled)
	{
		super.setEnabled(enabled);
		_panelScriptingGeneral.setEnabled(enabled);
		for (Component c : _panelScriptingGeneral.getComponents())
		{
			c.setEnabled(enabled);
		}
		_textAreaInitScript.setEnabled(enabled);
		_textAreaInitScript.setEditable(enabled);
		_textAreaFinishScript.setEnabled(enabled);
		_textAreaFinishScript.setEditable(enabled);
	}

	/**
	 * Event.
	 *
	 * @param processError processError
	 * @return true if no error, false otherwise
	 */
	private boolean verifyScriptInput(final boolean processError)
	{
		final String text = _textFieldScriptName.getText();

		boolean hasError = false;
		if (text.length() > 0)
		{
			if (Character.isJavaIdentifierStart(text.charAt(0)))
			{
				for (int i = 0; i < text.length(); i++)
				{
					if (!Character.isJavaIdentifierPart(text.charAt(i)))
					{
						hasError = true;
						break;
					}
				}
			}
			else
			{
				hasError = true;
			}
		}

		if (hasError && processError)
		{
			_textFieldScriptName.setSelectionStart(0);
			_textFieldScriptName.setSelectionEnd(text.length());
			Toolkit.getDefaultToolkit().beep();
		}
		return !hasError;
	}

	/**
	 * Event
	 */
	private void scriptNameChanged()
	{
		if (verifyScriptInput(false))
		{
			_textFieldScriptName.setForeground(UIManager.getColor("TextField.foreground"));
		}
		else
		{
			_textFieldScriptName.setForeground(Color.red);
		}
	}

	/**
	 * Event
	 */
	private void scriptNameActionPerformed()
	{
		if (_textFieldScriptName.getInputVerifier().verify(_textFieldScriptName) && (_data != null && _performChangeEvents))
		{
			_performChangeEvents = false;
			if (_textFieldScriptName.getText().isEmpty())
			{
				_data.setScriptName(null);
			}
			else
			{
				_data.setScriptName(_textFieldScriptName.getText());
			}
			_performChangeEvents = true;
		}
	}

	/**
	 * Event
	 */
	private void initScriptFocusLost()
	{
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			if (_textAreaInitScript.getText().trim().isEmpty())
			{
				_data.setInitScript(null);
			}
			else if ((_data.getInitScript() == null) || !_textAreaInitScript.getText().equals(_data.getInitScript().getSourceCode()))
			{
				_data.setInitScript(new ScriptItem(_textAreaInitScript.getText()));
			}
			_performChangeEvents = true;
		}
	}

	/**
	 * Event
	 */
	private void finishScriptFocusLost()
	{
		if (_data != null && _performChangeEvents)
		{
			_performChangeEvents = false;
			if (_textAreaFinishScript.getText().trim().isEmpty())
			{
				_data.setFinishScript(null);
			}
			else if ((_data.getFinishScript() == null) || !_textAreaFinishScript.getText().equals(_data.getFinishScript().getSourceCode()))
			{
				_data.setFinishScript(new ScriptItem(_textAreaFinishScript.getText()));
			}
			_performChangeEvents = true;
		}
	}

	/**
	 * Event.
	 * 
	 * @param property property
	 */
	private void dataChanged(final String property)
	{
		if (_performChangeEvents)
		{
			_performChangeEvents = false;
			if ((property == null) || SoundFxItem.PROPERTY_INIT_SCRIPT.equals(property))
			{
				if (_data.getInitScript() == null)
				{
					_textAreaInitScript.setText("");
				}
				else
				{
					_textAreaInitScript.setText(_data.getInitScript().getSourceCode());
				}
			}
			if ((property == null) || SoundFxItem.PROPERTY_FINISH_SCRIPT.equals(property))
			{
				if (_data.getFinishScript() == null)
				{
					_textAreaFinishScript.setText("");
				}
				else
				{
					_textAreaFinishScript.setText(_data.getFinishScript().getSourceCode());
				}
			}
			if ((property == null) || SoundFxItem.PROPERTY_SCRIPT_NAME.equals(property))
			{
				if (_data.getScriptName() == null)
				{
					_textFieldScriptName.setText("");
				}
				else
				{
					_textFieldScriptName.setText(_data.getScriptName());
				}
			}
			_performChangeEvents = true;
		}
	}

	/**
	 * Set the model
	 * 
	 * @param data model
	 */
	public void setModel(final SoundFxItem data)
	{
		if (data != _data)
		{
			if (_data != null)
			{
				_data.removeListener(_dataListener);

				final SoundFxItem oldData = _data;
				final String scriptName = _textFieldScriptName.getText();
				final String initScript = _textAreaInitScript.getText();
				final String finishScript = _textAreaFinishScript.getText();
				
				if (scriptName.isEmpty())
				{
					oldData.setScriptName(null);
				}
				else
				{
					oldData.setScriptName(scriptName);
				}
				if (initScript.trim().isEmpty())
				{
					oldData.setInitScript(null);
				}
				else if ((_data.getInitScript() == null) || !initScript.equals(_data.getInitScript().getSourceCode()))
				{
					oldData.setInitScript(new ScriptItem(initScript));
				}
				if (finishScript.trim().isEmpty())
				{
					oldData.setFinishScript(null);
				}
				else if ((_data.getFinishScript() == null) || !finishScript.equals(_data.getFinishScript().getSourceCode()))
				{
					oldData.setFinishScript(new ScriptItem(finishScript));
				}
			}
			_data = data;
			setEnabled(_data != null);
			if (_data != null)
			{
				dataChanged(null);
				_data.addListener(_dataListener);
			}
		}
	}
}
