package legedit2.cardtype;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.w3c.dom.Node;

import legedit2.card.Card;
import legedit2.imaging.CustomCardMaker;
import legedit2.imaging.MotionBlurOp;


public class ElementCardName extends CustomElement implements Cloneable {

	public enum HIGHLIGHT {BANNER, BLUR, BANNER_BLUR, NONE}
	
	// global card name settings
	public String defaultValue;
	public boolean includeSubname;
	public String subnameText;
	public boolean subnameEditable;
	public ALIGNMENT alignment = ALIGNMENT.CENTER;
	public boolean allowChange;
	public int x;
	public int y;
	public Color colour;
	public Color subNameColour;
	public boolean drawUnderlay;
	public int blurRadius;
	public boolean blurDouble;
	public int blurExpand;
	public Color highlightColour;
	public boolean uppercase;
	public HIGHLIGHT highlight = HIGHLIGHT.NONE;
	public int subnameGap = -1;
	private JTextField cardNameField;
	private JTextField cardSubNameField;
	
	// card name specific settings
	public String value;
	public String fontName;
	public int fontStyle;
	public int textSize;
	public String namePrefix = "";
	public String nameSuffix = "";
	
	// sub name specific settings
	public String subnameValue;
	public String subnameFontName;
	public int subnameFontStyle;
	public int subnameSize;
	private String subnamePrefix = "";
	private String subnameSuffix = "";	

	
	private Font createFont(String fontName, int fontStyle, int textSize)
	{
		Font newFont = null;
    	try
    	{
    		newFont = Font.createFont(Font.TRUETYPE_FONT, new File("legedit" + File.separator + "fonts" + File.separator + "Percolator.otf"));
    		newFont = newFont.deriveFont((float)getPercentage(textSize, getScale()));
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		
    		newFont = new Font("Percolator", Font.PLAIN, getPercentage(textSize, getScale()));
    	}
    	
    	if (fontName != null)
		{
    		newFont = new Font(fontName, fontStyle, getPercentage(textSize, getScale()));
		}
    	
    	return newFont;
    }
	
	private int getScreenStartingXPositionForString(String text, FontMetrics metrics)
	{
    	int stringLength = SwingUtilities.computeStringWidth(metrics, text);
        
        if (alignment.equals(ALIGNMENT.CENTER))
        	stringLength /= 2;
        
    	return getPercentage(x, getScale()) - stringLength;
	}
	
	private LineInformation createLineInformation(String text, Graphics2D g, FontMetrics metrics, int x, int y)
	{
        int stringLength = SwingUtilities.computeStringWidth(metrics, text);		        
        
        if (alignment.equals(ALIGNMENT.CENTER))
        	stringLength /= 2;
        if (alignment.equals(ALIGNMENT.RIGHT) || alignment.equals(ALIGNMENT.CENTER))
        	x -= stringLength;

    	LineMetrics lm = metrics.getLineMetrics(text, g);

    	return new LineInformation(text, x, y + (int)((lm.getAscent() - lm.getDescent())), (int)(lm.getDescent() * 1.5));
	}
	
	private List<LineInformation> prepareTextLines(String text, Graphics2D g, Font font, int xStart, int yStart)
	{
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);

		List<LineInformation> lines = new ArrayList<LineInformation>();
		//String tmpText = text.replaceAll("\\N", " ");
		//Boolean mustBreak = !text.contentEquals(tmpText);
		
		if (/*!mustBreak && */getScreenStartingXPositionForString(/*tmpText*/text, metrics) > 0)
		{
			// no need to break anything up, it fits already
			lines.add(createLineInformation(text, g, metrics, xStart, yStart));
		}
		else
		{
			// its too long, we need to break it down
			String newLine = "";
			int currentY = yStart;
	        for (String word : /*tmpText*/text.split("\\s+"))
	        {
	        	String testLine = newLine + " " + word;
	        	if (getScreenStartingXPositionForString(testLine, metrics) > 0)
	        	{
	        		// still fits, add and continue
	        		newLine = testLine;
	        	}
	        	else
	        	{
	        		// became too long, break and start new line
	        		LineInformation newLineInfo = createLineInformation(newLine, g, metrics, xStart, currentY);
	        		lines.add(newLineInfo);
	        		newLine = word;
	        		
	        		// update for next line
	        		currentY = newLineInfo.drawYPosition + newLineInfo.lineThickness;
	        	}
	        }
	        
	        if (!newLine.isEmpty())
	        {
	        	// must not forget last line
        		LineInformation newLineInfo = createLineInformation(newLine, g, metrics, xStart, currentY);
        		lines.add(newLineInfo);
	        }
		}
		
