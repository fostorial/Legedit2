package legedit2.cardtype;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.w3c.dom.Node;

import legedit2.card.Card;
import legedit2.definitions.Icon;
import legedit2.helpers.LegeditHelper;
import legedit2.imaging.BoxBlurFilter;
import legedit2.imaging.GaussianFilter;

public class ElementTextArea extends CustomElement {
	
	//////////////////////////////////////////////////////////////////
	/// Editable through template data
	//////////////////////////////////////////////////////////////////
	public String defaultValue;
	public boolean allowChange;
	public Color colour;
	public String fontName;
	public String fontNameBold;
	public int textSize = 27;
	public int textSizeBold = 27;
	public ALIGNMENT alignmentHorizontal = ALIGNMENT.LEFT;
	public ALIGNMENT alignmentVertical = ALIGNMENT.TOP;
	public String rectXArray;
	public String rectYArray;
	public boolean debug = false;
	public double gapSizeBetweenLines = 0.2d;
	public double gapSizeBetweenParagraphs = 0.6d;
	//////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////
	/// Editable through save file
	//////////////////////////////////////////////////////////////////
	public String value;
	public int fontStyle;	
	//////////////////////////////////////////////////////////////////

	public int textSizeHeader = 45;
	public int textIconBlurRadius = 5;
	public boolean textIconBlurDouble = true;
	public int expandTextIcon = 0;
	
	private Polygon polygon = null;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JComboBox<Icon> iconComboBox;
	private JButton addIconButton;
	private JButton keywordButton;
	private JButton regularButton;
	private JButton fontButton;
	
	public String getValue()
	{
		if (value != null)
		{
			return resolveAttributes(value, this);
		}
		return resolveAttributes(defaultValue, this);
	}

	public String getValueRaw()
	{
		if (value != null)
		{
			return value;
		}
		return defaultValue;
	}
	
