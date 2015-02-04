/*
 *
 * Senf was created by the Information Security Office
 * at the Univeristy of Texas at Austin.
 * 
 * This work is licensed under the Creative Commons 
 * Attribution-NonCommercial-ShareAlike 3.0 United States
 * License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 *
 * Send comments to security@utexas.edu
 *
 */ 
 
package senf;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JViewport;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Point;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.io.*;
import java.io.IOException;
import java.io.FilenameFilter;
import java.io.FileWriter;

import java.util.Vector;

public class SenfGUI
{
	private static final int WINDOW_WIDTH  = 600,
				 WINDOW_HEIGHT = 650;

	public static final String VERSION = "1.haku.111";

	private JFrame senfWindow,
			logWindow;

	private JPanel  mainContentPanel;

	private StatusBar statusBar;

	private JLabel  rspLabel,
			lastDateFormLabel,
			minMatchLabel,
			logFileNameLabel,
			viewLogLabel,
			logLabel,
			logPassLabel,
			logPassVerifyLabel,
			decryptPassLabel,
			decryptPassVerifyLabel;

	private JButton quitButton,
			scanButton,
			rspBrowseButton,
			logFileBrowseButton,
			viewLogButton,
			ACLup,
			ACLdown,
			ACLAdd,
			ACLRemove;

	private JTextField 	rspTextField,
				logFileOptionsPanel,
				logFileNameText,
				logPassText,
				logPassVerifyText,
				decryptPassText,
				decryptPassVerifyText,
				ACLText,
				spiderBase,
				depthLimit;

	private JScrollPane ACLPane;
	private JTable ACLTable;

	private JTextArea outputArea;

	private JCheckBox 	lastDateCheckBox,
				printErrorsCheckBox,
				appendConfCheckBox,
				appendToLogCheckBox,
				encryptLogCheckBox,
				encryptedLogCheckBox;

	private NumericTextField maxSizeTextField;

	private JComboBox maxSizeComboBox,
				ACLMode,
				ACLRule;

	private DateTextField lastDateTextField;

	private NumericTextField minMatchTextField;

	private SenfOptions opts;

	private JCheckBox maxSizeCheckBox;

	private static final int 	SCAN_STATE_DEAD = 0,
					SCAN_STATE_PAUSED = 1,
					SCAN_STATE_ACTIVE = 2;

	private int scanState;

	private ACLThing meh;
	private JPanel listOptionsPanel;

	private JTable resultsTable,
			listTable;

	private Thread senfThread;

	private JTabbedPane optionsPane;

