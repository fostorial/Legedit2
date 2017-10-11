package legedit2.gui.project;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class ProjectPanelCombined extends ProjectPanel {

	private static final long serialVersionUID = -6132850075460022938L;
	
	private JSplitPane splitPane = new JSplitPane();
	
	public ProjectPanelCombined()
	{
		setLayout(new BorderLayout(0, 0));
		splitPane.setContinuousLayout(true);
		splitPane.setLeftComponent(new JList<>(new String[]{"Name"}));
		splitPane.setRightComponent(new JPanel());
		splitPane.setBorder(null);
		//System.out.println(splitPane.getBounds());
		add(splitPane, BorderLayout.CENTER);
		
		setDeckPanel(new DeckCardSelectionPanel());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
