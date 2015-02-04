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

import streams.SenfInputStream;
import streams.ZipStream;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.util.zip.*;
import org.apache.tika.Tika;

class SenfAnalyzer extends JDialog
{
	private JButton 	closeButton;

	private JPanel 		buttonPanel,
				textPanel;

	private JScrollPane 	textScrollPane;

	private JTextArea 	textArea;

	private Highlighter.HighlightPainter highlightPainter;

	private ZipFile zipfile;

	private SenfResult result;
	private final int bytestoread = 50;
	private final int offsetcontext = 10;
	private char[] readin;
	private BufferedReader br;
	private Tika tika;

	public SenfAnalyzer(SenfResult resultset, Frame parent, boolean modal)
	{
		super(parent, modal);
		zipfile = null;
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Senf Analyzer");
		setResizable(false);
		addWindowListener(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					Tidy();
				}
			}
		);

		textPanel = new JPanel();
		textScrollPane = new JScrollPane();
		textArea = new JTextArea();
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		closeButton = new JButton("Close");

		closeButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					closeButton_click();
				}
			}
		);

		buttonPanel.add(closeButton);

		textScrollPane.setViewportView(textArea);
		textArea.setColumns(30);
		textArea.setRows(20);
		textArea.setEditable(false);

		textPanel.add(textScrollPane);

		getContentPane().add(textPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(closeButton);

		pack();

		this.result = resultset;
		this.readin = new char[bytestoread];
		tika = new Tika();

		//TODO: Add in parsing to display filetypes that need parsed...
		BufferedInputStream bis;
		try
		{
			if( result.getSS() instanceof ZipStream )
			{
				String container = ( (ZipStream)result.getSS() ).getPath();
				String name = result.getSS().getName();

				ZipFile z = new ZipFile( container );
				bis = new BufferedInputStream( z.getInputStream( z.getEntry( name ) ) );
			}
			else
				bis = new BufferedInputStream( result.getSS().getInputStream() );
			try {
				br = new BufferedReader(tika.parse(bis));
				br.mark(1);
				br.read();
				br.reset();
			} catch(IOException ioe) {
				br = new BufferedReader( new InputStreamReader( result.getSS().getInputStream() ) );
			}
		}
		catch( Exception E )
		{
			E.printStackTrace();
			textArea.setText("Error loading file!");
			return;
		}

		highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);

		UpdateViewer();

	}
	
	public void runDialog()
	{
		this.setVisible(true);
	}

	private void closeButton_click()
	{
		Tidy();
		this.setVisible(false);
	}

	private void Tidy()
	{
		try {
			if(br != null)
				br.close();

			if(zipfile != null)
				zipfile.close();

		} catch (IOException io) { }

		br = null;
		zipfile = null;
	}

	private void UpdateViewer()
	{
		SenfResult.result_entry[] results = result.getResults();

		int[] highlights = new int[results.length];

		StringBuffer sb = new StringBuffer();
		String newline = System.getProperty( "line.separator" );
		long read = 0;
		long skip = 0;

		try 
		{
			for(int i = 0; i < results.length; ++i)
			{
				sb.append("Result " + (i + 1) + ": " + newline);
				skip = Math.max(0, results[i].offset - offsetcontext - read);

				br.skip(skip);
				br.mark(bytestoread + 1);	
				read += skip;

				br.read(readin, 0, bytestoread);
				br.reset();

				highlights[i] = sb.length();

				if(results[i].offset - offsetcontext - read >= 0)
					highlights[i] += offsetcontext;
				
				sb.append(readin);
				sb.append(newline + newline);
			}
		} 
		catch (IOException io)
		{
			sb.append( "There was an error reading the file: " + io);
			io.printStackTrace();
		}

		textArea.setText(sb.toString());

		textArea.setCaretPosition(0);

		Highlighter hl = textArea.getHighlighter();

		try {
			for(int i = 0; i < highlights.length; ++i)
				hl.addHighlight(highlights[i], highlights[i] + results[i].length, highlightPainter);
		}
		catch (Exception e)
		{

		}
	}
}
