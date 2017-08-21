package legedit2.gui.menubar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import legedit2.definitions.LegeditItem;
import legedit2.exporters.LegeditExportDialog;
import legedit2.exporters.LegeditExporter;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;

public class FileMenu extends JMenu implements ActionListener {
	
	private static final long serialVersionUID = 6182281942894446671L;
	
	private JMenuItem newExpansion = new JMenuItem("New...");
	
	private JMenuItem open = new JMenuItem("Open...");
	
	private JMenuItem save = new JMenuItem("Save");
	
	private JMenuItem saveAs = new JMenuItem("Save As...");
	
	private JMenuItem exportSingleImages = new JMenuItem("Export Single Images...");
	
	private JMenuItem exit = new JMenuItem("Exit");
	
	public FileMenu()
	{
		this.setText("File");
		
		this.add(newExpansion);
		
		this.addSeparator();
		
		this.add(open);
		
		this.addSeparator();
		
		this.add(save);
		
		this.add(saveAs);
		
		this.addSeparator();
		
		this.add(exportSingleImages);
		
		this.addSeparator();
		
		this.add(exit);
		
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
		
		if (e.getSource().equals(save))
		{	
			String saveFile = LegeditHelper.getProperty(legedit2.helpers.LegeditHelper.PROPERTIES.lastExpansion);
			if (saveFile != null && new File(saveFile).exists())
			{
				ProjectHelper.saveProject(new File(saveFile));
			}
			else
			{
				JFileChooser chooser = new JFileChooser(LegeditHelper.getLastOpenDirectory());
				int outcome = chooser.showSaveDialog(this);
				if (outcome == JFileChooser.APPROVE_OPTION)
				{
					ProjectHelper.saveProject(chooser.getSelectedFile());
					LegeditHelper.setLastOpenDirectory(chooser.getSelectedFile().getAbsolutePath());
					LegeditHelper.setProperty(legedit2.helpers.LegeditHelper.PROPERTIES.lastExpansion, chooser.getSelectedFile().getAbsolutePath());
				}
			}
		}
		
		if (e.getSource().equals(saveAs))
		{
			JFileChooser chooser = new JFileChooser(LegeditHelper.getLastOpenDirectory());
			int outcome = chooser.showSaveDialog(this);
			if (outcome == JFileChooser.APPROVE_OPTION)
			{
				ProjectHelper.saveProject(chooser.getSelectedFile());
				LegeditHelper.setLastOpenDirectory(chooser.getSelectedFile().getAbsolutePath());
				LegeditHelper.setProperty(legedit2.helpers.LegeditHelper.PROPERTIES.lastExpansion, chooser.getSelectedFile().getAbsolutePath());
			}
		}
		
		if (e.getSource().equals(open))
		{
			JFileChooser chooser = new JFileChooser(LegeditHelper.getLastOpenDirectory());
			int outcome = chooser.showOpenDialog(this);
			if (outcome == JFileChooser.APPROVE_OPTION)
			{
				LegeditHelper.setProperty(legedit2.helpers.LegeditHelper.PROPERTIES.lastExpansion, chooser.getSelectedFile().getAbsolutePath());
				ProjectHelper.loadProject(chooser.getSelectedFile());
				LegeditHelper.setLastOpenDirectory(chooser.getSelectedFile().getAbsolutePath());
			}
		}
		
		if (e.getSource().equals(newExpansion))
		{
			ProjectHelper.newProject();
		}
		
		if (e.getSource().equals(exportSingleImages))
		{
			LegeditExportDialog.exportSingleImages();
		}
		
		if (e.getSource().equals(exit))
		{
			LegeditHelper.handleWindowCloseSave();
			
			System.exit(0);
		}
	}

}