	public void drawElement(Graphics2D g)
	{
		if (getValue() != null)
		{
			int cardWidth = template.getCardWidth();
			int cardHeight = template.getCardHeight();
			
			BufferedImage bi = new BufferedImage(LegeditHelper.getPercentage(cardWidth, getScale()), LegeditHelper.getPercentage(cardHeight, getScale()), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = getGraphics(bi);
			g2 = setGraphicsHints(g2);
			
			polygon = null;
			
			if (debug)
			{
				g2.setColor(Color.BLACK);
				g2.fillPolygon(getPolygon());
			}
			
	    	g2.setColor(colour);
	    	try
	    	{
				Font font = LegeditHelper.createFont(fontName, "Swiss 721", fontStyle, textSize, getScale());

				g2.setFont(font);
	    		g2 = setGraphicsHints(g2);
	    		
				Font fontBold = LegeditHelper.createFont(fontNameBold != null ? fontNameBold : fontName, "Swiss 721", Font.BOLD, textSizeBold, getScale());
				Font fontHeader = LegeditHelper.createFont(fontNameBold != null ? fontNameBold : fontName, "Percolator", Font.PLAIN, textSizeHeader, getScale());
	    		
	    		FontMetrics metrics = g2.getFontMetrics(font);
	    		
	    		int x = -1;
	    		int y = -1;
	    		
	    		//TODO other alignments
	    		
	    		Point startPoint = getStartPosition();
	    		x = startPoint.x;
	    		y = startPoint.y + metrics.getAscent();
	    		
	    		String[] sections = getValue().split("<h>");
	    		boolean firstSection = true;
	    		boolean lastWordWasBreak = false;
	    		int lastWordBreakHeight = 0;
	    		
	    		for (String sectionString : sections)
	    		{
	    			//System.out.println("Section: " + sectionString);
	    			
	    			if (!sectionString.isEmpty())
	    			{
	    				String headerStr = "";
	    				
	    				HeaderIcon headerIcon = null;
		    			String cardStr = sectionString;
		    			if (cardStr.contains("</h>"))
		    			{
		    				String[] headerSplit = sectionString.split("</h>");
		    				headerStr = headerSplit[0];
		    				
		    				//Calculate Header Icons
		    				if (headerStr.toLowerCase().contains("<hi") && headerStr.toLowerCase().contains("/>"))
		    				{
		    					String[] spl = headerStr.split("<hi");
		    					headerStr = spl[0];
		    					String[] spl2 = spl[1].split("/>");
		    					String headerIconText = spl2[0];
		    					
		    					String[] iconPairs = headerIconText.split(" ");
		    					for (String i : iconPairs)
		    					{
		    						if (i.toUpperCase().startsWith("ICON="))
		    						{
		    							if (headerIcon == null)
		    							{
		    								headerIcon = new HeaderIcon();
		    							}
		    							headerIcon.icon = isIcon(i.toUpperCase().trim().replace("ICON=", ""));
		    						}
		    						
		    						if (i.toUpperCase().startsWith("VALUE="))
		    						{
		    							if (headerIcon == null)
		    							{
		    								headerIcon = new HeaderIcon();
		    							}
		    							headerIcon.value = i.toUpperCase().trim().replace("VALUE=", "");
		    						}
		    					}
		    				}
		    				
		    				if (headerSplit.length > 1)
		    				{
		    					cardStr = headerSplit[1];
		    				}
		    				else
		    				{
		    					cardStr = "";
		    				}
		    			}
		    			
		    			//System.out.println("header: " + headerStr + ", card: " + cardStr);
		    			
		    			if (headerStr != null && !headerStr.isEmpty())
		    			{
		    				int headerHeight = (int)((double)g.getFontMetrics(fontHeader).getHeight() * 1.2d);
		    				if (lastWordWasBreak)
		    				{
		    					y -= lastWordBreakHeight;
		    				}
			    			drawHeader(g2, headerStr.toUpperCase(), fontHeader, /*card.cardType.getBgColor()*/ Color.red, y, headerHeight, LegeditHelper.getPercentage(cardWidth, 0.2d), headerIcon);
				    		y += headerHeight + metrics.getHeight() + LegeditHelper.getPercentage(metrics.getHeight(), 0.5d);
		    			}
//		    			else
//		    			{
//		    				if (firstSection)
//		    				{
//		    					y += 35;
//		    				}
//		    			}
		    			
		    			List<WordDefinition> words = WordDefinition.getWordDefinitionList(getValue());

			    		for (WordDefinition wd : words)
			    		{
			    			String s = wd.word;
							String spaceChar = "";
							if (wd.space)
							{
								spaceChar = " ";
							}
							
			    			if (s.startsWith("<k>"))
			    			{
			    				g2.setFont(fontBold);
			    				metrics = g2.getFontMetrics(fontBold);
			    				s = s.replace("<k>", "");
			    				g2 = setGraphicsHints(g2);
			    				continue;
			    			}
			    			
			    			if (s.startsWith("<r>"))
			    			{
			    				g2.setFont(font);
			    				metrics = g2.getFontMetrics(font);
			    				s = s.replace("<r>", "");
			    				g2 = setGraphicsHints(g2);
			    				continue;
			    			}
			    			
			    			boolean gap = false;
			    			if (s.equals("<g>"))
			    			{
			    				gap = true;
			    			}
			    			
			    			Icon icon = isIcon(s);
			    			if (gap == true)
			    			{
			    				y += g2.getFontMetrics(font).getHeight() + LegeditHelper.getPercentage(g2.getFontMetrics(font).getHeight(), gapSizeBetweenParagraphs);
			    				x = getXStart(y);
			    			}
			    			else if (icon == null)
			    			{
			    				int stringLength = SwingUtilities.computeStringWidth(metrics, s);

			    				if (!getPolygon().contains(x+stringLength, y))
			    				{
			    					//TODO Restore for rare backing?
			    					/*
			    					if (x > xEnd)
			    					{
			    						xEnd = x;
			    					}
			    					*/
			    					y += g2.getFontMetrics(font).getHeight() + LegeditHelper.getPercentage(g2.getFontMetrics(font).getHeight(), gapSizeBetweenLines);
			    					x = getXStart(y);
			    				}
			    				g2.drawString(s + " ", x, y);
			    				x += stringLength + SwingUtilities.computeStringWidth(metrics, spaceChar);
			    			}
			    			else if (icon != null)
			    			{
			    				BufferedImage i = getIconMaxHeight(icon, LegeditHelper.getPercentage(metrics.getHeight(), 1.2d));

			    				if (!getPolygon().contains(x + i.getWidth(), y))
			    				{
			    					//TODO Restore for rare backing?
			    					/*
			    					if (x > xEnd)
			    					{
			    						xEnd = x;
			    					}
			    					*/
			    					y += g2.getFontMetrics(font).getHeight() + LegeditHelper.getPercentage(g2.getFontMetrics(font).getHeight(), gapSizeBetweenLines);
			    					x = getXStart(y);
			    				}
			    				
			    				int modifiedY = (int)(y - i.getHeight() + metrics.getDescent());
			    				
			    				//System.out.println(offsetRatio + " " + offset + " " + modifiedY + " " + i.getHeight() + " " + metrics.getHeight());
			    				
			    				if (icon.isUnderlayMinimized())
			    				{
			    					drawUnderlay(i, g2, BufferedImage.TYPE_INT_ARGB, x, modifiedY, textIconBlurRadius, textIconBlurDouble, expandTextIcon, Color.black);
			    				}
			    				g2.drawImage(i, x, modifiedY, null);
			    				x += i.getWidth() + SwingUtilities.computeStringWidth(metrics, spaceChar);
			    			}
			    		}
	    			}
		    			
		    		firstSection = false;
		    	}
	    		
	            
	            
	    		
	    		//TODO Provide blur background option
	    		/*
	    		if (card.rarity.equals(CardRarity.RARE))
		    	{
	    			int padding = getPercentage(xEnd - xOrigin, rarePaddingRatio);
	    			yOrigin = yOrigin + (metrics.getHeight() / 3);
		    		BufferedImage blurBG = createRareBacking(xOrigin - padding, yOrigin - padding, xEnd + padding, y + padding);
		    		blurBG = makeTransparent(blurBG, 0.85d);
		    		blurBG = blurImage(blurBG, g2, rareBlurRadius);
		    		
		    		g.drawImage(blurBG, 0, 0, null);
		    	}
		    	*/
	    	}
	    	catch (Exception e)
	    	{
	    		e.printStackTrace();
	    	}

	    	if (rotate > 0)
			{
				double rotationRequired = Math.toRadians (rotate);
				double locationX = bi.getWidth() / 2;
				double locationY = bi.getHeight() / 2;
				AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				bi = op.filter(bi, null);
			}
	    	
	    	g.drawImage(bi, 0, 0, null);
	    	
	    	g2.dispose();
		}
	}
	
	public void drawElementDeprecated(Graphics2D g)
	{
		if (getValue() != null)
		{
			int cardWidth = template.getCardWidth();
			int cardHeight = template.getCardHeight();

			BufferedImage bi = new BufferedImage(LegeditHelper.getPercentage(cardWidth, getScale()), LegeditHelper.getPercentage(cardHeight, getScale()), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = getGraphics(bi);
			g2 = setGraphicsHints(g2);
			
			polygon = null;
			
			if (debug)
			{
				g2.setColor(Color.BLACK);
				g2.fillPolygon(getPolygon());
			}
			
	    	g2.setColor(colour);
	    	try
	    	{
				Font font = LegeditHelper.createFont(fontName, "Swiss 721", fontStyle, textSize, getScale());

				g2.setFont(font);
	    		g2 = setGraphicsHints(g2);
	    		
				Font fontBold = LegeditHelper.createFont(fontNameBold != null ? fontNameBold : fontName, "Swiss 721", Font.BOLD, textSizeBold, getScale());
	    		FontMetrics metrics = g2.getFontMetrics(font);
	    		
	    		int x = -1;
	    		int y = -1;
	    		
	    		//TODO other alignments
	    		
	    		Point startPoint = getStartPosition();
	    		x = startPoint.x;
	    		y = startPoint.y + metrics.getAscent();
	    		
	            
	            List<WordDefinition> words = WordDefinition.getWordDefinitionList(getValue());

	    		for (WordDefinition wd : words)
	    		{
	    			String s = wd.word;
					String spaceChar = "";
					if (wd.space)
					{
						spaceChar = " ";
					}
					
	    			if (s.startsWith("<k>"))
	    			{
	    				g2.setFont(fontBold);
	    				metrics = g2.getFontMetrics(fontBold);
	    				s = s.replace("<k>", "");
	    				g2 = setGraphicsHints(g2);
	    				continue;
	    			}
	    			
	    			if (s.startsWith("<r>"))
	    			{
	    				g2.setFont(font);
	    				metrics = g2.getFontMetrics(font);
	    				s = s.replace("<r>", "");
	    				g2 = setGraphicsHints(g2);
	    				continue;
	    			}
	    			
	    			boolean gap = false;
	    			if (s.equals("<g>"))
	    			{
	    				gap = true;
	    			}
	    			
	    			Icon icon = isIcon(s);
	    			if (gap == true)
	    			{
	    				y += g2.getFontMetrics(font).getHeight() + LegeditHelper.getPercentage(g2.getFontMetrics(font).getHeight(), gapSizeBetweenParagraphs);
	    				x = getXStart(y);
	    			}
	    			else if (icon == null)
	    			{
	    				int stringLength = SwingUtilities.computeStringWidth(metrics, s);

	    				if (!getPolygon().contains(x+stringLength, y))
	    				{
	    					//TODO Restore for rare backing?
	    					/*
	    					if (x > xEnd)
	    					{
	    						xEnd = x;
	    					}
	    					*/
	    					y += g2.getFontMetrics(font).getHeight() + LegeditHelper.getPercentage(g2.getFontMetrics(font).getHeight(), gapSizeBetweenLines);
	    					x = getXStart(y);
	    				}
	    				g2.drawString(s + " ", x, y);
	    				x += stringLength + SwingUtilities.computeStringWidth(metrics, spaceChar);
	    			}
	    			else if (icon != null)
	    			{
	    				BufferedImage i = getIconMaxHeight(icon, LegeditHelper.getPercentage(metrics.getHeight(), 1.2d));

	    				if (!getPolygon().contains(x + i.getWidth(), y))
	    				{
	    					//TODO Restore for rare backing?
	    					/*
	    					if (x > xEnd)
	    					{
	    						xEnd = x;
	    					}
	    					*/
	    					y += g2.getFontMetrics(font).getHeight() + LegeditHelper.getPercentage(g2.getFontMetrics(font).getHeight(), gapSizeBetweenLines);
	    					x = getXStart(y);
	    				}
	    				
	    				int modifiedY = (int)(y - i.getHeight() + metrics.getDescent());
	    				
	    				//System.out.println(offsetRatio + " " + offset + " " + modifiedY + " " + i.getHeight() + " " + metrics.getHeight());
	    				
	    				if (icon.isUnderlayMinimized())
	    				{
	    					drawUnderlay(i, g2, BufferedImage.TYPE_INT_ARGB, x, modifiedY, textIconBlurRadius, textIconBlurDouble, expandTextIcon, Color.black);
	    				}
	    				g2.drawImage(i, x, modifiedY, null);
	    				x += i.getWidth() + SwingUtilities.computeStringWidth(metrics, spaceChar);
	    			}
	    		}
	    		
	    		//TODO Provide blur background option
	    		/*
	    		if (card.rarity.equals(CardRarity.RARE))
		    	{
	    			int padding = getPercentage(xEnd - xOrigin, rarePaddingRatio);
	    			yOrigin = yOrigin + (metrics.getHeight() / 3);
		    		BufferedImage blurBG = createRareBacking(xOrigin - padding, yOrigin - padding, xEnd + padding, y + padding);
		    		blurBG = makeTransparent(blurBG, 0.85d);
		    		blurBG = blurImage(blurBG, g2, rareBlurRadius);
		    		
		    		g.drawImage(blurBG, 0, 0, null);
		    	}
		    	*/
	    	}
	    	catch (Exception e)
	    	{
	    		e.printStackTrace();
	    	}

	    	if (rotate > 0)
			{
				double rotationRequired = Math.toRadians (rotate);
				double locationX = bi.getWidth() / 2;
				double locationY = bi.getHeight() / 2;
				AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				bi = op.filter(bi, null);
			}
	    	
	    	g.drawImage(bi, 0, 0, null);
	    	
	    	g2.dispose();
		}
	}
	
	public Polygon getPolygon()
	{
		if (polygon == null)
		{
			String[] xsplit = rectXArray.split(",");
			int[] xpoints = new int[xsplit.length];
			for (int i = 0; i< xsplit.length; i++)
			{
				xpoints[i] = LegeditHelper.getPercentage(Integer.parseInt(xsplit[i].trim()),getScale());
			}
			
			String[] ysplit = rectYArray.split(",");
			int[] ypoints = new int[ysplit.length];
			for (int i = 0; i< ysplit.length; i++)
			{
				ypoints[i] = LegeditHelper.getPercentage(Integer.parseInt(ysplit[i].trim()),getScale());
			}
			
			polygon = new Polygon(xpoints, ypoints, xpoints.length);
			//System.out.println(polygon);
		}
		
		return polygon;
	}
	
	public int getXStart(int y)
	{
		if (alignmentHorizontal.equals(ALIGNMENT.LEFT))
		{
			for (int i = 0; i < LegeditHelper.getPercentage(template.getCardWidth(),getScale()); i++)
			{
				if (getPolygon().contains(i, y))
				{
					return i;
				}
			}
		}
		
		//TODO other alignments
		
		return -1;
	}
	
	public int getYStart(int x)
	{
		if (alignmentVertical.equals(ALIGNMENT.TOP))
		{
			for (int i = 0; i < LegeditHelper.getPercentage(template.getCardHeight(),getScale()); i++)
			{
				if (getPolygon().contains(x, i))
				{
					return i;
				}
			}
		}
		
		//TODO other alignments
		
		return -1;
	}
	
	public Point getStartPosition()
	{
		if (alignmentHorizontal.equals(ALIGNMENT.LEFT))
		{
			for (int i = 0; i < LegeditHelper.getPercentage(template.getCardWidth(),getScale()); i++)
			{
				int ypos = getYStart(i);
				if (ypos > -1)
				{
					return new Point(i, ypos);
				}
			}
		}
		
		//TODO other alignments
		return null;
	}
	
	public String generateOutputString()
	{
		return generateOutputString(false);
	}
	
	public String generateOutputString(boolean fullExport)
	{
		String str = "";
		if (value != null)
		{
			str += "CUSTOMVALUE;" + name + ";value;" + value + "\n";
		}
		
		str += "CUSTOMVALUE;" + name + ";textSize;" + textSize + "\n";
		
		str += "CUSTOMVALUE;" + name + ";visible;" + visible + "\n";
		
		return str;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public JComboBox<Icon> getIconComboBox() {
		return iconComboBox;
	}

	public void setIconComboBox(JComboBox<Icon> iconComboBox) {
		this.iconComboBox = iconComboBox;
	}

	public JButton getAddIconButton() {
		return addIconButton;
	}

	public void setAddIconButton(JButton addIconButton) {
		this.addIconButton = addIconButton;
	}
	
	@Override
	public void updateCardValues()
	{
		if (textArea != null)
		{
			value = transformString(textArea.getText());
		}
	}
	
	private String transformString(String newValue)
	{
		String str = textArea.getText();
		
		str = str.replace("\n", " <g> ");
		
		return str;
	}

	public JButton getKeywordButton() {
		return keywordButton;
	}

	public void setKeywordButton(JButton keywordButton) {
		this.keywordButton = keywordButton;
	}

	public JButton getRegularButton() {
		return regularButton;
	}

	public void setRegularButton(JButton regularButton) {
		this.regularButton = regularButton;
	}
	
	private void drawHeader(Graphics2D g, String header, Font font, Color color, int y, int height, int blurRadius, HeaderIcon headerIcon)
	{
		int cardWidth = template.getCardWidth();
		int cardHeight = template.getCardHeight();

		BufferedImage bi1 = new BufferedImage(LegeditHelper.getPercentage(cardWidth, getScale()), LegeditHelper.getPercentage(cardHeight, getScale()), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = getGraphics(bi1);
		
		g2.setColor(color);
		g2.fillRect(0, y, LegeditHelper.getPercentage(LegeditHelper.getPercentage(cardWidth, getScale()), 0.8d), height);
    	
    	if (blurRadius > 0)
    	{
    		BoxBlurFilter op = new BoxBlurFilter();
    		op.setHRadius(blurRadius);
    		op.setVRadius(0);
        	bi1 = op.filter(bi1, null);
        	makeTransparent(bi1, 0.7d);
    	}
    	
    	g2 = getGraphics(bi1);
    	
    	if (headerIcon != null && headerIcon.icon != null)
	    {
	    	BufferedImage bi = getIcon(headerIcon.icon, LegeditHelper.getPercentage(height, 1.9d), LegeditHelper.getPercentage(height, 1.9d));
	    	int iconx = LegeditHelper.getPercentage(cardWidth, getScale()) - LegeditHelper.getPercentage(LegeditHelper.getPercentage(cardWidth, getScale()), 0.09d) - bi.getWidth() + (bi.getWidth() / 2);
	    	int icony = y + (height / 2) - (bi.getWidth() / 2);
	    	
	    	g2.drawImage(bi, iconx, icony, null);
	    }
    	
    	BufferedImage bi2 = new BufferedImage(LegeditHelper.getPercentage(cardWidth, getScale()), LegeditHelper.getPercentage(cardHeight, getScale()), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g3 = getGraphics(bi2);	
		
    	g3.setColor(Color.WHITE);
    	g3.setFont(font);
    	
    	g3 = setGraphicsHints(g3);
    	
		g3.drawString(header, LegeditHelper.getPercentage(LegeditHelper.getPercentage(cardWidth, getScale()), 0.04d), y + g.getFontMetrics(font).getHeight() - (g.getFontMetrics(font).getHeight() / 6));
		
		if (headerIcon != null && headerIcon.value != null)
		{
			Font originalFont = font;
			
			font = font.deriveFont((float)(g.getFontMetrics(originalFont).getHeight() * 1.6f));
			g3.setFont(font);
			
			g3 = setGraphicsHints(g3);
			
			int stringLength = SwingUtilities.computeStringWidth(g.getFontMetrics(font), headerIcon.value.toUpperCase());
			g3.drawString(headerIcon.value, LegeditHelper.getPercentage(cardWidth, getScale()) - LegeditHelper.getPercentage(LegeditHelper.getPercentage(cardWidth, getScale()), 0.09d) - stringLength + (stringLength / 2), y + g.getFontMetrics(font).getHeight() - (int)(g.getFontMetrics(font).getHeight() / 2.6d));
			
			font = originalFont;
			g3.setFont(originalFont);
			
			g3 = setGraphicsHints(g3);
		}
		
		drawUnderlay(bi2, g3, BufferedImage.TYPE_INT_ARGB, 0, 0, 5, true, 3);
		
		g3.drawString(header, LegeditHelper.getPercentage(LegeditHelper.getPercentage(cardWidth, getScale()), 0.04d), y + g.getFontMetrics(font).getHeight() - (g.getFontMetrics(font).getHeight() / 6));
		
		if (headerIcon != null && headerIcon.value != null)
		{
			Font originalFont = font;
			
			font = font.deriveFont((float)(g.getFontMetrics(originalFont).getHeight() * 1.6f));
			g3.setFont(font);
			g3 = setGraphicsHints(g3);
			int stringLength = SwingUtilities.computeStringWidth(g.getFontMetrics(font), headerIcon.value.toUpperCase());
			g3.drawString(headerIcon.value, LegeditHelper.getPercentage(cardWidth, getScale()) - LegeditHelper.getPercentage(LegeditHelper.getPercentage(cardWidth, getScale()), 0.09d) - stringLength + (stringLength / 2), y + g.getFontMetrics(font).getHeight() - (int)(g.getFontMetrics(font).getHeight() / 2.6d));
			
			font = originalFont;
			g3.setFont(originalFont);
			g3 = setGraphicsHints(g3);
		}
		
		g.drawImage(bi1, 0, 0, null);
		g.drawImage(bi2, 0, 0, null);
		
		g2.dispose();
		g3.dispose();
	}
	
	private void drawUnderlay(BufferedImage bi, Graphics2D g, int type, int x, int y, int blurRadius, boolean doubleBlur, int expandBlackout)
	{
		BufferedImage blackout = new BufferedImage(LegeditHelper.getPercentage(template.getCardWidth(), getScale()), LegeditHelper.getPercentage(template.getCardHeight(), getScale()), type);
    	getGraphics(blackout).drawImage(bi, x, y, null);
    	
    	blackout = blackoutImage(blackout);
    	
    	if (expandBlackout > 0)
    	{
    		blackout = expandBlackout(blackout, expandBlackout);
    	}
    	
    	if (blurRadius > 0)
    	{
    		BufferedImageOp op = new GaussianFilter( blurRadius );
        	BufferedImage bi2 = op.filter(blackout, null);
        	g.drawImage(bi2, 0, 0, null);
        	
        	if (doubleBlur)
        	{
        		BufferedImage bi3 = op.filter(bi2, null);
        		g.drawImage(bi3, 0, 0, null);
        	}
    	}
    	else
    	{
    		g.drawImage(blackout, 0, 0, null);
    	}
	}
	
	private BufferedImage blackoutImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(image.getRGB(xx, yy), true);
                //System.out.println(xx + "|" + yy + " color: " + originalColor.toString() + "alpha: " + originalColor.getAlpha());
                if (originalColor.getAlpha() > 0) {
                    image.setRGB(xx, yy, Color.BLACK.getRGB());
                }
            }
        }
        return image;
    }
	
	private BufferedImage expandBlackout(BufferedImage image, int expandBlackout)
	{
		BufferedImage expand = new BufferedImage(LegeditHelper.getPercentage(template.getCardWidth(), getScale()), LegeditHelper.getPercentage(template.getCardHeight(), getScale()), BufferedImage.TYPE_INT_ARGB);
		
		int width = image.getWidth();
        int height = image.getHeight();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(image.getRGB(xx, yy), true);
                
                if (originalColor.getAlpha() > 0) {
                	//Quick and Dirty - Just ignore out of bounds
                	for (int i = expandBlackout; i > 0; i--)
                	{
                		try { expand.setRGB(xx, yy - i, Color.BLACK.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx, yy + i, Color.BLACK.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx - i, yy, Color.BLACK.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx + i, yy, Color.BLACK.getRGB()); } catch (Exception e) {}
                    	
                    	if (i == 1)
                    	{
                    	try { expand.setRGB(xx - i, yy - i, Color.BLACK.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx - i, yy + i, Color.BLACK.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx + i, yy - i, Color.BLACK.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx + i, yy + i, Color.BLACK.getRGB()); } catch (Exception e) {}
                    	}
                	}
                }
            }
        }
        return expand;
	}
	
