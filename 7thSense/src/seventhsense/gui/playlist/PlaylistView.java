/*
 * PlaylistView.java
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
package seventhsense.gui.playlist;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import seventhsense.data.INode;
import seventhsense.data.scenario.sound.IPlayable;
import seventhsense.gui.ModelView;
import seventhsense.gui.database.DatabaseView;
import seventhsense.gui.database.INodeSelectionListener;
import seventhsense.gui.player.SoundPlayer;
import seventhsense.gui.shortcut.ShortcutPanel;
import seventhsense.sound.engine.PlayerMixer;

/**
 * View on the playlist
 * 
 * @author Parallan
 *
 */
public class PlaylistView extends ModelView<INode>
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(PlaylistView.class.toString());
	
	private DatabaseView _databaseView;
	private SoundPlayer _soundPlayer;

	/**
	 * Creates the playlist view
	 */
	public PlaylistView(final PlayerMixer mixer)
	{
		super();
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		final JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		splitPane.setResizeWeight(0.5);
		final GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		add(splitPane, gbc_splitPane);
		
		_databaseView = new DatabaseView();
		_databaseView.addSelectionChangedListener(new INodeSelectionListener()
		{
			@Override
			public void selectionChanged(final INode newSelected)
			{
				onNodeSelectionChanged(newSelected);
			}
		});
		_databaseView.setEditable(false);
		splitPane.setLeftComponent(_databaseView);
		
		final JPanel panelRight = new JPanel();
		splitPane.setRightComponent(panelRight);
		final GridBagLayout gbl_panelRight = new GridBagLayout();
		gbl_panelRight.columnWidths = new int[]{0, 0, 0};
		gbl_panelRight.rowHeights = new int[]{0, 0, 0};
		gbl_panelRight.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panelRight.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panelRight.setLayout(gbl_panelRight);
		
		_soundPlayer = new SoundPlayer(mixer);
		final GridBagConstraints gbc__soundPlayer = new GridBagConstraints();
		gbc__soundPlayer.gridwidth = 2;
		gbc__soundPlayer.insets = new Insets(0, 0, 5, 0);
		gbc__soundPlayer.fill = GridBagConstraints.BOTH;
		gbc__soundPlayer.gridx = 0;
		gbc__soundPlayer.gridy = 0;
		panelRight.add(_soundPlayer, gbc__soundPlayer);
		
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(50);
		final GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panelRight.add(scrollPane, gbc_scrollPane);
		
		final JPanel panelShortcuts = new JPanel();
		scrollPane.setViewportView(panelShortcuts);
		final GridBagLayout gbl_panelShortcuts = new GridBagLayout();
		gbl_panelShortcuts.columnWidths = new int[]{0, 0};
		gbl_panelShortcuts.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelShortcuts.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelShortcuts.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelShortcuts.setLayout(gbl_panelShortcuts);
		
		for(int i = 0; i < 12; i++)
		{
			final ShortcutPanel shortcutPanelF = new ShortcutPanel("F" + (i + 1), KeyStroke.getKeyStroke(KeyEvent.VK_F1 + i, 0));
			shortcutPanelF.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					onShortcutAction((ShortcutPanel)e.getSource());
				}
			});
			final GridBagConstraints gbc_shortcutPanelF = new GridBagConstraints();
			gbc_shortcutPanelF.insets = new Insets(0, 0, 5, 0);
			gbc_shortcutPanelF.fill = GridBagConstraints.BOTH;
			gbc_shortcutPanelF.gridx = 0;
			gbc_shortcutPanelF.gridy = i;
			panelShortcuts.add(shortcutPanelF, gbc_shortcutPanelF);
		}
	}
	
	/**
	 * Event
	 * 
	 * @param newSelected newSelected
	 */
	private void onNodeSelectionChanged(final INode newSelected)
	{
		if(newSelected == null)
		{
			_soundPlayer.setModel(null);
		}
		else
		{
			final INode realSelected = newSelected.getRealNode();
			if(realSelected instanceof IPlayable)
			{
				final IPlayable playable = (IPlayable) realSelected;
				_soundPlayer.setModel(playable);
			}
			else
			{
				_soundPlayer.setModel(null);
			}
		}
	}

	/**
	 * Action.
	 * 
	 * @param panel panel
	 */
	private void onShortcutAction(final ShortcutPanel panel)
	{
		if(panel.getModel() != null)
		{
			_soundPlayer.play(panel.getModel());
		}
	}
	
	@Override
	public void setModel(final INode root)
	{
		_databaseView.setModel(root);
	}

	/**
	 * Get the model
	 * 
	 * @return model
	 */
	public INode getModel()
	{
		return _databaseView.getModel();
	}
}
