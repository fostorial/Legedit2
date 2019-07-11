package legedit2.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;

import legedit2.card.Card;
import legedit2.cardtype.CustomElement;
import legedit2.cardtype.ElementBackgroundImage;
import legedit2.cardtype.ElementCardName;
import legedit2.cardtype.ElementGroup;
import legedit2.cardtype.ElementIcon;
import legedit2.cardtype.ElementIconImage;
import legedit2.cardtype.ElementImage;
import legedit2.cardtype.ElementProperty;
import legedit2.cardtype.ElementScrollingTextArea;
import legedit2.cardtype.ElementText;
import legedit2.cardtype.ElementTextArea;
import legedit2.definitions.Icon;
import legedit2.gui.LegeditFrame;
import legedit2.gui.project.LegeditItemPreviewPanel;
import legedit2.helpers.LegeditHelper;
import opensource.JFontChooser;

public class CardPropertyPanel extends JPanel implements ActionListener {

	private LegeditItemPreviewPanel previewPanel;
	private Card card;
	
	private JPanel panel;
	private JScrollPane scroll;
	
	private HashMap<String, CustomElement> elementMap = new HashMap<>();
	
	private JButton updateButton = new JButton("Update");
	private JButton closeButton = new JButton("Close");
	
	public CardPropertyPanel(Card card, LegeditItemPreviewPanel previewPanel) {
		this.previewPanel = previewPanel;
		this.card = card;
		
		setLayout(new BorderLayout(1,1));
		
		scroll = new JScrollPane();
		scroll.setBorder(null);
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		scroll.setViewportView(panel);
		this.add(scroll, BorderLayout.CENTER);
		
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		JPanel p = new JPanel();
		p.add(updateButton);
		tb.add(p);
		updateButton.addActionListener(this);
		updateButton.setPreferredSize(new Dimension(100, 30));
		p.add(closeButton);
		closeButton.addActionListener(this);
		closeButton.setPreferredSize(new Dimension(100, 30));
		this.add(tb, BorderLayout.PAGE_END);
		
		setupComponents();
	}
	
