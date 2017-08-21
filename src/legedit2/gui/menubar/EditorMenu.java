package legedit2.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import legedit2.gui.LegeditFrame;

public class EditorMenu  extends JMenu implements ActionListener {

	private static final long serialVersionUID = -759016954036126341L;
	
	private JMenuItem closeTab = new JMenuItem("Close Tab");
	
	public EditorMenu() {
		setText("Editor");
		closeTab.addActionListener(this);
		this.add(closeTab);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(closeTab))
		{
			LegeditFrame.legedit.closeTab();
		}
	}

}