	public SenfGUI(SenfOptions options)
	{
		opts = options;
		scanState = SCAN_STATE_DEAD;

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) { System.out.println(e);}
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
	}

	public void showGUI()
	{
		System.out.println( "Starting GUI, Do Not Close This Window." );
		senfWindow = new JFrame("senf forecast");

		initContents();

		senfWindow.setResizable(false);
		senfWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		senfWindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		senfWindow.setVisible(true);

		senfWindow.addComponentListener(
			new java.awt.event.ComponentAdapter() {
				public void componentResized(java.awt.event.ComponentEvent e)
				{
					if(senfWindow.getHeight() < WINDOW_HEIGHT)
						senfWindow.setSize(senfWindow.getWidth(), WINDOW_HEIGHT);
					if(senfWindow.getWidth() < WINDOW_WIDTH)
						senfWindow.setSize(WINDOW_WIDTH, senfWindow.getHeight());
				}
			}
		);
	}

	private void initContents()
	{
		JPanel mainContentPanel = new JPanel(new BorderLayout());
		JPanel controlsPanel = new JPanel(new GridLayout(3, 1, 10, 5));
		controlsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		// Set up status bar
		statusBar = new StatusBar("Ready");
		statusBar.setBounds(-1, WINDOW_HEIGHT - 43, WINDOW_WIDTH + 1, 20);
		mainContentPanel.add(statusBar, BorderLayout.SOUTH);

		GridBagLayout gbl = new GridBagLayout();
		JPanel buttonsPanel = new JPanel(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new java.awt.Insets(20,0,5,0);
		gbc.anchor = GridBagConstraints.NORTH;

		// Set up logo picture
		{
			JButton logo = new JButton(new ImageIcon(SenfGUI.class.getResource("images/mutardo.png")));
			//logo.setBounds(WINDOW_WIDTH - 95, WINDOW_HEIGHT - 210, 75, 85);
			logo.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						new SenfAbout(senfWindow, true).runDialog();
					}
				}
			);
			logo.setPreferredSize(new java.awt.Dimension(75, 90));
			gbl.setConstraints(logo, gbc);
			buttonsPanel.add(logo);
		}
		
		gbc.insets = new java.awt.Insets(0,0,5,0);
		gbc.anchor = GridBagConstraints.CENTER;

		//Set up buttons
		{
			// Set up scanButton
			scanButton = new JButton("Scan");
			scanButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						scanButton_click();
					}
				}
			);
			//scanButton.setBounds(WINDOW_WIDTH - 95, WINDOW_HEIGHT - 117, 75, 25);
			scanButton.setPreferredSize(new java.awt.Dimension(75, 25));
			scanButton.setMinimumSize(new java.awt.Dimension(75, 25));
			scanButton.setMaximumSize(new java.awt.Dimension(75, 25));
			gbl.setConstraints(scanButton, gbc);
			buttonsPanel.add(scanButton);

			// Set up quitButton
			quitButton = new JButton("Quit");
			quitButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						quitButton_click();
					}
				}
			);
			//quitButton.setBounds(WINDOW_WIDTH - 95, WINDOW_HEIGHT - 85, 75, 25);
			quitButton.setPreferredSize(new java.awt.Dimension(75, 25));
			quitButton.setMinimumSize(new java.awt.Dimension(75, 25));
			quitButton.setMaximumSize(new java.awt.Dimension(75, 25));
			gbc.weighty = 1.0;
			gbc.insets = new java.awt.Insets(0,0,0,0);
			gbc.anchor = GridBagConstraints.NORTH;
			gbl.setConstraints(quitButton, gbc);
			buttonsPanel.add(quitButton);
		}

		//Set up output windows
		{
			JScrollPane outputScrollPane = new JScrollPane();
				outputArea = new JTextArea();
				outputArea.setEditable(false);
				outputScrollPane.setViewportView(outputArea);
				controlsPanel.add(outputScrollPane);

			JScrollPane resultsScrollPane = new JScrollPane();
				resultsTable = new JTable();
				resultsTable.setModel(
					new SenfResultsTableModel(
						new Object[][] { },
						new String[] { "Filename" }
					)
				);
				resultsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				resultsTable.addMouseListener(
					new java.awt.event.MouseAdapter()
					{
						public void mouseClicked(java.awt.event.MouseEvent e)
						{
							if(((SenfResultsTableModel)resultsTable.getModel()).getDataVector().size() == 0)
								return;

							SenfResult sr = (SenfResult)((SenfResultsTableModel)resultsTable.getModel()).getValueAt(
								resultsTable.getSelectedRows()[0],
								0
							);

							new SenfAnalyzer(sr, senfWindow, true).runDialog();
						}
					}
				);
				resultsTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "none");
				resultsTable.addKeyListener(
					new java.awt.event.KeyAdapter()
					{
						public void keyPressed(java.awt.event.KeyEvent e)
						{
							if(e.getKeyCode() != java.awt.event.KeyEvent.VK_ENTER)
								return;

							if(((SenfResultsTableModel)resultsTable.getModel()).getDataVector().size() == 0)
								return;

							SenfResult sr = (SenfResult)((SenfResultsTableModel)resultsTable.getModel()).getValueAt(
								resultsTable.getSelectedRows()[0],
								0
							);

							new SenfAnalyzer(sr, senfWindow, true).runDialog();
						}
					}
				);
				resultsScrollPane.setViewportView(resultsTable);
				resultsTable.setTableHeader(null);
				controlsPanel.add(resultsScrollPane);
		}

		//Set up options pane
		//                                                     ...I can't bear to see you go.
		{
			JPanel optionsPanel = new JPanel(new BorderLayout(10,0));
			optionsPane = new JTabbedPane();
				JPanel generalOptionsPanel = new JPanel(null);
				JPanel logFileOptionsPanel = new JPanel(null);
				listOptionsPanel = new JPanel(null);
				JPanel spiderOptionsPanel = new JPanel(null);
				optionsPane.addTab("General Options", generalOptionsPanel);
				optionsPane.addTab("LogFile Options", logFileOptionsPanel);
				optionsPane.addTab("Allow / Deny", listOptionsPanel);
//This will be fixed sooooooooon
//				optionsPane.addTab("Spider", spiderOptionsPanel);

					//Configure the General Options Tab
						rspLabel = new JLabel("Root scan path:");
						rspTextField = new JTextField();
						rspTextField.setColumns(20);
						rspBrowseButton = new JButton("Browse");
						rspLabel.setBounds(5, 5, 100, 20);
						generalOptionsPanel.add(rspLabel);
						rspTextField.setBounds(115, 5, 200, 20);
						generalOptionsPanel.add(rspTextField);
						rspBrowseButton.setBounds(320, 5, 90, 20);
						generalOptionsPanel.add(rspBrowseButton);
						rspBrowseButton.addActionListener(
							new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									rspBrowseButton_click();
								}
							}
						);
						// senf allows users to pass a list of directories from the CLI,
						// but currently this only grabs the first one. Maybe fix this.
							//in response to the above... Fixed ^.^
						if(opts.rootSearchDirs == null)
							rspTextField.setText( System.getProperty( "user.home" ) );
						else
							rspTextField.setText( opts.rootSearchDirs[0].toString() );

						// This is gone because I'm lazy, ok? If some other person wants to make
						// the nice dialog for it, or use a modified JFileChooser, he or she 
						// may rightly do so as licensed and bonded by the State of the Union 
						// Address which the President of the corporation delivered to the 
						// shareholders on a lukewarm day in mid-October when the hills were 
						// alive with the sound of music which is quite irritating to me right 
						// now as I have a headache.

						// P.S. I can't eat my cup.

						minMatchLabel = new JLabel("Minimum number of matches:");
						minMatchLabel.setBounds( 5, 60, 200, 20 );
						generalOptionsPanel.add(minMatchLabel);

						minMatchTextField = new NumericTextField();
						minMatchTextField.setText(""+opts.minMatches);
						minMatchTextField.setBounds(205, 60, 30, 20);
						generalOptionsPanel.add(minMatchTextField);

						printErrorsCheckBox = new JCheckBox("Report errors");
						printErrorsCheckBox.setBounds(2, 35, 110, 15);
						printErrorsCheckBox.setSelected(opts.showError);
						generalOptionsPanel.add(printErrorsCheckBox);
	
						maxSizeCheckBox = new JCheckBox("Check max file size?");
						maxSizeTextField = new NumericTextField();
						maxSizeComboBox = new JComboBox( new String[] { "bytes", "kilobytes", "megabytes", "gigabytes" } );
						maxSizeCheckBox.setBounds(2, 85, 200, 15);
						maxSizeCheckBox.addActionListener(
							new ActionListener() {
								public void actionPerformed(ActionEvent e)
								{
									maxSizeTextField.setVisible(maxSizeCheckBox.isSelected());
									maxSizeComboBox.setVisible(maxSizeCheckBox.isSelected());
								}
							}
						);
						generalOptionsPanel.add(maxSizeCheckBox);
						maxSizeTextField.setBounds(205, 85, 40, 20);
						maxSizeTextField.setVisible(false);
						maxSizeTextField.setText("1000");
						generalOptionsPanel.add(maxSizeTextField);
						maxSizeComboBox.setBounds(250, 85, 200, 20);
						maxSizeComboBox.setVisible(false);
						generalOptionsPanel.add(maxSizeComboBox);

						if(opts.maxFileLen != -1)
						{
							maxSizeCheckBox.setSelected(true);
							maxSizeComboBox.setSelectedIndex(opts.maxFileLenOrder);
							maxSizeTextField.setVisible(true);
							maxSizeComboBox.setVisible(true);
							maxSizeTextField.setText(""+opts.maxFileLenReq);
							maxSizeTextField.setCaretPosition(0);
						}

						lastDateCheckBox = new JCheckBox("Check last modified date?");
						lastDateTextField = new DateTextField();
						lastDateFormLabel = new JLabel("(yyyyMMdd)");
						lastDateCheckBox.setBounds(2, 110, 200, 15);
						lastDateCheckBox.addActionListener(
							new ActionListener() {
								public void actionPerformed(ActionEvent e)
								{
									lastDateTextField.setVisible(lastDateCheckBox.isSelected());
									lastDateFormLabel.setVisible(lastDateCheckBox.isSelected());
								}
							}
						);
						generalOptionsPanel.add(lastDateCheckBox);
						lastDateTextField.setBounds(205, 110, 85, 20);
						lastDateTextField.setVisible(false);
						generalOptionsPanel.add(lastDateTextField);
						lastDateFormLabel.setBounds(300, 110, 100, 15);
						lastDateFormLabel.setVisible(false);
						generalOptionsPanel.add(lastDateFormLabel);

						if(opts.checkModified)
						{
							lastDateCheckBox.setSelected(true);
							lastDateTextField.setVisible(true);
							lastDateFormLabel.setVisible(true);

							//Just in case we ever go back to this format...
							/*
							SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
							StringBuffer sb = new StringBuffer();
							sdf.format(new Date(opts.lastModified), sb, new FieldPosition(0));
							*/

							lastDateTextField.setText(opts.dateReq);
						}

					//Configure the LogFile Options Panel
						appendConfCheckBox = new JCheckBox("Append config file info to log");
                                                appendConfCheckBox.setBounds(5, 30, 225, 20);
                                                appendConfCheckBox.setSelected(opts.appendConfs);
                                                logFileOptionsPanel.add(appendConfCheckBox);


						appendToLogCheckBox = new JCheckBox("Append to existing log");
						appendToLogCheckBox.setBounds(230, 30, 170, 20);
						appendToLogCheckBox.setSelected(opts.appendLog);
						appendToLogCheckBox.addActionListener(
							new ActionListener()
							{
								public void actionPerformed( ActionEvent e )
								{
									logFileBrowseButton.setVisible( appendToLogCheckBox.isSelected() );
								}
							}
						);
						logFileOptionsPanel.add(appendToLogCheckBox);


						logFileNameLabel = new JLabel("Log File ");
						logFileNameLabel.setBounds(5, 5, 100, 20);
						logFileNameText = new JTextField( opts.logFilename );
						logFileNameText.setBounds(100, 5, 200, 20);
						logFileBrowseButton = new JButton("Browse");
                                                logFileBrowseButton.setBounds(305, 5, 90, 20);
						logFileBrowseButton.addActionListener(
							new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									logFileBrowseButton_click();
								}
							}
						);
						logFileOptionsPanel.add(logFileNameLabel);
						logFileOptionsPanel.add(logFileNameText);
						logFileOptionsPanel.add(logFileBrowseButton);


						viewLogButton = new JButton( "View Log" );
						viewLogButton.setBounds( 5, 90, 100, 20);
						viewLogButton.addActionListener(
							new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									viewLogButton_click();
								}
							}
						);
						logFileOptionsPanel.add(viewLogButton);

						encryptedLogCheckBox = new JCheckBox( "Log is Encrypted" );
						encryptedLogCheckBox.setBounds( 105, 90, 150, 20 );
						encryptedLogCheckBox.addActionListener(
                                                        new ActionListener()
                                                        {
                                                                public void actionPerformed(ActionEvent e)
                                                                {
                                                                        encryptedLogCheckBox_click();
                                                                }
                                                        }
                                                );
						logFileOptionsPanel.add( encryptedLogCheckBox );

						//Log Encryption... this will be great someday...when it actually does something...
						encryptLogCheckBox = new JCheckBox( "Encrypt Log" );
						encryptLogCheckBox.setBounds( 5, 60, 100, 20 );
						encryptLogCheckBox.setSelected( opts.encryptLog );
						encryptLogCheckBox.addActionListener(
                                                        new ActionListener()
                                                        {
                                                                public void actionPerformed(ActionEvent e)
                                                                {
			                                                encryptLogCheckBox_click();
                                                                }
                                                        }
                                                );
						logFileOptionsPanel.add( encryptLogCheckBox );

						decryptPassLabel = new JLabel( "Password" );
                                                decryptPassLabel.setBounds( 5, 115, 70, 20 );
                                                logFileOptionsPanel.add( decryptPassLabel );
                                                decryptPassLabel.setVisible( encryptedLogCheckBox.isSelected() );

                                                decryptPassVerifyLabel = new JLabel( "Verify Password" );
                                                decryptPassVerifyLabel.setBounds( 180, 115, 100, 20 );
                                                logFileOptionsPanel.add( decryptPassVerifyLabel );
                                                decryptPassVerifyLabel.setVisible( encryptedLogCheckBox.isSelected() );

                                                decryptPassText = new JPasswordField();
                                                decryptPassText.setBounds( 75, 115, 100, 20 );
                                                logFileOptionsPanel.add( decryptPassText );
                                                decryptPassText.setVisible( encryptedLogCheckBox.isSelected() );

                                                decryptPassVerifyText = new JPasswordField();
                                                decryptPassVerifyText.setBounds( 285, 115, 100, 20 );
                                                logFileOptionsPanel.add( decryptPassVerifyText);
                                                decryptPassVerifyText.setVisible( encryptedLogCheckBox.isSelected() );

						logPassLabel = new JLabel( "Password:" );
						logPassLabel.setBounds( 110, 60, 70, 20 );
						logFileOptionsPanel.add( logPassLabel );
						logPassLabel.setVisible( encryptLogCheckBox.isSelected() );

						logPassVerifyLabel = new JLabel( "Verify:" );
						logPassVerifyLabel.setBounds( 290, 60, 70, 20 );
						logFileOptionsPanel.add( logPassVerifyLabel );
						logPassVerifyLabel.setVisible( encryptLogCheckBox.isSelected() );

						logPassText = new JPasswordField();
						logPassText.setBounds( 185, 60, 100, 20 );
						logFileOptionsPanel.add( logPassText );
						logPassText.setVisible( encryptLogCheckBox.isSelected() );

						logPassVerifyText = new JPasswordField();
						logPassVerifyText.setBounds( 360, 60, 100, 20 );
						logFileOptionsPanel.add( logPassVerifyText);
						logPassVerifyText.setVisible( encryptLogCheckBox.isSelected() );

						logFileBrowseButton.setVisible( appendToLogCheckBox.isSelected() );

					//Setup ACL thingy Tab
					//contrary to popular belief, ACL stands for A.wesomeness C.learly L.acking
					//contrary to the comment above, which was made when the ACL was clearly lacking awesomeness, it is now the coolest thing since sliced bread and should be treated as such
						meh = new ACLThing();
						ACLDraw( meh ); //<<--...at somepoint...
						ACLPane = new JScrollPane( ACLTable );
						ACLPane.setBounds( 5, 5, 355, 115 );
        				        listOptionsPanel.add( ACLPane );

        				        ACLPane.setWheelScrollingEnabled( true );
						ACLup = new JButton( "Up" );
						ACLup.setBounds( 360, 5, 100, 20 );
						ACLup.addActionListener(
                                                        new ActionListener()
                                                        {
                                                                public void actionPerformed(ActionEvent e)
                                                                {
                                                                        int selected = ACLTable.getSelectedRow();

									if( selected >= 0 )
									{
										try
										{
											//....???!!!?!?
											( ( DefaultTableModel )ACLTable.getModel() ).moveRow( selected, selected, selected-1 );
											ACLTable.setRowSelectionInterval( selected-1, selected-1);

											meh.swap( selected, selected-1 );

											ACLPane.getVerticalScrollBar().setValue( 15*(selected-1) );

											meh.finalize( ACLTable );
										}
										catch( Exception E )
										{
											error( "Can't Move Selection Up!" );
										}
									}
									else
									{
										error( "Invalid Selection!" );
									}
                                                                }
                                                        }
                                                );
						listOptionsPanel.add( ACLup );

						ACLdown = new JButton( "Down" );
						ACLdown.setBounds( 360, 30, 100, 20 );
                                                ACLdown.addActionListener(
                                                        new ActionListener()
                                                        {
                                                                public void actionPerformed(ActionEvent e)
                                                                {
                                                                        int selected = ACLTable.getSelectedRow();

									if( selected >= 0 )
									{
										try
										{
											( ( DefaultTableModel )ACLTable.getModel() ).moveRow( selected, selected, selected+1 );
											ACLTable.setRowSelectionInterval( selected+1, selected+1 );

											meh.swap( selected, selected+1 );
											
											ACLPane.getVerticalScrollBar().setValue( 15*(selected-1) );

											meh.finalize( ACLTable );
										}
										catch( Exception E )
										{
											error( "Can't Move Selection Down!" );
										}
									}
									else
									{
										error( "Invalid Selection!" );
									}
                                                                }
                                                        }
                                                );
                                                listOptionsPanel.add( ACLdown );

						ACLRemove = new JButton( "Remove" );
                                                ACLRemove.setBounds( 360, 55, 100, 20 );
                                                ACLRemove.addActionListener(
                                                        new ActionListener()
                                                        {
                                                                public void actionPerformed(ActionEvent e)
                                                                {
									int selected = ACLTable.getSelectedRow();

									if( selected >= -1 )
									{
										( ( DefaultTableModel )ACLTable.getModel() ).removeRow( selected );

										meh.remove( selected );

										meh.finalize( ACLTable );
									}
									else
									{
										error( "Invalid Selection!" );
									}
                                                                }
                                                        }
                                                );
                                                listOptionsPanel.add( ACLRemove );

						ACLMode = new JComboBox( new String[] { "ALLOW ", "DENY " } );
						ACLMode.setBounds( 5, 120, 100, 20 );
						listOptionsPanel.add( ACLMode );

						ACLRule = new JComboBox( new String[] { "BEGINSWITH ", "CONTAINS ", "ENDSWITH ", "EXACTLY ", "REGEX " } );
						ACLRule.setBounds( 115, 120, 125, 20 );
						listOptionsPanel.add( ACLRule );

						ACLText = new JTextField();
						ACLText.setBounds( 245, 120, 100, 20 );
						listOptionsPanel.add( ACLText );

						ACLAdd = new JButton( "Add" );
                                                ACLAdd.setBounds( 360, 120, 100, 20 );
                                                ACLAdd.addActionListener(
                                                        new ActionListener()
                                                        {
                                                                public void actionPerformed(ActionEvent e)
                                                                {
                                                                        ( (DefaultTableModel )ACLTable.getModel() ).addRow( new String[] { ACLMode.getSelectedItem().toString(), ACLRule.getSelectedItem().toString(), ACLText.getText() } );
                                                                	meh.add( ACLMode.getSelectedItem().toString() + ACLRule.getSelectedItem().toString() + ACLText.getText() );

									ACLText.setText("");
									
									meh.finalize( ACLTable );
								}
                                                        }
                                                );
                                                listOptionsPanel.add( ACLAdd );

					//Setup Spider Tab Still gotta make it work...
