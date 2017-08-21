package legedit2.gui.config;

import javax.swing.JTabbedPane;

import legedit2.gui.templatemanager.TemplateManager;

public class ConfigPanel extends JTabbedPane {

	private static final long serialVersionUID = -8801985527441467584L;

	public ConfigPanel()
	{
		add("Icon Manager", new IconManager());
		add("Template Manager", new TemplateManager());
	}
}
