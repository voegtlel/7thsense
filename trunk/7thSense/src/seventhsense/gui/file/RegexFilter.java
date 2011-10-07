/*
 * RegexFilter.java
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

import java.io.File;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileFilter;

/**
 * Class for regex FileFilter used in file dialogs
 * 
 * @author Parallan
 *
 */
public class RegexFilter extends FileFilter
{
	/**
	 * Default playlist filter
	 */
	public static final RegexFilter SeventhSensePlaylistFilter = new RegexFilter("7th Sense Playlist (*.7sp)", "\\.(7sp)$");
	/**
	 * Default library filter
	 */
	public static final RegexFilter SeventhSenseLibraryFilter = new RegexFilter("7th Sense Library (*.7lp)", "\\.(7lp)$");
	/**
	 * Default media file filter
	 */
	public static final RegexFilter MediaFileFilter = new RegexFilter("Media File (*.mp3, *.ogg, *.wav)", "\\.(mp3|ogg|wav)$");
	
	/**
	 * description (name) of the filter
	 */
	private final String _description;
	
	/**
	 * Regular expression
	 */
	private final Pattern _pattern;

	/**
	 * Creates a Regex file filter
	 * 
	 * @param description description (name) of the filter
	 * @param regexp regular expression for filtering
	 */
	public RegexFilter(final String description, final String regexp)
	{
		super();
		_description = description;
		_pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
	}

	public boolean accept(final File file)
	{
		if (file.isDirectory())
		{
			return true;
		}
		return _pattern.matcher(file.getPath()).find();
	}

	public String getDescription()
	{
		return _description;
	}
}