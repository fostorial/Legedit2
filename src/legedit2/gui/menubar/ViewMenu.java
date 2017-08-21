package legedit2.gui.menubar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import legedit2.gui.LegeditFrame;
import legedit2.helpers.LegeditHelper;

public class ViewMenu extends JMenu implements ActionListener {

	private static final long serialVersionUID = 7518481957984128946L; 
	
	public enum VIEW_MODE {CLASSIC, COLUMNS, COMBINED};
	public enum EDITOR_MODE {TABBED, WINDOWED};
	
	private JRadioButtonMenuItem listModeClassic = new JRadioButtonMenuItem("Classic");
	private JRadioButtonMenuItem listModeCombined = new JRadioButtonMenuItem("Combined");
	private JRadioButtonMenuItem listModeColumns = new JRadioButtonMenuItem("Legedit 2");
	
	private JRadioButtonMenuItem editorModeTabbed = new JRadioButtonMenuItem("Tabbed");
	private JRadioButtonMenuItem editorModeWindowed = new JRadioButtonMenuItem("New Window");
	
	public ViewMenu()
	{
		this.setText("View");
		
		JMenuItem labelListMode = new JMenuItem("List View Mode");
		labelListMode.setEnabled(false);
		this.add(labelListMode);
		
		this.add(listModeClassic);
		//this.add(listModeCombined);
		this.add(listModeColumns);
		listModeColumns.setSelected(true);
		
		ButtonGroup group = new ButtonGroup();
		group.add(listModeClassic);
		group.add(listModeCombined);
		group.add(listModeColumns);
		
		addSeparator();
		
		JMenuItem labelEditorMode = new JMenuItem("Editor Mode");
		labelEditorMode.setEnabled(false);
		this.add(labelEditorMode);
		
		this.add(editorModeTabbed);
		this.add(editorModeWindowed);
		editorModeTabbed.setSelected(true);
		
		group = new ButtonGroup();
		group.add(editorModeTabbed);
		group.add(editorModeWindowed);
		
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
		if (e.getSource().equals(listModeClassic) || e.getSource().equals(listModeColumns) || e.getSource().equals(listModeCombined))
		{
			if (e.getSource().equals(listModeClassic))
			{
				LegeditFrame.viewMode = VIEW_MODE.CLASSIC;
			}
			if (e.getSource().equals(listModeColumns))
			{
				LegeditFrame.viewMode = VIEW_MODE.COLUMNS;
			}
			if (e.getSource().equals(listModeCombined))
			{
				LegeditFrame.viewMode = VIEW_MODE.COMBINED;
			}
			LegeditHelper.resetGUI();
		}
		
		if (e.getSource().equals(editorModeTabbed) || e.getSource().equals(editorModeWindowed))
		{
			if (e.getSource().equals(editorModeTabbed))
			{
				LegeditFrame.editorMode = EDITOR_MODE.TABBED;
			}
			if (e.getSource().equals(editorModeWindowed))
			{
				LegeditFrame.editorMode = EDITOR_MODE.WINDOWED;
			}
			LegeditHelper.resetGUI();
		}
	}
}
