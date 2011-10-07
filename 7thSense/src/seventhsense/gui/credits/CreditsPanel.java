/*
 * CreditsPanel.java
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
package seventhsense.gui.credits;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Panel for showing developer credits and donate button
 * 
 * @author Parallan, Drag-On
 *
 */
public class CreditsPanel extends JPanel
{
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(CreditsPanel.class.getName());

	/**
	 * Constructs the Credits panel
	 */
	public CreditsPanel()
	{
		super();
		
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		final JEditorPane editorPaneCredits = new JEditorPane();
		final GridBagConstraints gbc_editorPaneCredits = new GridBagConstraints();
		gbc_editorPaneCredits.fill = GridBagConstraints.BOTH;
		gbc_editorPaneCredits.gridx = 0;
		gbc_editorPaneCredits.gridy = 0;
		add(editorPaneCredits, gbc_editorPaneCredits);
		editorPaneCredits.setEditable(false);
		editorPaneCredits.setOpaque(false);
		try
		{
			editorPaneCredits.setPage(CreditsPanel.class.getResource("/seventhsense/resources/credits.html"));
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		editorPaneCredits.addHyperlinkListener(new HyperlinkListener()
		{
			@Override
			public void hyperlinkUpdate(final HyperlinkEvent event)
			{
				onCreditsHyperlinkUpdate(event);
			}
		});
		
	}

	/**
	 * Event.
	 * 
	 * @param event event
	 */
	private void onCreditsHyperlinkUpdate(final HyperlinkEvent event)
	{
		if(event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
		{
			if("http".equalsIgnoreCase(event.getURL().getProtocol())
					|| "https".equalsIgnoreCase(event.getURL().getProtocol()))
			{
				if(Desktop.isDesktopSupported())
				{
					final Desktop desktop = Desktop.getDesktop();
					if(desktop.isSupported(Desktop.Action.BROWSE))
					{
						try
						{
							desktop.browse(event.getURL().toURI());
						}
						catch (IOException e)
						{
							LOGGER.log(Level.SEVERE, e.toString(), e);
						}
						catch (URISyntaxException e)
						{
							LOGGER.log(Level.SEVERE, e.toString(), e);
						}
					}
				}
				else
				{
					LOGGER.log(Level.SEVERE, "Desktop not supported");
				}
			}
			else
			{
				LOGGER.log(Level.SEVERE, "Unsupported url type!");
			}
		}
	}
}
