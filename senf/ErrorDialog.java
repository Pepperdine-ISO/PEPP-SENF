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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import java.net.URL;

public class ErrorDialog extends JDialog
{
	private JButton okButton;
	private JLabel  errorLabel;
	private JPanel  errorPanel,
			buttonPanel,
			imagePanel;

	public ErrorDialog(String error, Frame parent, boolean modal)
	{
		super(parent, modal);
		//setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setSize(300,175);
		setTitle("Error!");

		errorLabel  = new JLabel(error);
		errorPanel  = new JPanel(new BorderLayout());
		okButton    = new JButton("OK");
		buttonPanel = new JPanel();
		imagePanel  = new JPanel(new BorderLayout());

		okButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					okButton_click();
				}
			}
		);
		imagePanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
		imagePanel.add(new JLabel(new ImageIcon(ErrorDialog.class.getResource("images/mutardo_err.png"))), BorderLayout.CENTER);
		errorPanel.add(imagePanel, BorderLayout.WEST);
		errorPanel.add(errorLabel, BorderLayout.CENTER);
		getContentPane().add(errorPanel, BorderLayout.CENTER);

		buttonPanel.add(okButton);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}

	public void runDialog()
	{
		this.setVisible(true);
	}

	private void okButton_click()
	{
		this.setVisible(false);
	}
}
