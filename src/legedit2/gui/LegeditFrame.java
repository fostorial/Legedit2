package legedit2.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javafx.stage.Popup;
import legedit2.card.Card;
import legedit2.cardtype.CardType;
import legedit2.gui.config.ConfigPanel;
import legedit2.gui.editor.CardEditorPanel;
import legedit2.gui.editor.CardTypeEditorPanel;
import legedit2.gui.menubar.LegeditMenuBar;
import legedit2.gui.menubar.LegeditMenuBar.MENU_MODE;
import legedit2.gui.menubar.ViewMenu.EDITOR_MODE;
import legedit2.gui.menubar.ViewMenu.VIEW_MODE;
import legedit2.gui.project.ProjectPanel;
import legedit2.gui.project.ProjectPanelClassic;
import legedit2.gui.project.ProjectPanelColumns;
import legedit2.gui.project.ProjectPanelCombined;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;

public class LegeditFrame extends JFrame {
	private static final long serialVersionUID = -81856009542857672L;
	
	public static LegeditFrame legedit;
	
	public static VIEW_MODE viewMode = VIEW_MODE.COLUMNS;
	public static EDITOR_MODE editorMode = EDITOR_MODE.TABBED;
	
	private JTabbedPane tabs;
	private ProjectPanel projectPanel;

	public LegeditFrame()
	{
		legedit = this;
		
		LegeditHelper.loadProperties();
		
		this.setTitle(LegeditHelper.getFrameName());
		
		setupGUI();
		setupMenus();
		setupWindow();
		
		try 
		{	
			String fontFolder = LegeditHelper.getFontPath("");

			File dir = new File(fontFolder);
			if (dir.exists())
			{
				// Register packaged fonts
				GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

				for (File f : dir.listFiles())
				{
					if (f.getName().toLowerCase().endsWith(".otf") || f.getName().toLowerCase().endsWith(".ttf"))
					{
						Font font = Font.createFont(Font.TRUETYPE_FONT, f);
						LegeditHelper.AddFontFamily(f.getPath(), font);
						env.registerFont(font);
					}
				}
			}			
		} 
		catch (Exception e1) 
		{
		}
		
		String saveFile = LegeditHelper.getProperty(legedit2.helpers.LegeditHelper.PROPERTIES.lastExpansion);
		if (saveFile != null && new File(saveFile).exists())
		{
			ProjectHelper.loadProject(new File(saveFile));
		}
	}
	
	public static void refreshGUI()
	{
		legedit.resetGUI();
	}
	
	
	
	private void setupGUI()
	{
		this.setLayout(new GridLayout(1, 1));
		if (editorMode.equals(EDITOR_MODE.TABBED))
		{
			setupTabs();
		}
		else
		{
			/* Initialise project component */
			initialiseProjectPanel();
			this.add(projectPanel, BorderLayout.CENTER);
		}
		
		validate();
	}
	
	private void initialiseProjectPanel()
	{
		projectPanel = null; 
		switch(viewMode)
		{
		case COLUMNS:
			projectPanel = new ProjectPanelColumns();
			break;
		case CLASSIC:
			projectPanel = new ProjectPanelClassic();
			break;
		case COMBINED:
			/* Fall back to default */
		default:
			projectPanel = new ProjectPanelCombined();
		}
	}
	
	private void setupTabs()
	{
		tabs = new JTabbedPane();
		this.add(tabs, BorderLayout.CENTER);
		
		/* Initialise project component */
		initialiseProjectPanel();
		tabs.add("Project", projectPanel);
		
		tabs.add("Configuration", new ConfigPanel());
	}
	
	private void setupMenus()
	{
		LegeditMenuBar legeditMenuBar = new LegeditMenuBar();
		legeditMenuBar.setupMenus(MENU_MODE.MAIN);
		setJMenuBar(legeditMenuBar);
	}
	
