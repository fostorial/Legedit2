package legedit2.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import legedit2.card.Card;
import legedit2.cardgroup.CardGroup;
import legedit2.cardgroup.CardGroupListCellRenderer;
import legedit2.cardtype.CardType;
import legedit2.cardtype.Style;
import legedit2.decktype.DeckType;
import legedit2.definitions.ItemType;
import legedit2.gui.project.LegeditItemPreviewPanel;

public class AddLegeditItemDialog extends JDialog implements ActionListener, ItemListener, ListSelectionListener {
	
	final static int OK_OPTION = JOptionPane.OK_OPTION;
	final static int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;

	private static final long serialVersionUID = -2995580312503623356L;
	
	private JSplitPane splitPane = new JSplitPane();
	
	private JPanel itemTypeListPanel = new JPanel();
	private JComboBox<CardGroup> itemTypeTypeFilter;
	private DefaultListModel<ItemType> itemTypeListModel = new DefaultListModel<>();
	private JList<ItemType> itemTypeList;
	private JScrollPane scroll = new JScrollPane();
	
	private int outcome = CANCEL_OPTION;
	private ItemType selectedItemType = null;
	
	private JButton addButton = new JButton("Add");
	private JButton cancelButton = new JButton("Cancel");
	
	private JPanel previewHolderPanel;
	private JComboBox<Style> styleTypeFilter;
	private LegeditItemPreviewPanel previewPanel;
	
	public AddLegeditItemDialog(DeckType deckType)
	{
		super();
		setModal(true);
		setSize(600, 500);
		setTitle("Select an item type...");
		
		previewPanel = new LegeditItemPreviewPanel();
		previewPanel.getCardMaker().setScale(0.3d);

		previewHolderPanel = new JPanel();
		previewHolderPanel.setLayout(new BorderLayout(1, 1));
		previewHolderPanel.add(previewPanel, BorderLayout.CENTER);
		
		styleTypeFilter = new JComboBox<Style>();
		styleTypeFilter.addItem(new Style());
		
		styleTypeFilter.setRenderer(new StyleListCellRenderer());
		styleTypeFilter.addItemListener(this);
		previewHolderPanel.add(styleTypeFilter, BorderLayout.PAGE_START);
		
		setLayout(new BorderLayout(1,1));
		splitPane.setContinuousLayout(true);
		splitPane.setRightComponent(previewHolderPanel);
		splitPane.setBorder(null);
		splitPane.setDividerLocation(0.5d);
		splitPane.setResizeWeight(0.2d);
		add(splitPane, BorderLayout.CENTER);
		
		scroll.setBorder(null);
		
		itemTypeListPanel.setLayout(new BorderLayout(1, 1));
		splitPane.setLeftComponent(itemTypeListPanel);
		
		if (deckType == null)
		{
			itemTypeTypeFilter = new JComboBox<CardGroup>();
			itemTypeTypeFilter.addItem(new CardGroup());
			for (CardGroup g : CardGroup.getCardTypes())
			{
				itemTypeTypeFilter.addItem(g);
			}
			itemTypeTypeFilter.setRenderer(new CardGroupListCellRenderer());
			itemTypeTypeFilter.addItemListener(this);
			itemTypeListPanel.add(itemTypeTypeFilter, BorderLayout.PAGE_START);
		}
		
		
		itemTypeList = new JList<>(itemTypeListModel);
		
		if (deckType != null)
		{
			for (ItemType c : deckType.getCardTypes())
			{
					if (deckType == null || (c instanceof CardType))
					{
						itemTypeListModel.addElement(c);
					}
			}
		}
		else
		{
			for (ItemType c : ItemType.getItemTypes())
			{
				if (c.includeInFilters())
				{
					if (deckType == null || (c instanceof CardType))
					{
						itemTypeListModel.addElement(c);
					}
				}
			}
		}
		
		if (!itemTypeListModel.isEmpty())
		{
			itemTypeList.setSelectedIndex(0);
			resetStyleMenu();
			previewPanel.setSelectedItemType(itemTypeListModel.getElementAt(0));
		}
		
		itemTypeList.setCellRenderer(new ItemTypeCellRenderer());
		itemTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemTypeList.setEnabled(true);
		itemTypeListPanel.add(scroll, BorderLayout.CENTER);
		
		itemTypeList.addMouseListener(new MouseAdapter(){
		    @Override
		    public void mouseClicked(MouseEvent e){
		        if(e.getClickCount()==2){
		            selectItem();
		        }
		    }
		});
		
		scroll.setViewportView(itemTypeList);
		
		itemTypeList.addListSelectionListener(this);
		
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		tb.setAlignmentX(JToolBar.RIGHT_ALIGNMENT);
		
		tb.add(addButton);
		tb.add(cancelButton);
		addButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		this.add(tb, BorderLayout.PAGE_END);
		
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(addButton))
		{
			selectItem();
		}
		
