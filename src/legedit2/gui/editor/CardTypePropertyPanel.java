package legedit2.gui.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;

import legedit2.card.Card;
import legedit2.cardgroup.CardGroup;
import legedit2.cardgroup.CardGroupListCellRenderer;
import legedit2.cardtype.CardType;
import legedit2.cardtype.CustomElement;
import legedit2.cardtype.ElementBackgroundImage;
import legedit2.cardtype.ElementCardName;
import legedit2.cardtype.ElementGroup;
import legedit2.cardtype.ElementIcon;
import legedit2.cardtype.ElementIconImage;
import legedit2.cardtype.ElementImage;
import legedit2.cardtype.ElementProperty;
import legedit2.cardtype.ElementText;
import legedit2.cardtype.ElementTextArea;
import legedit2.cardtype.Style;
import legedit2.decktype.DeckType;
import legedit2.definitions.Icon;
import legedit2.gui.project.LegeditItemPreviewPanel;
import legedit2.helpers.LegeditHelper;

public class CardTypePropertyPanel extends JPanel implements ActionListener, ItemListener {

	private LegeditItemPreviewPanel previewPanel;
	private CardType card;
	
	private JPanel panel;
	private JScrollPane scroll;
	
	private JComboBox<Style> styleFilter;
	private JSplitPane splitPane;
	
	private HashMap<String, CustomElement> elementMap = new HashMap<>();
	
	private JButton updateButton = new JButton("Update");
	
	public CardTypePropertyPanel(CardType card, LegeditItemPreviewPanel previewPanel) {
		this.previewPanel = previewPanel;
		this.card = card;
		
		setLayout(new BorderLayout(1,1));
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		//toolbar.setLayout(new BorderLayout(0, 0));
		toolbar.setLayout(new FlowLayout(FlowLayout.RIGHT,0, 0));
		
		styleFilter = new JComboBox<Style>();
		styleFilter.addItem(new Style());
		resetStyleMenu();
		styleFilter.setRenderer(new StyleListCellRenderer());
		styleFilter.addItemListener(this);
		//this.add(cardGroupFilter, BorderLayout.PAGE_START);
		toolbar.add(styleFilter);
		
		this.add(toolbar, BorderLayout.PAGE_START);
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setContinuousLayout(true);
		splitPane.setRightComponent(new JPanel());
		splitPane.setBorder(null);
		splitPane.setDividerLocation(0.5d);
		splitPane.setResizeWeight(0.25d);
		add(splitPane, BorderLayout.CENTER);
		
		
		scroll = new JScrollPane();
		scroll.setBorder(null);
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		scroll.setViewportView(panel);
		//this.add(scroll, BorderLayout.CENTER);
		splitPane.setBottomComponent(scroll);
		
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		tb.add(updateButton);
		updateButton.addActionListener(this);
		this.add(tb, BorderLayout.PAGE_END);
		
		setupComponents();
	}
	
	private void resetStyleMenu()
	{
		styleFilter.removeAllItems();
		
		if (card != null)
		{
				for (Style s : card.getStyles())
				{
					styleFilter.addItem(s);
				}
		}
		styleFilter.addItem(new Style());
	}
	
	private void setupComponents()
	{
		elementMap = new HashMap<>();
		
		int row = 0;
		
		if (card != null)
		{
			if (card.getStyle() != null)
			{
				for (CustomElement el : card.getStyle().getElements())
				{
					if (el instanceof ElementCardName)
					{
						row = addCardNameItems((ElementCardName)el, row);
					}
					if (el instanceof ElementIcon)
					{
						row = addIconItems((ElementIcon)el, row);
					}
					if (el instanceof ElementIconImage)
					{
						row = addIconImageItems((ElementIconImage)el, row);
					}
					if (el instanceof ElementProperty)
					{
						row = addPropertyItems((ElementProperty)el, row);
					}
					if (el instanceof ElementText)
					{
						row = addTextItems((ElementText)el, row);
					}
					if (el instanceof ElementTextArea)
					{
						row = addTextAreaItems((ElementTextArea)el, row);
					}
					if (el instanceof ElementImage)
					{
						row = addImageItems((ElementImage)el, row);
					}
					if (el instanceof ElementBackgroundImage)
					{
						row = addImageItems((ElementBackgroundImage)el, row);
					}
					if (el instanceof ElementGroup)
					{
						row = addGroupItems((ElementGroup)el, row);
					}
				}
			}
			
			for (CustomElement el : card.elements)
			{
				if (el instanceof ElementCardName)
				{
					row = addCardNameItems((ElementCardName)el, row);
				}
				if (el instanceof ElementIcon)
				{
					row = addIconItems((ElementIcon)el, row);
				}
				if (el instanceof ElementIconImage)
				{
					row = addIconImageItems((ElementIconImage)el, row);
				}
				if (el instanceof ElementProperty)
				{
					row = addPropertyItems((ElementProperty)el, row);
				}
				if (el instanceof ElementText)
				{
					row = addTextItems((ElementText)el, row);
				}
				if (el instanceof ElementTextArea)
				{
					row = addTextAreaItems((ElementTextArea)el, row);
				}
				if (el instanceof ElementImage)
				{
					row = addImageItems((ElementImage)el, row);
				}
				if (el instanceof ElementBackgroundImage)
				{
					row = addImageItems((ElementBackgroundImage)el, row);
				}
				if (el instanceof ElementGroup)
				{
					row = addGroupItems((ElementGroup)el, row);
				}
			}
			
		}
	}
	

