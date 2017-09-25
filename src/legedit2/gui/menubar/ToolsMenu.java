package legedit2.gui.menubar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import legedit2.tools.bleeder.LegeditBleeder;

public class ToolsMenu extends JMenu implements ActionListener {

	private static final long serialVersionUID = 7518481957984128946L; 
	
	private JMenuItem bleeder = new JMenuItem("Bleeder...");
	
	public ToolsMenu()
	{
		this.setText("Tools");
		
		this.add(bleeder);
		
		for (Component c : this.getMenuComponents())
		{
			if (c instanceof JMenuItem)
			{
				((JMenuItem)c).addActionListener(this);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(bleeder))
		{
			new LegeditBleeder();
		}
	}
}
