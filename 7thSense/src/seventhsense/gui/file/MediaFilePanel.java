/*
 * MediaFilePanel.java
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
package seventhsense.gui.file;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import seventhsense.data.FileReference;
import seventhsense.data.eventlist.EventList;
import javax.swing.ImageIcon;

/**
 * Class for selecting a media file
 * 
 * @author Parallan
 *
 */
public class MediaFilePanel extends JPanel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;

	private final JTextField _textFieldSoundFilePath;
	private final JButton _buttonSearchFilePath;
	private final JLabel _labelFileInfo;

	private final EventList<ChangeListener> _changeListeners = new EventList<ChangeListener>();

	/**
	 * Creates an instance of the MediaFilePanel
	 */
	public MediaFilePanel()
	{
		super();
		final GridBagLayout gbl_panelSoundFile = new GridBagLayout();
		gbl_panelSoundFile.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelSoundFile.rowHeights = new int[] { 0, 0 };
		gbl_panelSoundFile.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelSoundFile.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		this.setLayout(gbl_panelSoundFile);

		final JLabel labelSoundFile = new JLabel("");
		labelSoundFile.setToolTipText("Path to media file");
		labelSoundFile.setIcon(new ImageIcon(MediaFilePanel.class.getResource("/seventhsense/resources/Name_20.png")));
		final GridBagConstraints gbc_labelSoundFile = new GridBagConstraints();
		gbc_labelSoundFile.fill = GridBagConstraints.BOTH;
		gbc_labelSoundFile.insets = new Insets(0, 0, 0, 5);
		gbc_labelSoundFile.gridx = 0;
		gbc_labelSoundFile.gridy = 0;
		this.add(labelSoundFile, gbc_labelSoundFile);

		_textFieldSoundFilePath = new JTextField();
		_textFieldSoundFilePath.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(final FocusEvent e)
			{
				MediaFilePanel.this.filePathTextFieldActionPerformed();
			}
		});
		_textFieldSoundFilePath.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				MediaFilePanel.this.filePathTextFieldActionPerformed();
			}
		});
		labelSoundFile.setLabelFor(_textFieldSoundFilePath);
		_textFieldSoundFilePath.setToolTipText("Path to media file");
		final GridBagConstraints gbc_textFieldSoundFilePath = new GridBagConstraints();
		gbc_textFieldSoundFilePath.insets = new Insets(0, 0, 0, 5);
		gbc_textFieldSoundFilePath.fill = GridBagConstraints.BOTH;
		gbc_textFieldSoundFilePath.gridx = 1;
		gbc_textFieldSoundFilePath.gridy = 0;
		this.add(_textFieldSoundFilePath, gbc_textFieldSoundFilePath);
		_textFieldSoundFilePath.setColumns(10);

		_buttonSearchFilePath = new JButton("...");
		_buttonSearchFilePath.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				MediaFilePanel.this.searchFilePathActionPerformed();
			}
		});
		_buttonSearchFilePath.setToolTipText("Search file system for a media file");
		final GridBagConstraints gbc__buttonSearchFilePath = new GridBagConstraints();
		gbc__buttonSearchFilePath.insets = new Insets(0, 0, 0, 5);
		gbc__buttonSearchFilePath.fill = GridBagConstraints.BOTH;
		gbc__buttonSearchFilePath.gridx = 2;
		gbc__buttonSearchFilePath.gridy = 0;
		this.add(_buttonSearchFilePath, gbc__buttonSearchFilePath);

		_labelFileInfo = new JLabel("Info");
		_labelFileInfo.setToolTipText("Shows the type of media, or an error if it cannot be loaded");
		final GridBagConstraints gbc__labelFileInfo = new GridBagConstraints();
		gbc__labelFileInfo.fill = GridBagConstraints.VERTICAL;
		gbc__labelFileInfo.gridx = 3;
		gbc__labelFileInfo.gridy = 0;
		this.add(_labelFileInfo, gbc__labelFileInfo);
	}
	
	@Override
	public void setEnabled(final boolean enabled)
	{
		super.setEnabled(enabled);
		for(Component c : this.getComponents())
		{
			c.setEnabled(enabled);
		}
	}
	
	/**
	 * trys to get a relative path for the given path
	 * 
	 * @param path path to make relative
	 * @return relative path if possible, otherwise the given absolute path
	 */
	private String tryGetRelativePath(final File path)
	{
		final String localAbsolutePath = new File ("").getAbsolutePath();
		final String absolutePath = path.getAbsolutePath();
		if(absolutePath.startsWith(localAbsolutePath))
		{
			return absolutePath.substring(localAbsolutePath.length() + 1);
		}
		return path.getPath();
	}

	/**
	 * Event.
	 * Shows up a file chooser for media files
	 */
	private void searchFilePathActionPerformed()
	{
		final JFileChooser fileChooser = new JFileChooser(_textFieldSoundFilePath.getText());
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(RegexFilter.MediaFileFilter);
		fileChooser.setFileFilter(RegexFilter.MediaFileFilter);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			_textFieldSoundFilePath.setText(tryGetRelativePath(fileChooser.getSelectedFile()));
			filePathTextFieldActionPerformed();
		}
	}

	/**
	 * Event.
	 * Checks the entered path for a valid sound file
	 */
	private void filePathTextFieldActionPerformed()
	{
		final File file = new File(_textFieldSoundFilePath.getText());
		if (file.exists())
		{
			try
			{
				final AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(file);
				_labelFileInfo.setText(audioFileFormat.getType().toString());
				_labelFileInfo.setForeground(new Color(0x00aa00));
			}
			catch (UnsupportedAudioFileException e)
			{
				_labelFileInfo.setText("Unrecognized format");
				_labelFileInfo.setForeground(Color.RED);
			}
			catch (IOException e)
			{
				_labelFileInfo.setText("IO error");
				_labelFileInfo.setForeground(Color.RED);
			}
		}
		else
		{
			_labelFileInfo.setText("Not existing");
			_labelFileInfo.setForeground(Color.RED);
		}
		fireChangedListener();
	}

	/**
	 * Getter for the value
	 * 
	 * @return value
	 */
	public FileReference getValue()
	{
		return new FileReference(_textFieldSoundFilePath.getText());
	}

	/**
	 * Setter for the value
	 * 
	 * @param value value
	 */
	public void setValue(final FileReference value)
	{
		_textFieldSoundFilePath.setText(value.getPath());
		filePathTextFieldActionPerformed();
	}

	/**
	 * Add listener
	 * 
	 * @param listener listener
	 */
	public void addChangeListener(final ChangeListener listener)
	{
		_changeListeners.add(listener);
	}

	/**
	 * Add listener
	 * 
	 * @param listener listener
	 */
	public void removeChangedListener(final ChangeListener listener)
	{
		_changeListeners.remove(listener);
	}

	/**
	 * Fire listener
	 */
	protected void fireChangedListener()
	{
		for (ChangeListener listener : _changeListeners.iterateEvents())
		{
			listener.stateChanged(new ChangeEvent(this));
		}
	}
}
