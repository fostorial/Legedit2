package legedit2.gui.templatemanager;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import legedit2.cardtype.CardType;
import legedit2.definitions.Icon;
import legedit2.definitions.Icon.ICON_TYPE;
import legedit2.gui.LegeditFrame;
import legedit2.gui.dialogs.ManageIconPanel;
import legedit2.gui.dialogs.ManageTemplatesPanel;
import legedit2.gui.project.LegeditItemPreviewPanel;

public class TemplateManager extends JPanel implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 8680596845245244666L;
	
	private JSplitPane splitPane = new JSplitPane();
	
	private JPanel iconListPanel = new JPanel();
	private DefaultListModel<CardType> iconListModel = new DefaultListModel<>();
	private JList<CardType> iconList;
	private JScrollPane scroll = new JScrollPane();
	
	private LegeditItemPreviewPanel previewPanel;
	
	private JButton newIconButton = new JButton(" + ");
	
	public TemplateManager()
	{
		setLayout(new BorderLayout(1,1));
		splitPane.setContinuousLayout(true);
		splitPane.setRightComponent(new JPanel());
		splitPane.setBorder(null);
		splitPane.setDividerLocation(0.5d);
		splitPane.setResizeWeight(0.25d);
		add(splitPane, BorderLayout.CENTER);
		
		scroll.setViewportView(iconListPanel);
		scroll.setBorder(null);
		
		iconListPanel.setLayout(new BorderLayout(1, 1));
		splitPane.setLeftComponent(iconListPanel);
		
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		
		newIconButton.addActionListener(this);
		tb.add(newIconButton);
		
		iconListPanel.add(tb, BorderLayout.PAGE_START);
		
		iconList = new JList<>(iconListModel);
		for (CardType c : CardType.getCardTypes())
		{
			iconListModel.addElement(c);
		}
		iconList.setCellRenderer(new TemplateCellRenderer());
		iconList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		iconList.setEnabled(true);
		scroll.setViewportView(iconList);
		iconListPanel.add(scroll, BorderLayout.CENTER);
		
		iconList.addMouseListener(new MouseAdapter(){
		    @Override
		    public void mouseClicked(MouseEvent e){
		        if(e.getClickCount()==2){
		            selectCardTypeForEdit();
		        }
		    }
		});
		
		iconList.addListSelectionListener(this);
		
		previewPanel = new LegeditItemPreviewPanel();
		previewPanel.getCardMaker().setScale(0.5d);
		splitPane.setRightComponent(previewPanel);
	}
	
	public void resetIcons()
	{
		iconListModel.clear();
		
		for (CardType c : CardType.getCardTypes())
		{
			iconListModel.addElement(c);
		}
		
		iconList.setSelectedValue(CardType.getCardTypes().get(0), true);
	}
	
	private void selectCardTypeForEdit()
	{
		LegeditFrame.legedit.selectCardForEdit(iconList.getSelectedValue());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newIconButton))
		{
			
		}
	}
	

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (iconList.getSelectedValue() != null)
		{
			//System.out.println(iconList.getSelectedValue().getTemplateDisplayName());
		}
		previewPanel.setSelectedItemType(iconList.getSelectedValue());
		previewPanel.resetPreviewPanel();
	}

	public LegeditItemPreviewPanel getPreviewPanel() {
		return previewPanel;
	}

	public void setPreviewPanel(LegeditItemPreviewPanel previewPanel) {
		this.previewPanel = previewPanel;
	}

}
