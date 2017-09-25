package legedit2.gui.menubar;

import javax.swing.JMenuBar;

import legedit2.gui.LegeditFrame;
import legedit2.gui.menubar.ViewMenu.EDITOR_MODE;

public class LegeditMenuBar extends JMenuBar {
	
	private static final long serialVersionUID = -876845867333592473L;

	public enum MENU_MODE {MAIN};
	private MENU_MODE menuMode = MENU_MODE.MAIN;
	
	private FileMenu fileMenu = new FileMenu();
	private ViewMenu viewMenu = new ViewMenu();
	private ConfigMenu configMenu = new ConfigMenu();
	private EditorMenu editorMenu = new EditorMenu();
	private ToolsMenu toolsMenu = new ToolsMenu();
	
	public void setupMenus(MENU_MODE menuMode)
	{
		this.add(fileMenu);
		this.add(editorMenu);
		//this.add(viewMenu);
		this.add(configMenu);
		this.add(toolsMenu);
		
		this.setMenuMode(menuMode);
		switch (menuMode)
		{
		case MAIN:
			setupMainMenu();
			break;
		default: 
			setupMainMenu();
			break;
		}
	}
	
	public void setupMainMenu()
	{
		fileMenu.setVisible(true);
		viewMenu.setVisible(true);
		
		if (LegeditFrame.editorMode.equals(EDITOR_MODE.WINDOWED))
		{
			configMenu.setVisible(true);
		}
		else
		{
			configMenu.setVisible(false);
		}
	}

	public MENU_MODE getMenuMode() {
		return menuMode;
	}

	public void setMenuMode(MENU_MODE menuMode) {
		this.menuMode = menuMode;
	}
}
