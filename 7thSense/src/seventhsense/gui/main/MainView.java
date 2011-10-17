/*
 * MainView.java
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
package seventhsense.gui.main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import seventhsense.data.FolderNode;
import seventhsense.data.INode;
import seventhsense.gui.credits.CreditsView;
import seventhsense.gui.file.NodeFile;
import seventhsense.gui.library.LibraryView;
import seventhsense.gui.playlist.PlaylistView;
import seventhsense.gui.playlistmanager.PlaylistManagerView;
import seventhsense.gui.tabbedpane.VerticalLabelIcon;
import seventhsense.sound.engine.PlayerMixer;

/**
 * Panel containing all elements
 * 
 * @author Parallan
 *
 */
public class MainView extends JPanel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private final LibraryView _libraryView;
	private final PlaylistManagerView _playlistManagerView;
	private final PlaylistView _playlistView;
	private final CreditsView _creditView;

	/**
	 * Creates the main view
	 */
	public MainView()
	{
		super();
		
		this.addAncestorListener(new AncestorListener()
		{
			@Override
			public void ancestorRemoved(final AncestorEvent event)
			{
				MainView.this.onAncestorRemoved();
			}
			
			@Override
			public void ancestorMoved(final AncestorEvent event)
			{
				
			}
			
			@Override
			public void ancestorAdded(final AncestorEvent event)
			{
				
			}
		});
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		final GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		add(tabbedPane, gbc_tabbedPane);

		_libraryView = new LibraryView();
		tabbedPane.addTab(null, new VerticalLabelIcon(tabbedPane, "Library", true), _libraryView, null);
		
		_playlistManagerView = new PlaylistManagerView();
		_playlistManagerView.addLoadActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				MainView.this.onPlaylistManagerLoad();
			}
		});
		tabbedPane.addTab(null, new VerticalLabelIcon(tabbedPane, "Playlist Manager", true), _playlistManagerView, null);
		
		_playlistView = new PlaylistView();
		tabbedPane.addTab(null, new VerticalLabelIcon(tabbedPane, "Playlist", true), _playlistView, null);
		
		_creditView = new CreditsView();
		tabbedPane.addTab(null, new VerticalLabelIcon(tabbedPane, "Credits", true), _creditView, null);
		
		loadDefaultData();
	}
	
	/**
	 * Event.
	 */
	private void onPlaylistManagerLoad()
	{
		_playlistView.setModel(_playlistManagerView.getPlaylistModel());
	}
	
	/**
	 * Event.
	 */
	private void onAncestorRemoved()
	{
		saveDefaultData();
	}

	/**
	 * Loads the default files to the models
	 */
	private void loadDefaultData()
	{
		INode libraryNode;
		INode playlistNode; 
		if(NodeFile.DefaultLibraryFile.exists())
		{
			libraryNode = NodeFile.loadNode(NodeFile.DefaultLibraryFile);
		}
		else
		{
			libraryNode = new FolderNode("root");
		}
		if(NodeFile.DefaultPlaylistFile.exists())
		{
			playlistNode = NodeFile.loadNode(NodeFile.DefaultPlaylistFile);
		}
		else
		{
			playlistNode = new FolderNode("root");
		}
		
		_playlistManagerView.getSuperNode().addNode(-1, libraryNode);
		_playlistManagerView.getSuperNode().addNode(-1, playlistNode);
		
		_libraryView.setModel(libraryNode);
		_playlistManagerView.setModelLibrary(libraryNode);
		
		_playlistView.setModel(playlistNode);
		_playlistManagerView.setModelPlaylist(playlistNode);
	}
	
	/**
	 * Saves the models to the default files
	 */
	private void saveDefaultData()
	{
		NodeFile.saveNode(_libraryView.getModel(), NodeFile.DefaultLibraryFile);
		NodeFile.saveNode(_playlistView.getModel(), NodeFile.DefaultPlaylistFile);
	}
}
