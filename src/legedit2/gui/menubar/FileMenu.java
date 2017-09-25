package legedit2.gui.menubar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import legedit2.exporters.LegeditExportDialog;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;
import legedit2.tools.legeditimporter.LegeditImporter;

public class FileMenu extends JMenu implements ActionListener {
	
	private static final long serialVersionUID = 6182281942894446671L;
	
	private JMenuItem newExpansion = new JMenuItem("New...");
	
	private JMenuItem open = new JMenuItem("Open...");
	
	private JMenuItem save = new JMenuItem("Save");
	
	private JMenuItem saveAs = new JMenuItem("Save As...");
	
	private JMenu exportMenu = new JMenu("Export");
	
	private JMenuItem exportSingleImages = new JMenuItem("Export Single Images...");
	private JMenuItem exportSingleImagesBleed = new JMenuItem("Export Single Images With Bleed...");
	private JMenuItem export3By3 = new JMenuItem("Export 3x3 Pages...");
	
	private JMenuItem importLegeditDef = new JMenuItem("Import Legedit Def...");
	
	private JMenuItem exit = new JMenuItem("Exit");
	
	public FileMenu()
	{
		this.setText("File");
		
		this.add(newExpansion);
		
		this.addSeparator();
		
		this.add(open);
		
		this.add(importLegeditDef);
		
		this.addSeparator();
		
		this.add(save);
		
		this.add(saveAs);
		
		this.addSeparator();
		
		this.add(exportMenu);
		
		exportMenu.add(exportSingleImages);
		exportMenu.add(exportSingleImagesBleed);
		exportMenu.add(export3By3);
		
		this.addSeparator();
		
		this.add(exit);
		
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
		
		if (e.getSource().equals(importLegeditDef))
		{
			JFileChooser chooser = new JFileChooser(LegeditHelper.getLastOpenDirectory());
			int outcome = chooser.showOpenDialog(this);
			if (outcome == JFileChooser.APPROVE_OPTION)
			{
				new LegeditImporter(chooser.getSelectedFile());
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
		
		if (e.getSource().equals(exportSingleImagesBleed))
		{
			LegeditExportDialog.exportSingleImagesWithBleed();
		}
		
		if (e.getSource().equals(export3By3))
		{
			LegeditExportDialog.export3By3Pages();
		}
		
		if (e.getSource().equals(exit))
		{
			LegeditHelper.handleWindowCloseSave();
			
			System.exit(0);
		}
	}

}
