package legedit2.gui.menubar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import legedit2.exporters.LegeditExportDialog;
import legedit2.gui.LegeditFrame;

public class EditorMenu  extends JMenu implements ActionListener {

	private static final long serialVersionUID = -759016954036126341L;
	
	private JMenu exportMenu = new JMenu("Export");
	private JMenuItem exportSingleImages = new JMenuItem("Export Single Images...");
	private JMenuItem exportSingleImagesBleed = new JMenuItem("Export Single Images With Bleed...");
	
	private JMenuItem closeTab = new JMenuItem("Close Tab");
	
	public EditorMenu() {
		setText("Editor");
		
		exportMenu.add(exportSingleImages);
		exportMenu.add(exportSingleImagesBleed);
		this.add(exportMenu);
		
		this.add(closeTab);
		
		for (Component c : this.getMenuComponents())
		{
			if (c instanceof JMenuItem)
			{
				((JMenuItem)c).addActionListener(this);
			}
			
			if (c instanceof JMenu)
			{
				for (Component mc : ((JMenu)c).getMenuComponents())
				{
					if (mc instanceof JMenuItem)
					{
						((JMenuItem)mc).addActionListener(this);
					}
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(closeTab))
		{
			LegeditFrame.legedit.closeTab();
		}
		
		if (e.getSource().equals(exportSingleImages))
		{
			if (LegeditFrame.legedit.getCardFromTab() != null)
			{
				LegeditExportDialog.exportSingleImage(LegeditFrame.legedit.getCardFromTab());
			}
		}
		
		if (e.getSource().equals(exportSingleImagesBleed))
		{
			if (LegeditFrame.legedit.getCardFromTab() != null)
			{
				LegeditExportDialog.exportSingleImageWithBleed(LegeditFrame.legedit.getCardFromTab());
			}
		}
	}

}