	private void setupComponents()
	{
		elementMap = new HashMap<>();
		
		int row = 0;
		
		if (card != null && card.getTemplate() != null)
		{
			if (card.getTemplate().getStyle() != null)
			{
				for (CustomElement el : card.getTemplate().getStyle().getElements())
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
					if (el instanceof ElementScrollingTextArea)
					{
						row = addTextAreaItems((ElementScrollingTextArea)el, row);
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
			
			for (CustomElement el : card.getTemplate().elements)
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
				if (el instanceof ElementScrollingTextArea)
				{
					row = addTextAreaItems((ElementScrollingTextArea)el, row);
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
		JLabel nameLabel = new JLabel(group.name);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = row;
		panel.add(nameLabel, c);
		
		JCheckBox visible = new JCheckBox();
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 1;
		c.gridy = row;
		c.weightx = 0.5;
		panel.add(visible, c);
		visible.setText("Visible?");
		visible.setSelected(group.visible);
		
		visible.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				group.visible = visible.isSelected();
				for (CustomElement el : group.getElements())
				{
					el.visible = visible.isSelected();
				}
			}
		});
		
		group.setVisibleCheckbox(visible);
		row++;
		
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
			if (el instanceof ElementScrollingTextArea)
			{
				row = addTextAreaItems((ElementScrollingTextArea)el, row);
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
			
			row++;
			
			int x = 0;
			if (el.zoomable)
			{
				JLabel scaleLabel = new JLabel("Zoom");
				c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = 1;
				c.gridx = x;
				c.gridy = row;
				panel.add(scaleLabel, c);
				x++;
				
				JTextField scaleField = new JTextField();
				c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = 1;
				c.gridx = x;
				c.gridy = row;
				panel.add(scaleField, c);
				scaleField.setText(""+el.zoom);
				el.setZoomField(scaleField);
				x++;
			}
			
			if (!el.fullSize)
			{
				JLabel offsetLabel = new JLabel("Offset X/Y");
				c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = x;
				c.gridx = x;
				c.gridy = row;
				panel.add(offsetLabel, c);
				x++;
				
				JToolBar tb = new JToolBar();
				tb.setFloatable(false);
				
				JTextField offsetXField = new JTextField();
				c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = 1;
				c.gridx = x;
				c.gridy = row;
				tb.add(offsetXField);
				offsetXField.setText(""+el.imageOffsetX);
				el.setOffsetXField(offsetXField);
				
				JTextField offsetYField = new JTextField();
				c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = 1;
				c.gridx = x;
				c.gridy = row;
				tb.add(offsetYField);
				offsetYField.setText(""+el.imageOffsetY);
				el.setOffsetYField(offsetYField);
				
				panel.add(tb, c);
				x++;
			}
			
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
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 0.5;
			nameField.setText(el.getValue());			
			el.setTextField(nameField);
			panel.add(nameField, c);
			
			JButton fontButton = new JButton("Font");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 3;
			c.gridy = row;			
			fontButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFontChooser chooser = new JFontChooser();
					
					Font font;
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, new File("legedit" + File.separator + "fonts" + File.separator + "Swiss 721 Light Condensed.ttf"));
						font = font.deriveFont(el.fontStyle, (float)el.textSize);
						
						if (el.fontName != null)
			    		{
			    			font = new Font(el.fontName, el.fontStyle, el.textSize);
			    		}
						
						chooser.setFont(font);
						chooser.setSelectedFont(font);
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					int showDialog = chooser.showDialog(LegeditFrame.legedit);
					if (showDialog == JFontChooser.OK_OPTION)
					{
						Font selectedFont = chooser.getSelectedFont();
						el.fontName = selectedFont.getName();
						el.textSize = selectedFont.getSize();
						el.fontStyle = selectedFont.getStyle();
					}
				}
			});
			panel.add(fontButton, c);			
			el.setFontButton(fontButton);
			
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
			for (Icon icon : Icon.values())
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
			
			JButton fontButton = new JButton("Font");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 2;
			c.gridy = row;
			fontButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFontChooser chooser = new JFontChooser();
					
					Font font;
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, new File("legedit" + File.separator + "fonts" + File.separator + "Swiss 721 Light Condensed.ttf"));
						font = font.deriveFont((float)el.textSize);
						
						if (el.fontName != null)
			    		{
			    			font = new Font(el.fontName, el.fontStyle, el.textSize);
			    		}
						
						chooser.setFont(font);
						chooser.setSelectedFont(font);
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					int showDialog = chooser.showDialog(LegeditFrame.legedit);
					if (showDialog == JFontChooser.OK_OPTION)
					{
						Font selectedFont = chooser.getSelectedFont();
						el.fontName = selectedFont.getName();
						el.textSize = selectedFont.getSize();
						el.textSizeBold = selectedFont.getSize();
						el.fontStyle = selectedFont.getStyle();
					}
				}
			});
			panel.add(fontButton, c);
			
			el.setFontButton(fontButton);
			
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
	
	private int addTextAreaItems(ElementScrollingTextArea el, int row)
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
			for (Icon icon : Icon.values())
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
			
			JButton fontButton = new JButton("Font");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 2;
			c.gridy = row;
			fontButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFontChooser chooser = new JFontChooser();
					
					Font font;
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, new File("legedit" + File.separator + "fonts" + File.separator + "Swiss 721 Light Condensed.ttf"));
						font = font.deriveFont((float)el.textSize);
						
						if (el.fontName != null)
			    		{
			    			font = new Font(el.fontName, el.fontStyle, el.textSize);
			    		}
						
						chooser.setFont(font);
						chooser.setSelectedFont(font);
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					int showDialog = chooser.showDialog(LegeditFrame.legedit);
					if (showDialog == JFontChooser.OK_OPTION)
					{
						el.textSize = chooser.getSelectedFont().getSize();
						el.textSizeBold = chooser.getSelectedFont().getSize();
					}
				}
			});
			panel.add(fontButton, c);
			
			el.setFontButton(fontButton);
			
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
			for (Icon icon : Icon.values())
			{
				if (el.iconType != null && icon.getIconType() != null && (icon.getIconType().equals(el.iconType) || icon.getEnumName().equalsIgnoreCase("NONE")))
				{
					icons.addItem(icon);
				}
				else if (el.iconType == null)
				{
					icons.addItem(icon);
				}
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
			for (Icon icon : Icon.values())
			{
				if (el.iconType != null && icon.getIconType() != null && (icon.getIconType().equals(el.iconType) || icon.getEnumName().equalsIgnoreCase("NONE")))
				{
					icons.addItem(icon);
				}
				else if (el.iconType == null)
				{
					icons.addItem(icon);
				}
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
			c.gridwidth = 1;
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 0.5;
			panel.add(nameField, c);
			nameField.setText(el.getValue());
			
			el.setCardNameField(nameField);
			
			JButton fontButton = new JButton("Name Font");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 2;
			c.gridy = row;
			fontButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFontChooser chooser = new JFontChooser();
					
					Font font;
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, new File("legedit" + File.separator + "fonts" + File.separator + "Percolator.otf"));
						font = font.deriveFont((float)el.textSize);
						
						if (el.fontName != null)
			    		{
			    			font = new Font(el.fontName, el.fontStyle, el.textSize);
			    		}
						
						chooser.setFont(font);
						chooser.setSelectedFont(font);
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					int showDialog = chooser.showDialog(LegeditFrame.legedit);
					if (showDialog == JFontChooser.OK_OPTION)
					{
						Font selectedFont = chooser.getSelectedFont();
						el.fontName = selectedFont.getName();
						el.fontStyle = selectedFont.getStyle();
						el.textSize = selectedFont.getSize();
					}
				}
			});
			panel.add(fontButton, c);
			
			JButton subfontButton = new JButton("Subname Font");
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridx = 3;
			c.gridy = row;
			subfontButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFontChooser chooser = new JFontChooser();
					
					Font font;
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, new File("legedit" + File.separator + "fonts" + File.separator + "Percolator.otf"));
						font = font.deriveFont((float)el.subnameSize);
						
						if (el.subnameFontName != null)
			    		{
			    			font = new Font(el.subnameFontName, el.subnameFontStyle, el.subnameSize);
			    		}
						
						chooser.setFont(font);
						chooser.setSelectedFont(font);
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					int showDialog = chooser.showDialog(LegeditFrame.legedit);
					if (showDialog == JFontChooser.OK_OPTION)
					{
						Font selectedFont = chooser.getSelectedFont();
						el.subnameFontName = selectedFont.getName();
						el.subnameFontStyle = selectedFont.getStyle();
						el.subnameSize = selectedFont.getSize();
					}
				}
			});
			panel.add(subfontButton, c);
			
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

	public Card getCard() {
		return card;
	}


	public void setCard(Card card) {
		this.card = card;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(updateButton))
		{
			if (getPreviewPanel().getSelectedItem() instanceof Card)
			{
				//System.out.println("Updating card...");
				((Card)getPreviewPanel().getSelectedItem()).setChanged(true);
				((Card)getPreviewPanel().getSelectedItem()).updateCardValues();
				getPreviewPanel().rebuildImageList();
				getPreviewPanel().resetPreviewPanel();
			}
		}
		
		if (e.getSource().equals(closeButton))
		{
			LegeditFrame.legedit.closeTab();
		}
	}
	
}
