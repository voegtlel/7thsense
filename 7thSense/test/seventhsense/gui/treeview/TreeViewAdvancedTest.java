/*
 * TreeViewAdvancedTest.java
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
package seventhsense.gui.treeview;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import seventhsense.data.FolderNode;
import seventhsense.data.INode;
import seventhsense.data.scenario.AbstractScenarioNode;
import seventhsense.sound.engine.PlayerMixer;
import seventhsense.sound.engine.SoundException;

public class TreeViewAdvancedTest
{
	private static final Logger LOGGER = Logger.getLogger(TreeViewAdvancedTest.class.getName());

	private JFrame frame;
	private TreeView _tree;
	private JTextField _textFieldName;
	private int _nodeIndex;

	private static class TestScenarioNode extends AbstractScenarioNode
	{
		/**
		 * Default serial version
		 */
		private static final long serialVersionUID = 1L;

		public TestScenarioNode(final String name)
		{
			super(name);
		}

		@Override
		public INode deepClone()
		{
			return new TestScenarioNode(_name);
		}

		@Override
		public void play()
		{
		}

		@Override
		public void stop()
		{
		}

		@Override
		public void pause()
		{
		}

		@Override
		public void resume()
		{

		}

		@Override
		public boolean isPlaying()
		{
			return false;
		}

		@Override
		public void setFadeTime(final double fadeTime)
		{
			
		}

		@Override
		public boolean isPaused()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void load() throws SoundException
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unload()
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLoaded()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void validate(final boolean recursive)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isValid()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public double getTime()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getDuration()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setTime(double time)
		{
			// TODO Auto-generated method stub
			
		}
	}

	public static INode getTestData()
	{
		final FolderNode root = new FolderNode("root");
		root.addNode(0, new TestScenarioNode("Scenario1"));
		root.addNode(1, new TestScenarioNode("Scenario2"));
		final FolderNode subNode1 = new FolderNode("Folder1");
		root.addNode(2, subNode1);
		subNode1.addNode(0, new TestScenarioNode("Scenario3"));
		subNode1.addNode(1, new TestScenarioNode("Scenario4"));
		final FolderNode subNode2 = new FolderNode("Folder2");
		root.addNode(3, subNode2);
		subNode2.addNode(0, new TestScenarioNode("Scenario5"));
		subNode2.addNode(1, new TestScenarioNode("Scenario6"));
		final FolderNode subNode3 = new FolderNode("Folder3");
		subNode2.addNode(2, subNode3);
		subNode3.addNode(0, new TestScenarioNode("Scenario7"));
		subNode3.addNode(1, new TestScenarioNode("Scenario8"));
		final FolderNode subNode4 = new FolderNode("Folder4");
		subNode3.addNode(2, subNode4);
		subNode4.addNode(0, new TestScenarioNode("Scenario9"));
		subNode4.addNode(1, new TestScenarioNode("Scenario10"));
		final FolderNode subNode5 = new FolderNode("Folder5");
		subNode3.addNode(2, subNode5);
		subNode5.addNode(0, new TestScenarioNode("Scenario11"));
		subNode5.addNode(1, new TestScenarioNode("Scenario12"));
		return root;
	}

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					final TreeViewAdvancedTest window = new TreeViewAdvancedTest();
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					LOGGER.log(Level.SEVERE, e.toString(), e);
					return;
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TreeViewAdvancedTest()
	{
		initialize();
		_tree.setModel(getTestData());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		_tree = new TreeView();
		splitPane.setLeftComponent(_tree);

		final JPanel editorPanel = new JPanel();
		splitPane.setRightComponent(editorPanel);

		_textFieldName = new JTextField();
		editorPanel.add(_textFieldName);
		_textFieldName.setColumns(10);

		final JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				TreeViewAdvancedTest.this.addActionPerformed();
			}
		});
		editorPanel.add(btnAdd);

		final JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				TreeViewAdvancedTest.this.removeActionPerformed();
			}
		});
		editorPanel.add(btnRemove);

		final JButton btnSetName = new JButton("Set Name");
		btnSetName.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				TreeViewAdvancedTest.this.setNameActionPerformed();
			}
		});
		editorPanel.add(btnSetName);

		final JButton btnCollapseAll = new JButton("Collapse All");
		btnCollapseAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent arg0)
			{
				_tree.collapseAll();
			}
		});
		editorPanel.add(btnCollapseAll);

		final JButton btnExpandAll = new JButton("Expand All");
		btnExpandAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent event)
			{
				_tree.expandAll();
			}
		});
		editorPanel.add(btnExpandAll);
	}

	private void addActionPerformed()
	{
		final INode selection = _tree.getSelectedItem();
		selection.addNode(-1, new FolderNode("New Node " + (_nodeIndex++)));
	}

	private void removeActionPerformed()
	{
		final INode selection = _tree.getSelectedItem();
		selection.getParent().removeNode(selection);
	}

	private void setNameActionPerformed()
	{
		final INode selection = _tree.getSelectedItem();
		selection.setName(_textFieldName.getText());
	}
}
