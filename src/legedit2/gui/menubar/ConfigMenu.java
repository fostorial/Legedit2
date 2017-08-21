package legedit2.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import legedit2.gui.LegeditFrame;
import legedit2.gui.config.IconManager;

public class ConfigMenu extends JMenu implements ActionListener{
	private static final long serialVersionUID = 4887763138940803765L;
	
	private JMenuItem iconManager = new JMenuItem("Icon Manager"); 

	public ConfigMenu()
	{
		setText("Configuration");
		iconManager.addActionListener(this);
		add(iconManager);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(iconManager))
		{
			JDialog d = new JDialog(LegeditFrame.legedit, true);
			d.setContentPane(new IconManager());
			d.setSize(600, 500);
			d.setTitle("Icon Manager");
			d.setVisible(true);
		}
	}
}
