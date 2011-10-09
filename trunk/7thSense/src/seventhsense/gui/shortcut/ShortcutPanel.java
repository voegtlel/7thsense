/*
 * ShortcutPanel.java
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
package seventhsense.gui.shortcut;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import seventhsense.data.INode;
import seventhsense.data.eventlist.EventList;
import seventhsense.data.scenario.sound.IPlayable;
import seventhsense.gui.ModelView;

/**
 * Panel for keyboard-shortcuts
 * 
 * @author Parallan
 *
 */
public class ShortcutPanel extends ModelView<IPlayable>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private final JTextArea _textAreaSelectedItem;

	private IPlayable _model;
	
	private final EventList<ActionListener> _actionListeners = new EventList<ActionListener>();

	/**
	 * Creates a shortcut panel with a given keystroke
	 * 
	 * @param name name of the panel
	 * @param keyStroke keystroke to listen
	 */
	public ShortcutPanel(final String name, final KeyStroke keyStroke)
	{
		super();
		
		final ShortcutTransferHandler transferHandler = new ShortcutTransferHandler(this);
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final JButton buttonShortcut = new JButton(name);
		buttonShortcut.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				onShortcutAction();
			}
		});
		buttonShortcut.setFont(buttonShortcut.getFont().deriveFont(buttonShortcut.getFont().getStyle() | Font.BOLD,
				buttonShortcut.getFont().getSize() * 2));
		buttonShortcut.setTransferHandler(transferHandler);
		final GridBagConstraints gbc_buttonShortcut = new GridBagConstraints();
		gbc_buttonShortcut.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonShortcut.insets = new Insets(0, 0, 0, 5);
		gbc_buttonShortcut.gridx = 0;
		gbc_buttonShortcut.gridy = 0;
		add(buttonShortcut, gbc_buttonShortcut);

		_textAreaSelectedItem = new JTextArea();
		_textAreaSelectedItem.setLineWrap(true);
		_textAreaSelectedItem.setWrapStyleWord(true);
		_textAreaSelectedItem.setEnabled(false);
		_textAreaSelectedItem.setEditable(false);
		_textAreaSelectedItem.setTransferHandler(transferHandler);
		final GridBagConstraints gbc__textAreaSelectedItem = new GridBagConstraints();
		gbc__textAreaSelectedItem.fill = GridBagConstraints.BOTH;
		gbc__textAreaSelectedItem.gridx = 1;
		gbc__textAreaSelectedItem.gridy = 0;
		add(_textAreaSelectedItem, gbc__textAreaSelectedItem);

		buttonShortcut.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "Action");
		buttonShortcut.getActionMap().put("Action", new AbstractAction()
		{
			/**
			 * Required
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e)
			{
				onShortcutAction();
			}
		});
	}
	
	/**
	 * Gets the text for a model
	 * 
	 * @param model model
	 * @return text
	 */
	private String getModelText(final IPlayable model)
	{
		if(model instanceof INode)
		{
			INode node = (INode) model;
			final StringBuilder text = new StringBuilder();
			while(node.getParent() != null)
			{
				text.insert(0, "/" + node.toString());
				node = node.getParent();
			}
			return text.substring(1);
		}
		else
		{
			return model.toString();
		}
	}

	@Override
	public void setModel(final IPlayable model)
	{
		_model = model;
		_textAreaSelectedItem.setText(getModelText(model));
	}
	
	/**
	 * Returns the model as playable
	 * 
	 * @return model
	 */
	public IPlayable getModel()
	{
		return _model;
	}

	/**
	 * Event
	 */
	private void onShortcutAction()
	{
		fireActionListener();
	}
	
	/**
	 * Add a listener
	 * 
	 * @param listener listener
	 */
	public void addActionListener(final ActionListener listener)
	{
		_actionListeners.add(listener);
	}
	
	/**
	 * Remove a listener
	 * @param listener listener
	 */
	public void removeActionListener(final ActionListener listener)
	{
		_actionListeners.remove(listener);
	}
	
	/**
	 * Fire listeners
	 */
	private void fireActionListener()
	{
		final ActionEvent event = new ActionEvent(this, -1, "");
		for(ActionListener listener : _actionListeners)
		{
			listener.actionPerformed(event);
		}
	}
}