/*						JLabel baseLabel = new JLabel( "Starting Website:" );
						baseLabel.setBounds( 5, 5, 150, 20 );
						spiderOptionsPanel.add( baseLabel );

						spiderBase = new JTextField();
						spiderBase.setBounds( 155, 5, 250, 20 );
						spiderOptionsPanel.add( spiderBase );

						JLabel depthLabel = new JLabel( "Depth Limit:" );
						depthLabel.setBounds( 5, 25, 100, 20 );
						spiderOptionsPanel.add( depthLabel );

						depthLimit = new JTextField();
						depthLimit.setBounds( 155, 25, 20, 20 );
						spiderOptionsPanel.add( depthLimit );

						JButton spider = new JButton( "Spider" );
						spider.setBounds( 350, 125, 100, 20 );
						spider.addActionListener(
                                                        new ActionListener()
                                                        {
                                                                public void actionPerformed(ActionEvent e)
                                                                {
									spider_click();
                                                                }
                                                        }
                                                );
						spiderOptionsPanel.add( spider );
*/
			optionsPanel.add(optionsPane, BorderLayout.CENTER);
			optionsPanel.add(buttonsPanel, BorderLayout.EAST);
			optionsPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,1));
			controlsPanel.add(optionsPanel);
		}

		mainContentPanel.setOpaque(true);

		mainContentPanel.add(controlsPanel, BorderLayout.CENTER);
		senfWindow.setContentPane(mainContentPanel);
	}

	public void spider_click()
	{
		//Spider stuffs....
	}


	private void ACLDraw( ACLThing meh )
	{
		final String[] COL_NAMES = { "Allow/Deny", "Rule", "Expression" };
		final int NUM_COLS = 3;
		String[] lines = meh.getLines();
		int numRows = meh.getSize();
		String[][] data = new String[ numRows ][ NUM_COLS ];
		int i, x;

		for( i=0; i<numRows; i++ )
		{
			String[] temp = lines[ i ].split( " " );

			for( x=0; x<NUM_COLS; x++ )
			{
				data[ i ][ x ] = temp[ x ];
			}
		}

		ACLTable = new JTable( new DefaultTableModel( data, COL_NAMES )
					{
						public boolean isCellEditable( int row, int column )
						{
							return false;
						}
					}
		);
		ACLTable.setShowVerticalLines( false );
		ACLTable.setRowHeight( 15 );
		ACLTable.setRowMargin( 0 );
	}

	private void error( String arg )
	{
		new ErrorDialog( arg, this.senfWindow, true ).runDialog();
	}

	private void quitButton_click() {
		switch (scanState)
		{
			case SCAN_STATE_DEAD:
				System.exit(0);
			break;
			case SCAN_STATE_PAUSED:
			case SCAN_STATE_ACTIVE:
				this.quitButton.setText("Quit");
				this.scanButton.setText("Scan");
				this.setOptionsEnabled(true);
				this.outputArea.append("\nScanning stopped by user\n");
				statusBar.setText("Ready");
				scanState = SCAN_STATE_DEAD;
				senfThread.stop();
			break;
		}
	}

	private void scanButton_click() {
		switch(scanState)
		{
			case SCAN_STATE_DEAD:
				// First, update our options
				
					
				try
				{
					opts.setMinMatches( minMatchTextField.getText() );
				} catch (NumberFormatException nfe)
				{
					new ErrorDialog("Invalid minimum matches!", this.senfWindow, true).runDialog();
					return;
				}

				try
				{
					if(maxSizeCheckBox.isSelected())
					{
						char[] maxFileOrders = { 'b', 'k', 'm', 'g' };
						opts.setMaxFileLength( maxSizeTextField.getText() + maxFileOrders[maxSizeComboBox.getSelectedIndex()] );
					}
					else
					{
						opts.setMaxFileLength("-1");
					}
				} catch (NumberFormatException nfe)
				{
					new ErrorDialog("Invalid max file size!", this.senfWindow, true).runDialog();
					return;
				}

				try
				{
					if(lastDateCheckBox.isSelected())
					{
						opts.setLastModifiedDate(lastDateTextField.getText());
					}
					else
					{
						opts.checkModified = false;
						opts.lastModified = 0;
					}
				} catch (ParseException pe)
				{
					new ErrorDialog("Invalid last modified date!", this.senfWindow, true).runDialog();
					return;
				}

				try
				{
					opts.setRootDirs( rspTextField.getText().split( File.pathSeparator ) );
				} catch (IOException ioe)
				{
					new ErrorDialog("Invalid root path!", this.senfWindow, true).runDialog();
					return;
				}

				opts.encryptLog = encryptLogCheckBox.isSelected();
				if( opts.encryptLog )
			{
				if( logPassText.getText().equals( logPassVerifyText.getText() ) )
					{
						if( logPassText.getText().equals( null ) || logPassText.getText().equals( "" ) )
						{
							new ErrorDialog("Invalid Log Encryption Password!", this.senfWindow, true).runDialog();
							return;
						}
						else
						{
							opts.logPass = logPassText.getText();
						}
					}
					else
					{
						new ErrorDialog("Log Encryption Passwords Do not Match!", this.senfWindow, true).runDialog();
						return;
					}
				}

				opts.appendLog = appendToLogCheckBox.isSelected();
				opts.logFilename = logFileNameText.getText();

				opts.showError = printErrorsCheckBox.isSelected();
				opts.appendConfs = appendConfCheckBox.isSelected();

				opts.setupOutputLog();

				// Some things that *need* to be set:
				opts.logToFile = true;

				outputArea.setText("");
				((SenfResultsTableModel)resultsTable.getModel()).clearTable();

				OutputThing o = new OutputThing() 
				{
					int tried = 0;

					private void scroll()
					{
						outputArea.setCaretPosition(outputArea.getDocument().getLength());
					}
					private void log( String words )
					{
						String write = words;
						if( tried == 0 )
						{
							try
							{
								if( opts.logToFile )
								{
									if( opts.encryptLog )
									{
										Encrypter enc = new Encrypter( opts.logPass );
										write = enc.encrypt( words );
									}
									FileWriter logFile = new FileWriter( new File( opts.logFilename ), true );
									logFile.write( write + "\n" );
									logFile.close();
								}
							}
							catch( Exception E )
							{
								System.out.println( "Error writing to Log File. SENF will continue without a Log File." );
								tried = 1;
							}
						}
					}
					public void print(String words) 
					{
						outputArea.append(words);
						scroll();
						log( words );
					}
					public void println(String words) 
					{
						outputArea.append(words);
						outputArea.append( System.getProperty( "line.separator" ) );
						scroll();
						log( words );
					}
					public void println() 
					{ 
						outputArea.append( System.getProperty( "line.separator" ) );
						scroll();
						log( "" );
					}
				};

				OutputThing so = new OutputThing() {
					public void print(String words) {
						statusBar.setText(words);
					}
					public void println(String words) {
						print(words);
					}
					public void println() { 
					}
				};

				try
				{
					senfThread = new Thread( 
						new SenfScanner( 
							opts
							,
							new SenfDoneThing()
							{
								public void doStuff()
								{
									scanButton.setText( "Scan" );
									quitButton.setText( "Quit" );
									setOptionsEnabled( true );
									statusBar.setText( "Ready" );
									scanState = SCAN_STATE_DEAD;
								}
							}
							,
							new SenfMatchEvent()
							{
								public void onMatch( SenfResult result )
								{
									(( SenfResultsTableModel) resultsTable.getModel() ).addResult( result );
								}
							}
							,
							o
							,
							so
						)
					);
					senfThread.start();
				}
				catch( Exception E )
				{
					error( "Error while running SENF!" );
						return;
				}

				this.scanButton.setText("Pause");
				this.quitButton.setText("Stop");
				setOptionsEnabled(false);
				scanState = SCAN_STATE_ACTIVE;
			break;
			case SCAN_STATE_PAUSED:
				this.scanButton.setText("Pause");
				this.quitButton.setText("Stop");
				scanState = SCAN_STATE_ACTIVE;
				senfThread.resume();
			break;
			case SCAN_STATE_ACTIVE:
				this.scanButton.setText("Resume");
				this.quitButton.setText("Stop");
				scanState = SCAN_STATE_PAUSED;
				senfThread.suspend();
			break;
		}
	}

	private void setOptionsEnabled(boolean enabled)
	{
		this.optionsPane.setEnabled(enabled);
		this.rspTextField.setEnabled(enabled);
		this.lastDateCheckBox.setEnabled(enabled);
		this.printErrorsCheckBox.setEnabled(enabled);
		this.appendConfCheckBox.setEnabled(enabled);
		this.maxSizeTextField.setEnabled(enabled);
		this.maxSizeComboBox.setEnabled(enabled);
		this.lastDateTextField.setEnabled(enabled);
		this.minMatchTextField.setEnabled(enabled);
		this.maxSizeCheckBox.setEnabled(enabled);
		this.rspBrowseButton.setEnabled(enabled);
		this.rspLabel.setEnabled(enabled);
		this.lastDateFormLabel.setEnabled(enabled);
		this.minMatchLabel.setEnabled(enabled);
		this.logFileBrowseButton.setEnabled(enabled);
		this.logFileNameText.setEnabled(enabled);
		this.appendToLogCheckBox.setEnabled(enabled);
		this.encryptedLogCheckBox.setEnabled(enabled);
		this.viewLogButton.setEnabled(enabled);
		this.encryptLogCheckBox.setEnabled(enabled);
		this.decryptPassText.setEnabled(enabled);
		this.decryptPassVerifyText.setEnabled(enabled);
		this.logPassText.setEnabled(enabled);
		this.logPassVerifyText.setEnabled(enabled);
		this.ACLTable.setEnabled(enabled);
		this.logFileNameLabel.setEnabled(enabled);
		this.ACLPane.setEnabled(enabled);
		this.ACLup.setEnabled(enabled);
		this.ACLdown.setEnabled(enabled);
		this.ACLAdd.setEnabled(enabled);
		this.ACLRemove.setEnabled(enabled);
		this.ACLMode.setEnabled(enabled);
		this.ACLRule.setEnabled(enabled);
		this.ACLText.setEnabled(enabled);
	}

	private void rspBrowseButton_click()
	{
		JFileChooser rspFileChooser = new JFileChooser();
		String rspFilePathAndName;

		rspFileChooser.setAcceptAllFileFilterUsed( false );
		rspFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		rspFileChooser.setDialogTitle( "senf Root Path Chooser" );
		rspFileChooser.setMultiSelectionEnabled( true );

		int returnVal = rspFileChooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			File[] selectedFiles = rspFileChooser.getSelectedFiles();

			rspFilePathAndName = selectedFiles[0].getPath();

			for( int i=1; i<selectedFiles.length; i++ )
			{
				rspFilePathAndName = rspFilePathAndName + selectedFiles[i].getPath() + File.pathSeparator;
				System.out.println( selectedFiles[i].getName() );
			}
			rspTextField.setText( rspFilePathAndName );
		}
	}

	private void logFileBrowseButton_click()
	{
		JFileChooser logFileChooser = new JFileChooser();
		String logFilePathAndName = new String();
		SenfFileFilter txtFilter = new SenfFileFilter("txt", "Text Files (.txt)");

		logFileChooser.addChoosableFileFilter( txtFilter );
		logFileChooser.setAcceptAllFileFilterUsed( false );
		logFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		logFileChooser.setDialogTitle( "senf Log File Chooser" );

		int returnVal = logFileChooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			logFilePathAndName = logFileChooser.getCurrentDirectory().getPath() + System.getProperty( "file.separator" )  + logFileChooser.getSelectedFile().getName();
			logFileNameText.setText( logFilePathAndName );
		}
	}

	private void encryptLogCheckBox_click()
	{
                logPassLabel.setVisible( encryptLogCheckBox.isSelected() );
                logPassVerifyLabel.setVisible( encryptLogCheckBox.isSelected() );
                logPassText.setVisible( encryptLogCheckBox.isSelected() );
		logPassVerifyText.setVisible( encryptLogCheckBox.isSelected() );

	}

        private void encryptedLogCheckBox_click()
        {
                decryptPassLabel.setVisible( encryptedLogCheckBox.isSelected() );
                decryptPassVerifyLabel.setVisible( encryptedLogCheckBox.isSelected() );
                decryptPassText.setVisible( encryptedLogCheckBox.isSelected() );
                decryptPassVerifyText.setVisible( encryptedLogCheckBox.isSelected() );

        }

	private void viewLogButton_click()
	{
		String filename = logFileNameText.getText();
		String line;
		StringBuffer sb = new StringBuffer();
		String fileStr = new String();

		if( filename.equals(null) || filename.equals("") )
		{
                                new ErrorDialog("The Specified Log File Does Not Exist!", this.senfWindow, true).runDialog();
		}
		else
		{
			if( !filename.endsWith( opts.DEFAULT_LOG_EXTENSION ) )
			{
				filename = filename + opts.DEFAULT_LOG_EXTENSION;
			}

			try
			{
				BufferedReader in = new BufferedReader(new FileReader(filename));

				if( encryptedLogCheckBox.isSelected() )
				{
					if( decryptPassText.getText().equals( decryptPassVerifyText.getText() ) )
					{
						if( decryptPassText.getText().equals( null ) || decryptPassText.getText().equals( "" ) )
						{
							error( "Invalid Log Decryption Password!" );
						}
						else
						{
							Encrypter dec = new Encrypter( decryptPassText.getText() );
							
							while( (line = in.readLine() ) != null )
							{
								sb.append( dec.decrypt( line ) );
								sb.append( "\n" );
							}
						}
					}
				}
				else
				{
					while ((line = in.readLine()) != null)
					{
						sb.append(line);
						sb.append("\n");
					}
				}

				in.close();
				fileStr = sb.toString().trim();
			}
			catch ( Exception e )
			{
				new ErrorDialog("The Specified Log File Does Not Exist!", this.senfWindow, true).runDialog();
			}
			//fight alzheimer's.  fileStr = what I want to output in a new JFrame... at some point... I think
				//^^I don't think that's right^^
					//^^...never mind... I was right the first time^^
						//I wrote it for a reason...			

			outputArea.setText( fileStr );
			outputArea.setCaretPosition( 0 );
		}
	}

	private class StatusBar extends JPanel
	{
		JLabel statusLabel;

		StatusBar(String text)
		{
			super(new java.awt.BorderLayout());

			statusLabel = new JLabel(text);
			add(statusLabel, java.awt.BorderLayout.WEST);
			setBorder(new BevelBorder(BevelBorder.LOWERED));
		}

		public void setText(String text)
		{
			statusLabel.setText(text);
		}	
	}

	private class NumericTextField extends JTextField
	{
		public NumericTextField()
		{
		}

		protected Document createDefaultModel()
		{
			return new NumericDocument();
		}

		class NumericDocument extends PlainDocument
		{
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
			{
				try
				{
					Long.parseLong(str);
					super.insertString(offs, str, a);
				}
				catch (NumberFormatException nfe) { }
			}
		}
	}

	private class DateTextField extends JTextField
	{
		protected Document createDefaultModel()
		{
			return new DateDocument();
		}

		class DateDocument extends PlainDocument
		{
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
			{
				// "xx/xx/xxxx".length() == 10
				// "yyyyMMdd".length() == 8
				if(getLength() + str.length() > 8)
					return;

				String pre = getText(0, offs);
				String post = getText(offs, getLength() - offs);

				try {
					Long.parseLong(str);
					super.insertString(offs, str, a);
				} catch (NumberFormatException nfe) { }
				
				// Newer note: In case we ever go back to the MM/dd/yyyy format...
				// Note: This works for now, but a different control would be better...
				//if( (pre + str + post).matches("\\d{0,2}/?\\d{0,2}/?\\d{0,4}") )...
				// ...
			}
		}
	}
}
