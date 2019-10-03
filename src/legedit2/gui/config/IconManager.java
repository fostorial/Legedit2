package legedit2.gui.config;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

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
import legedit2.gui.LegeditFrame;
import legedit2.gui.dialogs.ManageIconPanel;
import legedit2.helpers.LegeditHelper;

public class IconManager extends JPanel implements ActionListener, ItemListener, ListSelectionListener {

	private static final long serialVersionUID = 8680596845245244666L;
	
	private JSplitPane splitPane = new JSplitPane();
	
	private JPanel iconListPanel = new JPanel();
	private JComboBox<String> iconTypeFilter;
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
				
		iconTypeFilter = new JComboBox<String>();
		iconTypeFilter.addItem("");
		for (String g : Icon.categories())
		{
			if (!g.equals(""))
			{
				iconTypeFilter.addItem(g);
			}
		}
		
		iconTypeFilter.setRenderer(new legedit2.helpers.IconTypeListCellRenderer());
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
		for (Icon c : Icon.sorted_values())
		{
			if (c.getCategory() != null && !c.getCategory().isEmpty())
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
		
		String filteredCategory = (String)iconTypeFilter.getSelectedItem();
		if (filteredCategory.equals(""))
		{
			List<Icon> icons = Icon.sorted_values(); 
			for (Icon c : icons)
			{
				if (c.getCategory() != null && !c.getCategory().isEmpty())
				{
					iconListModel.addElement(c);
				}
			}

			iconList.setSelectedValue(icons.get(0), true);
		}
		else
		{
			resetIconsByType(filteredCategory);
		}
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
				//Icon.values().remove(icon);
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
		resetIconsByType((String)e.getItem());
	}
	
	private void resetIconsByType(String category)
	{
		iconListModel.clear();
		String categoryName = null;
		if (category != null)
		{
			categoryName = category.toUpperCase();
		}
		
		for (Icon c : Icon.sorted_values())
		{
			if (!c.getCategory().equals(""))
			{
				if (categoryName != null && !categoryName.isEmpty())
				{
					if (c.getCategory().equals(categoryName))
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
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (iconList.getSelectedValue() != null)
		{
			//System.out.println(iconList.getSelectedValue().getTagName());
		}
		managePanel.setAddMode(false);
		managePanel.setSelectedItem(iconList.getSelectedValue());
		managePanel.resetFields();
	}

}
