/*
 * PlaylistManagerView.java
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
package seventhsense.gui.playlistmanager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import seventhsense.data.FolderNode;
import seventhsense.data.INode;
import seventhsense.data.LinkNode;
import seventhsense.data.eventlist.EventList;
import seventhsense.gui.database.DatabaseView;
import seventhsense.gui.database.INodeSelectionListener;
import seventhsense.gui.file.NodeFile;
import seventhsense.gui.file.RegexFilter;

/**
 * This class implements the window where the user may manage the playlist
 * @author Drag-On
 *
 */
public class PlaylistManagerView extends JPanel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private final DatabaseView _libraryView;
	private final JButton _buttonAdd;
	private final JButton _buttonRemove;
	private final JButton _buttonMoveUp;
	private final JButton _buttonMoveDown;
	private final DatabaseView _playlistView;

	private final INode _superRoot = new FolderNode("superroot");
	
	private final EventList<ActionListener> _onLoadListeners = new EventList<ActionListener>();

	/**
	 * Constructor
	 */
	public PlaylistManagerView()
	{
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final JToolBar toolBarFile = new JToolBar();
		toolBarFile.setFloatable(false);
		final GridBagConstraints gbc_toolBarFile = new GridBagConstraints();
		gbc_toolBarFile.fill = GridBagConstraints.BOTH;
		gbc_toolBarFile.insets = new Insets(0, 0, 5, 0);
		gbc_toolBarFile.gridx = 0;
		gbc_toolBarFile.gridy = 0;
		add(toolBarFile, gbc_toolBarFile);

		final JButton buttonOpen = new JButton("");
		buttonOpen.setToolTipText("Open a saved playlist");
		buttonOpen.setIcon(new ImageIcon(PlaylistManagerView.class.getResource("/seventhsense/resources/Open_20.png")));
		buttonOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				PlaylistManagerView.this.onOpenAction();
			}
		});
		toolBarFile.add(buttonOpen);

		final JButton buttonSaveAs = new JButton("");
		buttonSaveAs.setIcon(new ImageIcon(PlaylistManagerView.class.getResource("/seventhsense/resources/SaveAs_20.png")));
		buttonSaveAs.setToolTipText("Save current playlist as");
		buttonSaveAs.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				PlaylistManagerView.this.onSaveAsAction();
			}
		});
		toolBarFile.add(buttonSaveAs);

		final JButton buttonSave = new JButton("");
		buttonSave.setIcon(new ImageIcon(PlaylistManagerView.class.getResource("/seventhsense/resources/Save_20.png")));
		buttonSave.setToolTipText("Save playlist as default playlist (automatically done on exit)");
		buttonSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				PlaylistManagerView.this.onSaveAction();
			}
		});
		toolBarFile.add(buttonSave);
		final JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(10);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.5);
		final GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		add(splitPane, gbc_splitPane);

		_libraryView = new DatabaseView();
		_libraryView.setEditable(false);
		_libraryView.addSelectionChangedListener(new INodeSelectionListener()
		{
			@Override
			public void selectionChanged(final INode newSelected)
			{
				onLibrarySelectionChanged(newSelected);
			}
		});
		splitPane.setLeftComponent(_libraryView);

		final JPanel panelPlaylist = new JPanel();
		splitPane.setRightComponent(panelPlaylist);
		final GridBagLayout gbl_panelPlaylist = new GridBagLayout();
		gbl_panelPlaylist.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelPlaylist.rowHeights = new int[] { 0, 0 };
		gbl_panelPlaylist.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelPlaylist.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelPlaylist.setLayout(gbl_panelPlaylist);

		final JToolBar toolBarPlaylist = new JToolBar();
		final GridBagConstraints gbc_toolBarPlaylist = new GridBagConstraints();
		gbc_toolBarPlaylist.fill = GridBagConstraints.BOTH;
		gbc_toolBarPlaylist.gridx = 0;
		gbc_toolBarPlaylist.gridy = 0;
		panelPlaylist.add(toolBarPlaylist, gbc_toolBarPlaylist);
		toolBarPlaylist.setFloatable(false);
		toolBarPlaylist.setOrientation(SwingConstants.VERTICAL);

		_buttonAdd = new JButton("");
		_buttonAdd.setToolTipText("Add selected node from library to the playlist");
		_buttonAdd.setEnabled(false);
		_buttonAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				onButtonAddClicked();
			}
		});
		_buttonAdd.setIcon(new ImageIcon(PlaylistManagerView.class.getResource("/seventhsense/resources/Add_20.png")));
		toolBarPlaylist.add(_buttonAdd);

		_buttonRemove = new JButton("");
		_buttonRemove.setToolTipText("Remove selected node from the playlist");
		_buttonRemove.setEnabled(false);
		_buttonRemove.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				onButtonRemoveClicked();
			}
		});
		_buttonRemove.setIcon(new ImageIcon(PlaylistManagerView.class.getResource("/seventhsense/resources/Delete_20.png")));
		toolBarPlaylist.add(_buttonRemove);

		_buttonMoveUp = new JButton("");
		_buttonMoveUp.setToolTipText("Move selected node in playlist up");
		_buttonMoveUp.setEnabled(false);
		_buttonMoveUp.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				onButtonMoveUpClicked();
			}
		});
		_buttonMoveUp.setIcon(new ImageIcon(PlaylistManagerView.class.getResource("/seventhsense/resources/Up_20.png")));
		toolBarPlaylist.add(_buttonMoveUp);

		_buttonMoveDown = new JButton("");
		_buttonMoveDown.setToolTipText("Move selected node in playlist down");
		_buttonMoveDown.setEnabled(false);
		_buttonMoveDown.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				onButtonMoveDownClicked();
			}
		});
		_buttonMoveDown.setIcon(new ImageIcon(PlaylistManagerView.class.getResource("/seventhsense/resources/Down_20.png")));
		toolBarPlaylist.add(_buttonMoveDown);

		_playlistView = new DatabaseView();
		_playlistView.setOnlyRootEditable(true);
		_playlistView.setOnlyLinks(true);
		_playlistView.addSelectionChangedListener(new INodeSelectionListener()
		{
			@Override
			public void selectionChanged(final INode newSelected)
			{
				onPlaylistSelectionChanged(newSelected);
			}
		});
		final GridBagConstraints gbc_databaseView = new GridBagConstraints();
		gbc_databaseView.fill = GridBagConstraints.BOTH;
		gbc_databaseView.gridx = 1;
		gbc_databaseView.gridy = 0;
		panelPlaylist.add(_playlistView, gbc_databaseView);
	}

	/**
	 * Event.
	 */
	private void onOpenAction()
	{
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(RegexFilter.SeventhSensePlaylistFilter);
		fileChooser.setFileFilter(RegexFilter.SeventhSensePlaylistFilter);

		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			final File selectedFile = fileChooser.getSelectedFile();
			final INode loadedNode = NodeFile.loadNode(selectedFile);
			setModelPlaylist(loadedNode);
			fireLoadActionListener(new ActionEvent(this, -1, ""));
		}
	}

	/**
	 * Event.
	 */
	private void onSaveAsAction()
	{
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(RegexFilter.SeventhSensePlaylistFilter);
		fileChooser.setFileFilter(RegexFilter.SeventhSensePlaylistFilter);

		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			final File selectedFile = fileChooser.getSelectedFile();
			NodeFile.saveNode(getPlaylistModel(), selectedFile);
		}
	}

	/**
	 * Event.
	 */
	private void onSaveAction()
	{
		NodeFile.saveNode(getPlaylistModel(), NodeFile.DefaultPlaylistFile);
	}

	/**
	 * Event.
	 * 
	 * @param newSelected newSelected
	 */
	private void onLibrarySelectionChanged(final INode newSelected)
	{
		_buttonAdd.setEnabled(newSelected != null);
	}

	/**
	 * Checks if the specified node is a "root" node (inside first level in root)
	 * 
	 * @param testNode node to test
	 * @return true, if the test node is a top level node
	 */
	private boolean isRootNode(final INode testNode)
	{
		return (testNode != null) && (testNode.getParent() == _playlistView.getModel());
	}

	/**
	 * Event.
	 * 
	 * @param newSelected newSelected
	 */
	private void onPlaylistSelectionChanged(final INode newSelected)
	{
		final boolean enable = isRootNode(newSelected);
		_buttonRemove.setEnabled(enable);
		_buttonMoveUp.setEnabled(enable);
		_buttonMoveDown.setEnabled(enable);
	}

	/**
	 * Event.
	 */
	private void onButtonMoveDownClicked()
	{
		final INode selectedItem = _playlistView.getSelectedItem();
		if (isRootNode(selectedItem))
		{
			final INode parent = selectedItem.getParent();
			final int lastIndex = parent.getIndex(selectedItem);
			if (lastIndex < parent.getChildCount() - 1)
			{
				parent.removeNode(selectedItem);
				parent.addNode(parent.getChildCount(), selectedItem);
			}
		}
	}

	/**
	 * Event
	 */
	private void onButtonMoveUpClicked()
	{
		final INode selectedItem = _playlistView.getSelectedItem();
		if (isRootNode(selectedItem))
		{
			final INode parent = selectedItem.getParent();
			final int lastIndex = parent.getIndex(selectedItem);
			if (lastIndex > 0)
			{
				parent.removeNode(selectedItem);
				parent.addNode(lastIndex - 1, selectedItem);
			}
		}
	}

	/**
	 * Event
	 */
	private void onButtonRemoveClicked()
	{
		final INode selectedItem = _playlistView.getSelectedItem();
		if (isRootNode(selectedItem))
		{
			selectedItem.getParent().removeNode(selectedItem);
		}
	}

	/**
	 * Event
	 */
	private void onButtonAddClicked()
	{
		if (_libraryView.getSelectedItem() != null)
		{
			final INode selectedItem = _libraryView.getSelectedItem();
			final INode destNode = _playlistView.getModel();
			destNode.addNode(-1, new LinkNode(selectedItem));
		}
	}

	/**
	 * Sets the library model.
	 * The model must not have a parent (or it must be the super root)
	 * 
	 * @param libraryRoot model
	 */
	public void setModelLibrary(final INode libraryRoot)
	{
		if ((libraryRoot != null) && (libraryRoot.getParent() != null) && (libraryRoot.getParent() != _superRoot))
		{
			throw new IllegalArgumentException("Library root must not have a parent!");
		}
		if (_libraryView.getModel() != null)
		{
			_superRoot.removeNode(_libraryView.getModel());
		}
		if (libraryRoot != null)
		{
			_superRoot.addNode(-1, libraryRoot);
		}
		_libraryView.setModel(libraryRoot);
	}

	/**
	 * Sets the playlist model.
	 * The model must not have a parent (or it must be the super root)
	 * 
	 * @param playlistRoot model
	 */
	public void setModelPlaylist(final INode playlistRoot)
	{
		if ((playlistRoot != null) && (playlistRoot.getParent() != null) && (playlistRoot.getParent() != _superRoot))
		{
			throw new IllegalArgumentException("Playlist root must not have a parent!");
		}
		if (_playlistView.getModel() != null)
		{
			_superRoot.removeNode(_playlistView.getModel());
		}
		if (playlistRoot != null)
		{
			_superRoot.addNode(-1, playlistRoot);
		}
		_playlistView.setModel(playlistRoot);
	}

	/**
	 * Gets the library model
	 * 
	 * @return library model
	 */
	public INode getLibraryModel()
	{
		return _libraryView.getModel();
	}

	/**
	 * Gets the playlist model
	 * 
	 * @return playlist model
	 */
	public INode getPlaylistModel()
	{
		return _playlistView.getModel();
	}
	
	/**
	 * Gets the common parentnode of the library and playlist model
	 * 
	 * @return common model for library and playlist
	 */
	public INode getSuperNode()
	{
		return _superRoot;
	}
	
	/**
	 * Add listener
	 * 
	 * @param listener listener
	 */
	public void addLoadActionListener(final ActionListener listener)
	{
		_onLoadListeners.add(listener);
	}
	
	/**
	 * Remove listener
	 * 
	 * @param listener listener
	 */
	public void removeLoadActionListener(final ActionListener listener)
	{
		_onLoadListeners.remove(listener);
	}
	
	/**
	 * Fire event
	 * 
	 * @param event event
	 */
	private void fireLoadActionListener(final ActionEvent event)
	{
		for(ActionListener listener : _onLoadListeners.iterateEvents())
		{
			listener.actionPerformed(event);
		}
	}
}
