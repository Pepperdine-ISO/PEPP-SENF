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

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import java.net.URL;

class SenfAbout extends JDialog
{
	public SenfAbout(Frame parent, boolean modal)
	{
		super(parent, modal);

		//getRootPane().setWindowDecorationStyle(javax.swing.JRootPane.NONE);

		setTitle("About senf forecast");

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);

		JLabel picture = new JLabel(new ImageIcon(SenfAbout.class.getResource("images/mulogo.png")), SwingConstants.CENTER);
		JLabel aboutLabel = new JLabel("senf forecast: only slightly less overhead than your typical cat");
		JLabel senfGUIVersionLabel = new JLabel("senf forecast version: " + SenfGUI.VERSION, SwingConstants.CENTER);
		JLabel senfVersionLabel = new JLabel("senf version: " + Senf.VERSION, SwingConstants.CENTER);
		JLabel senfBaseVersionLabel = new JLabel("Based on senf version: " + Senf.BASE_VERSION, SwingConstants.CENTER);
		JLabel urlLabel = new JLabel("https://source.its.utexas.edu/groups/its-iso/projects/senf", SwingConstants.CENTER);

		picture.setAlignmentX(Component.CENTER_ALIGNMENT);
		aboutLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		senfGUIVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		senfVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		senfBaseVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		urlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		mainPanel.add(picture);
		mainPanel.add(aboutLabel);
		mainPanel.add(senfGUIVersionLabel);
		mainPanel.add(senfVersionLabel);
		mainPanel.add(senfBaseVersionLabel);
		mainPanel.add(urlLabel);
		
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		JPanel buttPanel = new JPanel();
		JButton quitButton = new JButton("Close");
		quitButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setVisible(false);
				}
			}
		);
		buttPanel.add(quitButton);
		mainPanel.add(buttPanel);

		getContentPane().add(mainPanel);

		pack();
	}

	public void runDialog()
	{
		this.setVisible(true);
	}
}