	private void setupWindow()
	{
		this.setSize(900, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	LegeditHelper.handleWindowCloseSave();
            	
            	e.getWindow().dispose();
        		System.exit(0);
            }
		});
		
		this.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException e)
		{
			// handle exception
		}
		catch (ClassNotFoundException e)
		{
			// handle exception
		}
		catch (InstantiationException e)
		{
			// handle exception
		}
		catch (IllegalAccessException e)
		{
			// handle exception
		}
		
		new LegeditFrame();
	}
	
	private void resetGUI()
	{	
		for (Component c : legedit.getComponents())
		{
			if (c.equals(tabs))
			{
				legedit.remove(tabs);
			}
			if (c.equals(projectPanel))
			{
				legedit.remove(projectPanel);				
			}
			recursiveComponentSearch(c);
		}
		legedit.setupGUI();
		((LegeditMenuBar)legedit.getJMenuBar()).setupMenus(((LegeditMenuBar)legedit.getJMenuBar()).getMenuMode());
		
		this.setTitle(LegeditHelper.getFrameName());
	}
	
	private void recursiveComponentSearch(Component comp)
	{
		
		if (comp instanceof JPanel)
		{
			for (Component c : ((JPanel)comp).getComponents())
			{
				if (c.equals(tabs))
				{
					legedit.remove(tabs);				
				}
				if (c.equals(projectPanel))
				{
					legedit.remove(projectPanel);				
				}
				recursiveComponentSearch(c);
			}
		}
		
		if (comp instanceof JRootPane)
		{
			for (Component c : ((JRootPane)comp).getComponents())
			{
				if (c.equals(tabs))
				{
					legedit.remove(tabs);				
				}
				if (c.equals(projectPanel))
				{
					legedit.remove(projectPanel);				
				}
				recursiveComponentSearch(c);
			}
		}
		
		if (comp instanceof JLayeredPane)
		{
			for (Component c : ((JLayeredPane)comp).getComponents())
			{
				if (c.equals(tabs))
				{
					//System.out.println("Removing Tabs");
					legedit.remove(tabs);				
				}
				if (c.equals(projectPanel))
				{
					//System.out.println("Removing Project Panel");
					legedit.remove(projectPanel);				
				}
				recursiveComponentSearch(c);
			}
		}
	}
	
	public void selectCardForEdit(Card c)
	{
		if (editorMode.equals(EDITOR_MODE.TABBED))
		{
			boolean found = false;
			for (int i = 0; i < tabs.getTabCount(); i++)
			{
				if (tabs.getComponentAt(i) instanceof CardEditorPanel
						&& ((CardEditorPanel)tabs.getComponentAt(i)).getSelectedCard().equals(c))
				{
					tabs.setSelectedIndex(i);
					found = true;
					break;
				}
			}
			
			if (found == false)
			{
				CardEditorPanel editorPanel = new CardEditorPanel();
				editorPanel.setSelectedCard(c);
				legedit.tabs.add("" + c.getCardName(), editorPanel);
				tabs.setSelectedIndex(tabs.getTabCount()-1);
			}
		}
		else
		{
			
		}
		
	}
	
	public void selectCardForEdit(CardType c)
	{
		if (editorMode.equals(EDITOR_MODE.TABBED))
		{
			boolean found = false;
			for (int i = 0; i < tabs.getTabCount(); i++)
			{
				if (tabs.getComponentAt(i) instanceof CardEditorPanel
						&& ((CardEditorPanel)tabs.getComponentAt(i)).getSelectedCard().equals(c.getCopy()))
				{
					tabs.setSelectedIndex(i);
					found = true;
					break;
				}
			}
			
			if (found == false)
			{
				CardTypeEditorPanel editorPanel = new CardTypeEditorPanel();
				editorPanel.setSelectedCard(c.getCopy());
				legedit.tabs.add("" + c.getTemplateDisplayName(), editorPanel);
				tabs.setComponentAt(1, editorPanel);
				tabs.setSelectedIndex(tabs.getTabCount()-1);
			}
		}
		else
		{
			
		}
		
	}
	
	public void closeTab()
	{
		if (editorMode.equals(EDITOR_MODE.TABBED))
		{
			int i = tabs.getSelectedIndex();
			
			if (i >= 0 && tabs.getComponentAt(i) instanceof CardEditorPanel)
			{
				tabs.remove(i);
				tabs.setSelectedIndex(0);
			}
		}
		else
		{
			
		}
	}
	
	public Card getCardFromTab()
	{
		if (editorMode.equals(EDITOR_MODE.TABBED))
		{
			int i = tabs.getSelectedIndex();
			
			if (i >= 0 && tabs.getComponentAt(i) instanceof CardEditorPanel)
			{
				return ((CardEditorPanel)tabs.getComponentAt(i)).getSelectedCard();
			}
		}
		else
		{
			return null;
		}
		return null;
	}
}
