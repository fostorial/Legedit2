package legedit2.gui.config;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import legedit2.definitions.Icon;
import legedit2.definitions.Icon.ICON_TYPE;
import legedit2.gui.LegeditFrame;
import legedit2.gui.dialogs.ManageIconPanel;
import legedit2.helpers.LegeditHelper;

public class IconManager extends JPanel implements ActionListener, ItemListener, ListSelectionListener {

	private static final long serialVersionUID = 8680596845245244666L;
	
	private JSplitPane splitPane = new JSplitPane();
	
	private JPanel iconListPanel = new JPanel();
	private JComboBox<ICON_TYPE> iconTypeFilter;
	private DefaultListModel<Icon> iconListModel = new DefaultListModel<>();
	private JList<Icon> iconList;
	private JScrollPane scroll = new JScrollPane();
	
	private ManageIconPanel managePanel;
	
	private JButton newIconButton = new JButton(" + ");
	private JButton deleteIconButton = new JButton(" - ");
	
	public IconManager()
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
		
		iconTypeFilter = new JComboBox<ICON_TYPE>();
		iconTypeFilter.addItem(ICON_TYPE.NONE);
		for (ICON_TYPE g : ICON_TYPE.values())
		{
			if (!g.equals(ICON_TYPE.NONE))
			{
				iconTypeFilter.addItem(g);
			}
		}
		iconTypeFilter.setRenderer(new IconTypeListCellRenderer());
		iconTypeFilter.addItemListener(this);
		
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		tb.add(iconTypeFilter);
		
		newIconButton.addActionListener(this);
		tb.add(newIconButton);
		
		deleteIconButton.addActionListener(this);
		tb.add(deleteIconButton);
		
		iconListPanel.add(tb, BorderLayout.PAGE_START);
		
		iconList = new JList<>(iconListModel);
		Collections.sort(Icon.values());
		for (Icon c : Icon.values())
		{
			if (!c.getIconType().equals(ICON_TYPE.NONE))
			{
				iconListModel.addElement(c);
			}
		}
		iconList.setCellRenderer(new IconCellRenderer());
		iconList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		iconList.setEnabled(true);
		scroll.setViewportView(iconList);
		iconListPanel.add(scroll, BorderLayout.CENTER);
		
		iconList.addListSelectionListener(this);
		
		managePanel = new ManageIconPanel();
		managePanel.setIconManager(this);
		splitPane.setRightComponent(managePanel);
	}
	
	public void resetIcons()
	{
		iconListModel.clear();
		
		Icon.loadIcons();
		Collections.sort(Icon.values());
		
		for (Icon c : Icon.values())
		{
			if (!c.getIconType().equals(ICON_TYPE.NONE))
			{
				iconListModel.addElement(c);
			}
		}
		
		iconList.setSelectedValue(Icon.values().get(0), true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newIconButton))
		{
			managePanel.setSelectedItem(null);
			managePanel.setAddMode(true);
			managePanel.resetFields();
		}
		
		if (e.getSource().equals(deleteIconButton))
		{
			Icon icon = iconList.getSelectedValue();
			if (icon != null)
			{
				iconListModel.removeElement(icon);
				Icon.values().remove(icon);
				Icon.saveIconDefinitions();
				
				managePanel.setSelectedItem(null);
				managePanel.resetFields();
			}
			else
			{
				JOptionPane.showMessageDialog(LegeditFrame.legedit, "No icon selected", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		resetIconsByType((ICON_TYPE)e.getItem());
	}
	
	private void resetIconsByType(ICON_TYPE group)
	{
		iconListModel.clear();
		
		for (Icon c : Icon.values())
		{
			if (group != null && !group.equals(ICON_TYPE.NONE))
			{
				if (c.getIconType().equals(group))
				{
					iconListModel.addElement(c);
				}
			}
			else
			{
				iconListModel.addElement(c);
			}			
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (iconList.getSelectedValue() != null)
		{
			System.out.println(iconList.getSelectedValue().getTagName());
		}
		managePanel.setAddMode(false);
		managePanel.setSelectedItem(iconList.getSelectedValue());
		managePanel.resetFields();
	}

}