	private int addGroupItems(ElementGroup group, int row)
	{
		for (CustomElement el : group.getElements())
		{
			if (el instanceof ElementCardName)
			{
				row = addCardNameItems((ElementCardName)el, row);
			}
			if (el instanceof ElementIcon)
			{
				row = addIconItems((ElementIcon)el, row);
			}
			if (el instanceof ElementIconImage)
			{
				row = addIconImageItems((ElementIconImage)el, row);
			}
			if (el instanceof ElementProperty)
			{
				row = addPropertyItems((ElementProperty)el, row);
			}
			if (el instanceof ElementText)
			{
				row = addTextItems((ElementText)el, row);
			}
			if (el instanceof ElementTextArea)
			{
				row = addTextAreaItems((ElementTextArea)el, row);
			}
			if (el instanceof ElementImage)
			{
				row = addImageItems((ElementImage)el, row);
			}
			if (el instanceof ElementBackgroundImage)
			{
				row = addImageItems((ElementBackgroundImage)el, row);
			}
			if (el instanceof ElementGroup)
			{
				row = addGroupItems((ElementGroup)el, row);
			}
		}
		
		return row;
	}
	
	private int addImageItems(ElementImage el, int row)
	{
		if (el.allowChange)
		{
			JLabel nameLabel = new JLabel(el.name);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			panel.add(nameLabel, c);
			
			JLabel fileNameLabel = new JLabel(el.name);
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 2;
			c.gridx = 1;
			c.gridy = row;
			panel.add(fileNameLabel, c);
			if (el.path != null)
			{
				fileNameLabel.setText(new File(el.path).getName());
			}
			
			JButton browseButton = new JButton("Browse");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 3;
			c.gridy = row;
			panel.add(browseButton, c);
			JFileChooser chooser = new JFileChooser(LegeditHelper.getLastOpenDirectory());
			browseButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int outcome = chooser.showOpenDialog(previewPanel);
					if (outcome == JFileChooser.APPROVE_OPTION)
					{
						fileNameLabel.setText(chooser.getSelectedFile().getName());
					}
				}
			});
			
			el.setFileNameLabel(fileNameLabel);
			el.setBrowseButton(browseButton);
			el.setChooser(chooser);
			row++;
		}
		
		return row;
	}
	
	private int addImageItems(ElementBackgroundImage el, int row)
	{
		if (el.allowChange)
		{
			JLabel nameLabel = new JLabel(el.name);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			panel.add(nameLabel, c);
			
			JLabel fileNameLabel = new JLabel(el.name);
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 2;
			c.gridx = 1;
			c.gridy = row;
			panel.add(fileNameLabel, c);
			if (el.path != null)
			{
				fileNameLabel.setText(new File(el.path).getName());
			}
			
			JButton browseButton = new JButton("Browse");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 3;
			c.gridy = row;
			panel.add(browseButton, c);
			JFileChooser chooser = new JFileChooser(LegeditHelper.getLastOpenDirectory());
			browseButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int outcome = chooser.showOpenDialog(previewPanel);
					if (outcome == JFileChooser.APPROVE_OPTION)
					{
						fileNameLabel.setText(chooser.getSelectedFile().getName());
					}
				}
			});
			
			el.setFileNameLabel(fileNameLabel);
			el.setBrowseButton(browseButton);
			el.setChooser(chooser);
			row++;
		}
		
		return row;
	}
	
	private int addTextItems(ElementText el, int row)
	{
		if (el.allowChange)
		{
			JLabel nameLabel = new JLabel(el.name);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			panel.add(nameLabel, c);
			
			JTextField nameField = new JTextField();
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 3;
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 0.5;
			panel.add(nameField, c);
			nameField.setText(el.getValue());
			
			el.setTextField(nameField);
			row++;
		}
		
		return row;
	}
	
	private int addTextAreaItems(ElementTextArea el, int row)
	{
		if (el.allowChange)
		{
			JTextArea textArea = new JTextArea();
			
			JLabel nameLabel = new JLabel(el.name);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			panel.add(nameLabel, c);
			
			JComboBox<Icon> icons = new JComboBox<Icon>();
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 2;
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 0.5;
			icons.setRenderer(new IconListRenderer());
			for (Icon icon : Icon.sorted_values())
			{
				icons.addItem(icon);
			}
			icons.setSelectedItem(Icon.valueOf("ATTACK"));
			panel.add(icons, c);
			
			el.setIconComboBox(icons);
			
			JButton addIcon = new JButton("+");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 3;
			c.gridy = row;
			addIcon.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.insert("<" + ((Icon)icons.getSelectedItem()).getEnumName() + ">", textArea.getCaretPosition());
				}
			});
			panel.add(addIcon, c);
			
			el.setAddIconButton(addIcon);
			
			row++;
			
			JButton keywordButton = new JButton("Keyword");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			keywordButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.insert("<k>", textArea.getCaretPosition());
				}
			});
			panel.add(keywordButton, c);
			
			el.setKeywordButton(keywordButton);
			
			JButton regularButton = new JButton("Regular");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 1;
			c.gridy = row;
			regularButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.insert("<r>", textArea.getCaretPosition());
				}
			});
			panel.add(regularButton, c);
			
			el.setRegularButton(regularButton);
			
			row++;
			
			JScrollPane scrollPane = new JScrollPane();
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 4;
			c.ipady = 50;
			c.gridx = 0;
			c.gridy = row;
			c.weightx = 0.5;
			panel.add(scrollPane, c);
			textArea.setText(el.getValue());
			scrollPane.setViewportView(textArea);
			
			el.setTextArea(textArea);
			el.setScrollPane(scrollPane);
			row++;
		}
		
		return row;
	}
	
	private int addPropertyItems(ElementProperty el, int row)
	{
		
		{
			JLabel nameLabel = new JLabel(el.name);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			panel.add(nameLabel, c);
			
			JTextField nameField = new JTextField();
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 3;
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 0.5;
			panel.add(nameField, c);
			nameField.setText(el.getValue());
			
			el.setPropertyField(nameField);
			row++;
		}
		
		return row;
	}
	
	private int addIconItems(ElementIcon el, int row)
	{
		if (el.allowChange)
		{
			JLabel nameLabel = new JLabel(el.name);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			panel.add(nameLabel, c);
			
			JComboBox<Icon> icons = new JComboBox<Icon>();
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 3;
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 0.5;
			icons.setRenderer(new IconListRenderer());
			for (Icon icon : Icon.sorted_values())
			{
				icons.addItem(icon);
			}
			icons.setSelectedItem(el.getIconValue());
			panel.add(icons, c);
			
			el.setIconCombobox(icons);
			row++;
		}
		return row;
	}
	
	private int addIconImageItems(ElementIconImage el, int row)
	{
		if (el.allowChange)
		{
			JLabel nameLabel = new JLabel(el.name);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			panel.add(nameLabel, c);
			
			JComboBox<Icon> icons = new JComboBox<Icon>();
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 3;
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 0.5;
			icons.setRenderer(new IconListRenderer());
			for (Icon icon : Icon.sorted_values())
			{
				icons.addItem(icon);
			}
			icons.setSelectedItem(el.getIconValue());
			panel.add(icons, c);
			
			el.setIconCombobox(icons);
			row++;
		}
		return row;
	}
	
	private int addCardNameItems(ElementCardName el, int row)
	{
		if (el.allowChange)
		{
			JLabel nameLabel = new JLabel("Name");
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			panel.add(nameLabel, c);
			
			JTextField nameField = new JTextField();
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 3;
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 0.5;
			panel.add(nameField, c);
			nameField.setText(el.getValue());
			
			el.setCardNameField(nameField);
			row++;
		}
		
		if (el.includeSubname && el.subnameEditable)
		{
			JLabel subnameLabel = new JLabel("Sub Name");
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = row;
			panel.add(subnameLabel, c);
			
			JTextField subnameField = new JTextField();
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 3;
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 0.5;
			panel.add(subnameField, c);
			
			el.setCardSubNameField(subnameField);
			row++;
		}
		
		return row;
	}

	public LegeditItemPreviewPanel getPreviewPanel() {
		return previewPanel;
	}

	public void setPreviewPanel(LegeditItemPreviewPanel previewPanel) {
		this.previewPanel = previewPanel;
	}

	public CardType getCard() {
		return card;
	}


	public void setCard(CardType card) {
		this.card = card;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(updateButton))
		{
			if (getPreviewPanel().getSelectedItem() instanceof Card)
			{
				//System.out.println("Updating card...");
				((Card)getPreviewPanel().getSelectedItem()).updateCardValues();
				getPreviewPanel().rebuildImageList();
				getPreviewPanel().resetPreviewPanel();				
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (styleFilter.getSelectedItem() != null && ((Style)styleFilter.getSelectedItem()).getName() != null)
		{
			//System.out.println("Rerendering Style...");
			card.setStyle(((Style)styleFilter.getSelectedItem()));
			getPreviewPanel().rebuildImageList();
			getPreviewPanel().resetPreviewPanel();	
		}
	}
	
}
