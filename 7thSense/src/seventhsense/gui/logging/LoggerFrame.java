/*
 * LoggerFrame.java
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
package seventhsense.gui.logging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * Class for a window, that performs logging.
 * Logs everything to a logfile, too.
 * 
 * @author Parallan
 *
 */
public class LoggerFrame
{
	private final static Logger GLOBAL_LOGGER = Logger.getLogger("");
	
	private final JFrame _frame;
	private final JTextPane _textPaneLogging;
	private final StyledDocument _document;
	
	private final AttributeSet _messageInfoAttributeSet;
	private final AttributeSet _messageWarningAttributeSet;
	private final AttributeSet _messageExceptionAttributeSet;
	private final AttributeSet _messageExceptionClassAttributeSet;
	private final AttributeSet _messageExceptionMethodAttributeSet;
	private final AttributeSet _messageExceptionSourceAttributeSet;
	private final JPanel panel;
	
	private Handler _loggerHandler;
	
	private FileWriter _logfileWriter;
	
	/**
	 * Create the application.
	 */
	public LoggerFrame()
	{
		_frame = new JFrame();
		_frame.setTitle("Logger");
		_frame.setBounds(100, 100, 632, 441);
		_frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		_frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		final JScrollPane scrollPaneLogging = new JScrollPane();
		scrollPaneLogging.getVerticalScrollBar().setUnitIncrement(scrollPaneLogging.getFont().getSize());
		scrollPaneLogging.getHorizontalScrollBar().setUnitIncrement(scrollPaneLogging.getFont().getSize());
		_frame.getContentPane().add(scrollPaneLogging);
		
		panel = new JPanel();
		scrollPaneLogging.setViewportView(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		final StyleContext context = new StyleContext();
		_document = new DefaultStyledDocument(context);
		
		_textPaneLogging = new JTextPane();
		panel.add(_textPaneLogging);
		_textPaneLogging.setEditable(false);
		_textPaneLogging.setStyledDocument(_document);
		
		_messageInfoAttributeSet = new SimpleAttributeSet();
		_messageWarningAttributeSet = new SimpleAttributeSet();
		_messageExceptionAttributeSet = new SimpleAttributeSet();
		_messageExceptionClassAttributeSet = new SimpleAttributeSet();
		_messageExceptionMethodAttributeSet = new SimpleAttributeSet();
		_messageExceptionSourceAttributeSet = new SimpleAttributeSet();
		((SimpleAttributeSet)_messageWarningAttributeSet).addAttribute(StyleConstants.CharacterConstants.Foreground, Color.red);
		((SimpleAttributeSet)_messageExceptionAttributeSet).addAttribute(StyleConstants.CharacterConstants.Foreground, Color.red);
		((SimpleAttributeSet)_messageExceptionClassAttributeSet).addAttribute(StyleConstants.CharacterConstants.Bold, true);
		((SimpleAttributeSet)_messageExceptionClassAttributeSet).addAttribute(StyleConstants.CharacterConstants.LeftIndent, 10);
		((SimpleAttributeSet)_messageExceptionClassAttributeSet).addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(0xff4444));
		((SimpleAttributeSet)_messageExceptionMethodAttributeSet).addAttribute(StyleConstants.CharacterConstants.Bold, true);
		((SimpleAttributeSet)_messageExceptionMethodAttributeSet).addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(0xff0000));
		((SimpleAttributeSet)_messageExceptionSourceAttributeSet).addAttribute(StyleConstants.CharacterConstants.Bold, true);
		((SimpleAttributeSet)_messageExceptionSourceAttributeSet).addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(0xff4444));
		
		try
		{
			_logfileWriter = new FileWriter(new File("log.txt"), true);
			_logfileWriter.write("\r\n\r\n\r\nStartLogging at " + new Date().toString() + "\r\n");
		}
		catch (IOException e)
		{
		}
		
		_loggerHandler = new Handler()
		{
			@Override
			public void close() throws SecurityException
			{
				//no close operations
			}

			@Override
			public void flush()
			{
				//no flush operations
				if(_logfileWriter != null)
				{
					try
					{
						_logfileWriter.flush();
					}
					catch (IOException e)
					{
					}
				}
			}

			@Override
			public void publish(final LogRecord record)
			{
				synchronized (LoggerFrame.this)
				{
					LoggerFrame.this.publishLogRecord(record);
				}
			}
		};
		
		attachLogger();
	}
	
	/**
	 * Attaches the logger
	 */
	private void attachLogger()
	{
		GLOBAL_LOGGER.setUseParentHandlers(true);
		GLOBAL_LOGGER.setLevel(Level.FINE);
		GLOBAL_LOGGER.addHandler(_loggerHandler);
	}
	
	/**
	 * Detaches the logger
	 */
	private void detachLogger()
	{
		GLOBAL_LOGGER.removeHandler(_loggerHandler);
	}
	
	/**
	 * Writes a string to the logger window and file
	 * 
	 * @param str text to write
	 * @param attributeSet attributes for writing
	 */
	private void writeString(final String str, final AttributeSet attributeSet)
	{
		if(_logfileWriter != null)
		{
			try
			{
				_logfileWriter.write(str);
			}
			catch (IOException e)
			{
			}
		}
		try
		{
			_document.insertString(_document.getLength(), str, attributeSet);
		}
		catch (BadLocationException e)
		{
		}
	}
	
	/**
	 * Event.
	 * 
	 * @param record record
	 */
	private void publishLogRecord(final LogRecord record)
	{
		if(record.getSourceClassName().startsWith("seventhsense"))
		{
			final boolean isAtBottom = (_textPaneLogging.getCaretPosition() >= _document.getLength());
			
			AttributeSet messageAttributeSet = _messageInfoAttributeSet;
			if(record.getLevel().intValue() >= Level.WARNING.intValue())
			{
				messageAttributeSet = _messageWarningAttributeSet;
			}
			writeString(record.getMessage() + "\r\n", messageAttributeSet);
			if(record.getThrown() != null)
			{
				final Throwable throwable = record.getThrown();
				writeString(throwable.getClass().toString(), _messageExceptionClassAttributeSet);
				writeString(": " + throwable.getMessage() + "\r\n", _messageExceptionAttributeSet);
				for(StackTraceElement stackTraceElement : throwable.getStackTrace())
				{
					writeString("\tat ", _messageExceptionAttributeSet);
					writeString(stackTraceElement.getClassName(), _messageExceptionClassAttributeSet);
					writeString("." + stackTraceElement.getMethodName(), _messageExceptionMethodAttributeSet);
					String sourceString = "Unknown Source";
					if(stackTraceElement.getFileName() != null)
					{
						sourceString = stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber();
					}
					writeString("(" + sourceString + ")" + "\r\n", _messageExceptionSourceAttributeSet);
				}
			}
			
			if(isAtBottom)
			{
				_textPaneLogging.setCaretPosition(_document.getLength());
			}
		}
		try
		{
			_logfileWriter.flush();
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * Adds a listener to the window (e.g. for closing events)
	 * 
	 * @param listener listener
	 */
	public void addWindowListener(final WindowListener listener)
	{
		_frame.addWindowListener(listener);
	}

	/**
	 * Sets the window visible
	 * 
	 * @param visible visible
	 */
	public void setVisible(final boolean visible)
	{
		_frame.setVisible(visible);
	}
	
	/**
	 * Destroys the window
	 */
	public void dispose()
	{
		detachLogger();
		_frame.dispose();
	}
}