		if (e.getSource().equals(cancelButton))
		{
			selectedItemType = null;
			outcome = JOptionPane.CANCEL_OPTION;
			this.setVisible(false);
		}
	}
	
	private void selectItem()
	{
		selectedItemType = itemTypeList.getSelectedValue();
		outcome = JOptionPane.OK_OPTION;
		this.setVisible(false);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource().equals(itemTypeTypeFilter))
		{
			resetItemTypesByType((CardGroup)e.getItem());
		}
		if (e.getSource().equals(styleTypeFilter) && previewPanel.getSelectedItemType() != null)
		{
			if (previewPanel.getSelectedItemType() instanceof CardType)
			{
				for (Style s : ((CardType)previewPanel.getSelectedItemType()).getStyles())
				{
					if (s.getName() != null && ((Style)styleTypeFilter.getSelectedItem()) != null 
							&& s.getName().equals(((Style)styleTypeFilter.getSelectedItem()).getName()))
					{
						System.out.println("Set Style: " + s.getName());
						((CardType)previewPanel.getSelectedItemType()).setStyle(s);
					}
				}
			}
			else if (previewPanel.getSelectedItemType() instanceof DeckType)
			{
				if (((Style)styleTypeFilter.getSelectedItem()) != null)
				{
					((DeckType)previewPanel.getSelectedItemType()).setDefaultStyle(((Style)styleTypeFilter.getSelectedItem()).getName());
				}
				
				for (CardType ct : ((DeckType)previewPanel.getSelectedItemType()).getCardTypes())
				{
					for (Style s : ct.getStyles())
					{
						if (s.getName() != null && ((Style)styleTypeFilter.getSelectedItem()) != null 
								&& s.getName().equals(((Style)styleTypeFilter.getSelectedItem()).getName()))
						{
							System.out.println("Set Style: " + s.getName());
							ct.setStyle(s);
						}
					}					
				}
			}
			previewPanel.resetPreviewPanel();
		}
	}
	
	private void resetItemTypesByType(CardGroup group)
	{
		itemTypeListModel.clear();
		
		for (ItemType c : ItemType.getItemTypes())
		{
			if (group != null && group.getName() != null && c.includeInFilters())
			{
				if (c.getCardGroup() != null && c.getCardGroup().equals(group.getName()))
				{
					itemTypeListModel.addElement(c);
				}
			}
			else
			{
				if (c.includeInFilters())
				{
					itemTypeListModel.addElement(c);
				}
			}			
		}
	}
	
	private void resetStyleMenu()
	{
		styleTypeFilter.removeAllItems();
		//System.out.println(itemTypeList.getSelectedValue());
		if (itemTypeList.getSelectedValue() instanceof CardType)
		{
			if (((CardType)itemTypeList.getSelectedValue()).getStyles().size() > 0)
			{
				for (Style s : ((CardType)itemTypeList.getSelectedValue()).getStyles())
				{
					styleTypeFilter.addItem(s);
				}
			}
			else
			{
				styleTypeFilter.addItem(new Style());
			}
		}
		else if (itemTypeList.getSelectedValue() instanceof DeckType)
		{
			DeckType dt = (DeckType)itemTypeList.getSelectedValue();
			List<String> usedStyles = new ArrayList<>(); 
			for (CardType ct : dt.getCardTypes())
			{
				for (Style s : ct.getStyles())
				{
					if (!usedStyles.contains(s.getName()))
					{
						usedStyles.add(s.getName());
						styleTypeFilter.addItem(s);
					}
				}
			}
		}
		else
		{
			styleTypeFilter.addItem(new Style());
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(itemTypeList) && itemTypeList.getSelectedValue() != null)
		{
			resetStyleMenu();
			previewPanel.setSelectedItemType(itemTypeList.getSelectedValue());
		}
	}

	public int getOutcome() {
		return outcome;
	}

	public void setOutcome(int outcome) {
		this.outcome = outcome;
	}
	
	public int showAddLegeditItem()
	{
		AddLegeditItemDialog d = new AddLegeditItemDialog(null);
		
		if (d.getOutcome() == CANCEL_OPTION)
		{
			d.setSelectedItemType(null);
		}
		
		return d.getOutcome();
	}
	
	public static ItemType addLegeditItem()
	{
		AddLegeditItemDialog d = new AddLegeditItemDialog(null);
		
		if (d.getOutcome() == CANCEL_OPTION)
		{
			d.setSelectedItemType(null);
		}
		
		return d.getSelectedItemType();
	}
	
	public int showAddLegeditItemWithoutDecks(DeckType deckType)
	{
		AddLegeditItemDialog d = new AddLegeditItemDialog(deckType);
		
		if (d.getOutcome() == CANCEL_OPTION)
		{
			d.setSelectedItemType(null);
		}
		
		return d.getOutcome();
	}
	
	public static ItemType addLegeditItemWithoutDecks(DeckType deckType)
	{
		AddLegeditItemDialog d = new AddLegeditItemDialog(deckType);
		
		if (d.getOutcome() == CANCEL_OPTION)
		{
			d.setSelectedItemType(null);
		}
		
		return d.getSelectedItemType();
	}

	public ItemType getSelectedItemType() {
		return selectedItemType;
	}

	public void setSelectedItemType(ItemType selectedItemType) {
		this.selectedItemType = selectedItemType;
	}
}
