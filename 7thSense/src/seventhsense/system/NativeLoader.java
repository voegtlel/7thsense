/*
 * Loader.java
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
package seventhsense.system;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jogamp.common.os.Platform;

/**
 * Loader for the native librarys required to run JOAL
 * 
 * @author Parallan
 *
 */
public class NativeLoader
{
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(NativeLoader.class.getName());
	
	/**
	 * Name of the operating system as a simplified string
	 */
	private final String _osName;
	/**
	 * System type (i586 or amd64)
	 */
	private final String _osSystem;
	/**
	 * Path to the library folder
	 */
	private final File _osLibPath;

	/**
	 * The Gluegen library file
	 */
	private final File _libraryFileGluegen;
	/**
	 * The Java OpenAL library file
	 */
	private final File _libraryFileJoal;
	
	/**
	 * Class for loading the native librarys needed for OpenAL
	 * 
	 * @param basePath path to the directory containing the library files
	 */
	public NativeLoader(final File basePath)
	{
		final String osNameProperty = Platform.getOS().toLowerCase();
        if(osNameProperty.startsWith("wind"))
        {
        	_osName = "windows";
        	_osSystem = (Platform.is32Bit()?"i586":"amd64");
        }
        else if(osNameProperty.startsWith("mac os x"))
        {
        	_osName = "macosx";
        	_osSystem = "universal";
        }
        else
        {
        	_osName = "linux";
        	_osSystem = (Platform.is32Bit()?"i586":"amd64");
        }
        
        _osLibPath = new File(basePath, _osName + "-" + _osSystem);
        
		_libraryFileGluegen = new File(_osLibPath + "/" + System.mapLibraryName("gluegen-rt"));
        _libraryFileJoal = new File(_osLibPath + "/" + System.mapLibraryName("joal"));
        
        LOGGER.log(Level.FINE, "Library Path: " + System.getProperty("java.library.path"));
        LOGGER.log(Level.INFO, "Library Gluegen: " + _libraryFileGluegen.getAbsolutePath() + " (exists: " + _libraryFileGluegen.exists() + ")");
        LOGGER.log(Level.INFO, "Library Joal: " + _libraryFileJoal.getAbsolutePath() + " (exists: " + _libraryFileGluegen.exists() + ")");
	}
	
	/**
	 * Loads the library path required by Java OpenAL
	 */
	public void initializeNatives()
	{
		// Adjust native library path
		
		// Set global property
		System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + _osLibPath.getAbsolutePath());

		// Clear ClassLoader cache
		// Hack java ClassLoader
		try
		{
			final Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		}
		catch (SecurityException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (NoSuchFieldException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (IllegalAccessException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	
		// Load native librarys
		System.load(_libraryFileGluegen.getAbsolutePath());
		System.load(_libraryFileJoal.getAbsolutePath());
	}
	
	/**
	 * Calls the given main class
	 * 
	 * @param classname name of the class
	 * @param args program arguments
	 */
	public void callClassMain(final String classname, final String[] args)
	{
		try
		{
			final Class<?> mainClass = NativeLoader.class.getClassLoader().loadClass(classname);
			final Method mainMethod = mainClass.getMethod("main", new Class<?>[]{String[].class});
			mainMethod.invoke(null, new Object[]{args});
		}
		catch (SecurityException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (NoSuchMethodException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (IllegalAccessException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		catch (InvocationTargetException e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}
}