	private BufferedImage makeTransparent(BufferedImage bi, double percent)
	{
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(bi.getRGB(xx, yy), true);
                if (originalColor.getAlpha() > 0) {
                    int col = (LegeditHelper.getPercentage(originalColor.getAlpha(), percent) << 24) | (originalColor.getRed() << 16) | (originalColor.getGreen() << 8) | originalColor.getBlue();
                    bi.setRGB(xx, yy, col);
                }
            }
        }
		
		return bi;
	}
	
	public void loadValues(Node node, Card card)
	{
		if (!node.getNodeName().equals("textarea"))
		{
			return;
		}
		
		if (node.getAttributes().getNamedItem("value") != null)
		{
			value = node.getAttributes().getNamedItem("value").getNodeValue();
			value = restoreNonXMLCharacters(value);
		}
		
		if (node.getAttributes().getNamedItem("fontname") != null)
		{
			fontName = node.getAttributes().getNamedItem("fontname").getNodeValue();
		}
		
		if (node.getAttributes().getNamedItem("fontnamebold") != null)
		{
			fontNameBold = node.getAttributes().getNamedItem("fontnamebold").getNodeValue();
		}
		
		if (node.getAttributes().getNamedItem("fontstyle") != null)
		{
			fontStyle = Integer.parseInt(node.getAttributes().getNamedItem("fontstyle").getNodeValue());
		}
		
		if (node.getAttributes().getNamedItem("textsize") != null)
		{
			textSize = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
			textSizeBold = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
		}
		
		if (node.getAttributes().getNamedItem("textsizebold") != null)
		{
			textSizeBold = Integer.parseInt(node.getAttributes().getNamedItem("textsizebold").getNodeValue());
		}
	}
	
	public String getDifferenceXML()
	{
		String str = "";
		
		/////////////////////////////////////////////////////////////////
		// for global attributes to really work (ie, if the value changes, then it changes for all using cards),
		// we need to keep the original "attribute" like text around so it can continue to be interpreted when
		// needed. So here we check the formatted text with the raw version, if they are not identical, then
		// it means that something got interpreted by a Global attribute, hence we keep its raw value to 
		// keep the attribute around.
		/////////////////////////////////////////////////////////////////
		
		String value = "";
		if (getValue().equalsIgnoreCase(getValueRaw()))
			value = getValue();
		else
			value = getValueRaw();
		
		str += "<textarea name=\"" + replaceNonXMLCharacters(name) + "\" value=\""+replaceNonXMLCharacters(value)+"\" "
				+ (fontName == null ? "" : "fontname=\""+replaceNonXMLCharacters(fontName)+"\" ")
				+ (fontNameBold == null ? "" : "fontnamebold=\""+replaceNonXMLCharacters(fontNameBold)+"\" ")
				+ (fontName == null ? "" : "fontstyle=\""+fontStyle+"\" ")
				+ "textsize=\""+textSize+"\" "
				+ "textsizebold=\""+textSizeBold+"\" />\n";
		
		return str;
	}

	public JButton getFontButton() {
		return fontButton;
	}

	public void setFontButton(JButton fontButton) {
		this.fontButton = fontButton;
	}
}