		return lines;
	}	
	
	public void drawElement(Graphics2D g)
	{
		if (getValue() != null)
		{
        	double scale = getScale();
        	int xScaled = getPercentage(x, scale);
	        int currentY = getPercentage(y, scale);

	        
	        BufferedImage bi = new BufferedImage(getPercentage(CustomCardMaker.cardWidth, getScale()), getPercentage(CustomCardMaker.cardHeight, getScale()), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = getGraphics(bi);
			g2 = setGraphicsHints(g2);
			
			if (colour != null)
				g2.setColor(colour);
        

			Font font = createFont(fontName, fontStyle, textSize);
	        Font fontSubname = null;
	        
		
			////////////////////////////////////////////////////////
	        // Prep up by breaking out our lines for card name and subname
	        ////////////////////////////////////////////////////////
	        
	        List<LineInformation> cardNameLines = prepareTextLines(getValueForDraw(), g2, font, xScaled, currentY);
	        List<LineInformation> subNameLines = null;
	        
	        if (includeSubname)
	        {
	        	fontSubname = createFont(subnameFontName, subnameFontStyle, subnameSize);
		        
	        	LineInformation lastLine = cardNameLines.isEmpty() ? null : cardNameLines.get(cardNameLines.size()-1);
	        	if (lastLine != null)
		        	currentY = lastLine.drawYPosition + lastLine.lineThickness;
	        	
	        	int subnameGapScaled = getPercentage(subnameGap, getScale());
		        if (subnameGapScaled >= 0 )
		        	currentY += subnameGapScaled;
		        
	        	subNameLines = prepareTextLines(getSubnameValueForDraw(), g2, fontSubname, xScaled, currentY);
	        	
		        g2.setFont(font);
	        }
	        
	        ////////////////////////////////////////////////////////
	        // Draw our banners/highlights if needed
	        ////////////////////////////////////////////////////////
	        
	        if (highlight.equals(HIGHLIGHT.BLUR) || highlight.equals(HIGHLIGHT.BANNER_BLUR))
	        {
	        	// We want the underlay to be applied to the text, that means we need it to had been drawn prior but before 
	        	// the banner (else its just a big blob of blackness). So draw here first (yes text will be drawn twice but thats life)
		        for (LineInformation line: cardNameLines)
		        {
			    	g2.drawString(line.text, line.drawXPosition, line.drawYPosition);
		        }
		        
		        if (includeSubname)
		        {
			        g2.setFont(fontSubname);
			        for (LineInformation line: subNameLines)
				    	g2.drawString(line.text, line.drawXPosition, line.drawYPosition);
			        g2.setFont(font);
		        }
		        
		    	drawUnderlay(bi, g2, BufferedImage.TYPE_INT_ARGB, 0, 0, getPercentage(blurRadius, getScale()), blurDouble, getPercentage(blurExpand, getScale()), highlightColour);
	        }
	        
	        /*
	        g.setColor(Color.GREEN);
	        g.drawLine(0, yModified - (int)lm.getAscent() + (int)lm.getDescent(), 750, yModified - (int)lm.getAscent() + (int)lm.getDescent());
	        
	        g.setColor(Color.RED);
	        g.drawLine(0, yModified, 750, yModified);
	        
	        g.setColor(Color.BLUE);
	        g.drawLine(0, yModified + (int)lm.getDescent(), 750, yModified + (int)lm.getDescent());
	        */	        
	        
	        if (highlight.equals(HIGHLIGHT.BANNER) || highlight.equals(HIGHLIGHT.BANNER_BLUR))
	        {	        
	        	int cardWidthScaled = getPercentage(CustomCardMaker.cardWidth, getScale());
	        	int cardHeightScaled = getPercentage(CustomCardMaker.cardHeight, getScale());

	        	int bannerStart = 0;
	        	LineInformation firstLine = null;
		        if (!cardNameLines.isEmpty())
		        	firstLine = cardNameLines.get(0);
		        else if (subNameLines != null && !subNameLines.isEmpty())
		        	firstLine = subNameLines.get(0);
	        	bannerStart = getPercentage(y, scale) - getPercentage(cardHeightScaled, 0.01d);
	
	        	int bannerEnd = 0;
	        	LineInformation lastLine = null;
		        if (subNameLines != null && !subNameLines.isEmpty())
		        	lastLine = subNameLines.get(subNameLines.size()-1);
		        else if (!cardNameLines.isEmpty())
		        	lastLine = cardNameLines.get(cardNameLines.size()-1);
	        	bannerEnd = lastLine.drawYPosition + lastLine.lineThickness + getPercentage(cardHeightScaled, 0.015d);

		        if (firstLine != null && lastLine != null)
		        {
		        	BufferedImage bi2 = new BufferedImage(cardWidthScaled, cardHeightScaled, BufferedImage.TYPE_INT_ARGB);
			        Graphics g3 = bi2.getGraphics();
			        
		        	int bannerHeight = bannerEnd - bannerStart;
					g3.setColor(highlightColour);
					g3.fillRect(cardWidthScaled / 2, bannerStart - getPercentage(cardHeightScaled, 0.005d), getPercentage(cardWidthScaled, 0.15d), bannerHeight);
			    	
					MotionBlurOp op = new MotionBlurOp();
					op.setDistance(200f);
		        	bi2 = op.filter(bi2, null);
		        	
		        	makeTransparent(bi2, 0.8d);
					
					g2.drawImage(bi2, 0, 0, null);
					
					//Flip and re-draw image
					AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
					tx.translate(-bi2.getWidth(null), 0);
					AffineTransformOp aop = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
					bi2 = aop.filter(bi2, null);
					
					g2.drawImage(bi2, 0, 0, null);		        	
		        }
	        }
	        
	        ////////////////////////////////////////////////////////
	        // Now we can draw our lines	
	        ////////////////////////////////////////////////////////
	        
	        for (LineInformation line: cardNameLines)
	        {
		    	g2.drawString(line.text, line.drawXPosition, line.drawYPosition);
	        }
	        
	        if (includeSubname)
	        {
		        g2.setFont(fontSubname);
		        for (LineInformation line: subNameLines)
			    	g2.drawString(line.text, line.drawXPosition, line.drawYPosition);
		        g2.setFont(font); // just in case, for consistency, but not really needed since we are doing drawing
	        }
	        
	        
	        ////////////////////////////////////////////////////////
	        // Final polish	
	        ////////////////////////////////////////////////////////

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
	
	public String getValue()
	{
		if (value != null)
		{
			return populateVariables(value, this);
		}
		return populateVariables(defaultValue, this);
	}
	
	private String getValueForDraw()
	{
		String valueForDraw = getNamePrefix() + getValue() + getNameSuffix();
		if (uppercase && valueForDraw != null)
		{
			return valueForDraw.toUpperCase();
		}
		return valueForDraw;
	}
	
	public String getSubnameValue()
	{
		if (subnameValue != null)
		{
			return populateVariables(subnameValue, this);
		}
		return populateVariables(subnameText, this);
	}
	
	private String getSubnameValueForDraw()
	{
		if (uppercase && getSubnameValue() != null)
		{
			return getSubnamePrefix().toUpperCase() + getSubnameValue().toUpperCase() + getSubnameSuffix().toUpperCase();
		}
		return getSubnamePrefix() + getSubnameValue() + getSubnameSuffix();
	}
	
	private BufferedImage makeTransparent(BufferedImage bi, double percent)
	{
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(bi.getRGB(xx, yy), true);
                if (originalColor.getAlpha() > 0) {
                    int col = (getPercentage(originalColor.getAlpha(), percent) << 24) | (originalColor.getRed() << 16) | (originalColor.getGreen() << 8) | originalColor.getBlue();
                    bi.setRGB(xx, yy, col);
                }
            }
        }
		
		return bi;
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
		if (subnameValue != null)
		{
			str += "CUSTOMVALUE;" + name + ";subnameValue;" + subnameValue + "\n";
		}
		str += "CUSTOMVALUE;" + name + ";visible;" + visible + "\n";
		return str;
	}
	
	public String getNamePrefix() {
		return namePrefix;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}	

	public String getNameSuffix() {
		return nameSuffix;
	}

	public void setNameSuffix(String nameSuffix) {
		this.nameSuffix = nameSuffix;
	}	

	public String getSubnamePrefix() {
		return subnamePrefix;
	}

	public void setSubnamePrefix(String subnamePrefix) {
		this.subnamePrefix = subnamePrefix;
	}

	public String getSubnameSuffix() {
		return subnameSuffix;
	}

	public void setSubnameSuffix(String subnameSuffix) {
		this.subnameSuffix = subnameSuffix;
	}

	public JTextField getCardNameField() {
		return cardNameField;
	}

	public void setCardNameField(JTextField cardNameField) {
		this.cardNameField = cardNameField;
	}

	public JTextField getCardSubNameField() {
		return cardSubNameField;
	}

	public void setCardSubNameField(JTextField cardSubNameField) {
		this.cardSubNameField = cardSubNameField;
	}
	
	@Override
	public void updateCardValues()
	{
		if (cardNameField != null)
		{
			value = cardNameField.getText();
		}
		
		if (cardSubNameField != null && subnameEditable)
		{
			subnameValue = cardSubNameField.getText();
		}
	}
	
	public String getDifferenceXML()
	{
		String str = "";
		
		str += "<cardname name=\"" + replaceNonXMLCharacters(name) + "\" "
				+ "value=\""+replaceNonXMLCharacters(getValue())+"\" "
				+ (fontName == null ? "" : "fontname=\""+replaceNonXMLCharacters(fontName)+"\" ")
				+ (fontName == null ? "" : "fontstyle=\""+fontStyle+"\" ")
				+ "textsize=\""+textSize+"\" "
				+ "subnameValue=\"" + replaceNonXMLCharacters(getSubnameValue()) + "\" "
				+ (subnameFontName == null ? "" : "subnamefontname=\""+replaceNonXMLCharacters(subnameFontName)+"\" ")
				+ (subnameFontName == null ? "" : "subnamefontstyle=\""+subnameFontStyle+"\" ")
				+ "subnamesize=\""+subnameSize+"\" />\n";
		
		return str;
	}
	
	public void loadValues(Node node, Card card)
	{
		if (!node.getNodeName().equals("cardname"))
		{
			return;
		}
		
		// card name specific settings
		
		if (node.getAttributes().getNamedItem("value") != null)
		{
			value = node.getAttributes().getNamedItem("value").getNodeValue();
		}
		
		if (node.getAttributes().getNamedItem("fontname") != null)
		{
			fontName = node.getAttributes().getNamedItem("fontname").getNodeValue();
		}
		
		if (node.getAttributes().getNamedItem("fontstyle") != null)
		{
			fontStyle = Integer.parseInt(node.getAttributes().getNamedItem("fontstyle").getNodeValue());
		}

		if (node.getAttributes().getNamedItem("textsize") != null)
		{
			textSize = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
		}
		
		
		// sub name specific settings

		if (node.getAttributes().getNamedItem("subnameValue") != null)
		{
			if (card.getTemplate() != null 
					&& card.getTemplate().findElementByType(ElementCardName.class) != null
					&& ((ElementCardName)card.getTemplate().findElementByType(ElementCardName.class)).subnameEditable)
			{
				subnameValue = node.getAttributes().getNamedItem("subnameValue").getNodeValue();
			}
		}

		if (node.getAttributes().getNamedItem("subnamefontname") != null)
		{
			subnameFontName = node.getAttributes().getNamedItem("subnamefontname").getNodeValue();
		}
		
		if (node.getAttributes().getNamedItem("subnamefontstyle") != null)
		{
			subnameFontStyle = Integer.parseInt(node.getAttributes().getNamedItem("subnamefontstyle").getNodeValue());
		}

		if (node.getAttributes().getNamedItem("subnamesize") != null)
		{
			subnameSize = Integer.parseInt(node.getAttributes().getNamedItem("subnamesize").getNodeValue());
		}
	}
}
