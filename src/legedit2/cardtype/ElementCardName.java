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

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.w3c.dom.Node;

import legedit2.imaging.CustomCardMaker;
import legedit2.imaging.MotionBlurOp;

public class ElementCardName extends CustomElement implements Cloneable {

	public enum HIGHLIGHT {BANNER, BLUR, NONE}
	
	public String defaultValue;
	public boolean includeSubname;
	public String subnameText;
	public boolean subnameEditable;
	public ALIGNMENT alignment = ALIGNMENT.CENTER;
	public boolean allowChange;
	public int x;
	public int y;
	public Color colour;
	public boolean drawUnderlay;
	public int blurRadius;
	public boolean blurDouble;
	public int blurExpand;
	public Color highlightColour;
	public int textSize;
	public int subnameSize;
	public String fontName;
	public int fontStyle;
	public boolean uppercase;
	public HIGHLIGHT highlight = HIGHLIGHT.NONE;
	public int subnameGap = -1;
	
	public String value;
	public String subnameValue;
	private String subnamePrefix = "";
	private String subnameSuffix = "";
	
	private JTextField cardNameField;
	private JTextField cardSubNameField;
	
	public void drawElement(Graphics2D g)
	{
		if (getValue() != null)
		{
			BufferedImage bi = new BufferedImage(getPercentage(CustomCardMaker.cardWidth, getScale()), getPercentage(CustomCardMaker.cardHeight, getScale()), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = getGraphics(bi);
			g2 = setGraphicsHints(g2);
			
			if (colour != null)
			{
				g2.setColor(colour);
			}
			
			Font font = null;
	    	try
	    	{
	    	font = Font.createFont(Font.TRUETYPE_FONT, new File("legedit" + File.separator + "fonts" + File.separator + "Percolator.otf"));
	        font = font.deriveFont((float)getPercentage(textSize, getScale()));
	    	}
	    	catch (Exception e)
	    	{
	    		e.printStackTrace();
	    		
	    		font = new Font("Percolator", Font.PLAIN, getPercentage(textSize,getScale()));
	    	}
	    	if (fontName != null)
    		{
    			font = new Font(fontName, fontStyle, getPercentage(textSize, getScale()));
    		}
	        g2.setFont(font);
	        FontMetrics metrics = g2.getFontMetrics(font);
	        int stringLength = SwingUtilities.computeStringWidth(metrics, getValueForDraw());
	        
	        g2 = setGraphicsHints(g2);
	        
	        int newx = getPercentage(x,getScale());
	        if (alignment.equals(ALIGNMENT.CENTER))
	        {
	        	newx = getPercentage(x,getScale()) - (stringLength / 2);
	        }
	        if (alignment.equals(ALIGNMENT.RIGHT))
	        {
	        	newx = getPercentage(x,getScale()) - stringLength;
	        }
	        
	        LineMetrics lm = metrics.getLineMetrics(getValueForDraw(), g2);
	        int yModified = getPercentage(y,getScale()) + (int)((lm.getAscent() - lm.getDescent()));
	        
	        int bannerStart = (yModified - (int)lm.getAscent() + (int)lm.getDescent()) - getPercentage(getPercentage(CustomCardMaker.cardHeight, getScale()), 0.01d);
	        int bannerEnd = yModified + getPercentage(getPercentage(CustomCardMaker.cardHeight, getScale()), 0.015d);
	        
	        /*
	        g.setColor(Color.GREEN);
	        g.drawLine(0, yModified - (int)lm.getAscent() + (int)lm.getDescent(), 750, yModified - (int)lm.getAscent() + (int)lm.getDescent());
	        
	        g.setColor(Color.RED);
	        g.drawLine(0, yModified, 750, yModified);
	        
	        g.setColor(Color.BLUE);
	        g.drawLine(0, yModified + (int)lm.getDescent(), 750, yModified + (int)lm.getDescent());
	        */
	        
	        int subnameStartY = yModified + (int)lm.getDescent() + (int)(lm.getDescent() / 2);
	        if (getPercentage(subnameGap, getScale()) >= 0)
	        {
	        	subnameStartY = yModified + getPercentage(subnameGap, getScale());
	        }
	        int newxSubname = getPercentage(x,getScale());
	        int yModifiedSubname = subnameStartY;
	        Font fontSubname = null;
	        if (includeSubname)
	        {
	        	fontSubname = font.deriveFont((float)getPercentage(subnameSize, getScale()));
		        
		        g2.setFont(fontSubname);
		        metrics = g2.getFontMetrics(fontSubname);
		        stringLength = SwingUtilities.computeStringWidth(metrics, getSubnameValueForDraw());
		        
		        if (alignment.equals(ALIGNMENT.CENTER))
		        {
		        	newxSubname = getPercentage(x,getScale()) - (stringLength / 2);
		        }
		        if (alignment.equals(ALIGNMENT.RIGHT))
		        {
		        	newxSubname = getPercentage(x,getScale()) - stringLength;
		        }
		        
		        lm = metrics.getLineMetrics(getValueForDraw(), g2);
		        yModifiedSubname = subnameStartY + (int)((lm.getAscent() - lm.getDescent()));
		        
		        g2.setFont(font);
		        metrics = g2.getFontMetrics(font);
		        
		        bannerEnd = yModifiedSubname + getPercentage(getPercentage(CustomCardMaker.cardHeight, getScale()), 0.015d);
		        
	        }
	        
	        
	        if (highlight.equals(HIGHLIGHT.BLUR))
	        {
	        	g2 = setGraphicsHints(g2);
	        	
	        	g2.drawString(getValueForDraw(), newx, yModified);
	        	
	        	if (includeSubname)
	        	{
	        		g2.setFont(fontSubname);
			        metrics = g2.getFontMetrics(fontSubname);
			        
			        g2 = setGraphicsHints(g2);
			        
	        		g2.drawString(getSubnameValueForDraw(), newxSubname, yModifiedSubname);
	        		
	        		g2.setFont(font);
			        metrics = g2.getFontMetrics(font);
			        
			        g2 = setGraphicsHints(g2);
	        	}
	        	
		    	drawUnderlay(bi, g2, BufferedImage.TYPE_INT_ARGB, 0, 0, getPercentage(blurRadius, getScale()), blurDouble, getPercentage(blurExpand, getScale()), highlightColour);
	        }
	        
	        if (highlight.equals(HIGHLIGHT.BANNER))
	        {
	        	BufferedImage bi2 = new BufferedImage(getPercentage(CustomCardMaker.cardWidth, getScale()), getPercentage(CustomCardMaker.cardHeight, getScale()), BufferedImage.TYPE_INT_ARGB);
		        Graphics g3 = bi2.getGraphics();
		        
	        	int bannerHeight = bannerEnd - bannerStart;
				g3.setColor(highlightColour);
				g3.fillRect((getPercentage(CustomCardMaker.cardWidth,getScale()) / 2), bannerStart - getPercentage(getPercentage(CustomCardMaker.cardHeight, getScale()), 0.005d), getPercentage(getPercentage(CustomCardMaker.cardWidth, getScale()), 0.15d), bannerHeight);
		    	
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
	    	
	        g2 = setGraphicsHints(g2);
	        
	    	g2.drawString(getValueForDraw(), newx, yModified);
	    	
	    	if (includeSubname)
        	{
	    		g2.setFont(fontSubname);
		        metrics = g2.getFontMetrics(fontSubname);
		        
		        g2 = setGraphicsHints(g2);
		        
        		g2.drawString(getSubnameValueForDraw(), newxSubname, yModifiedSubname);
        		
        		g2.setFont(font);
		        metrics = g2.getFontMetrics(font);
		        
		        g2 = setGraphicsHints(g2);
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
	
	public String getValue()
	{
		if (value != null)
		{
			return populateVariables(value);
		}
		return populateVariables(defaultValue);
	}
	
	private String getValueForDraw()
	{
		if (uppercase && getValue() != null)
		{
			return getValue().toUpperCase();
		}
		return getValue();
	}
	
	public String getSubnameValue()
	{
		if (subnameValue != null)
		{
			return populateVariables(subnameValue);
		}
		return populateVariables(subnameText);
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
		
		str += "<cardname name=\"" + name + "\" value=\""+getValue()+"\" subnameValue=\"" + getSubnameValue() + "\" />\n";
		
		return str;
	}
	
	public void loadValues(Node node)
	{
		if (!node.getNodeName().equals("cardname"))
		{
			return;
		}
		
		if (node.getAttributes().getNamedItem("value") != null)
		{
			value = node.getAttributes().getNamedItem("value").getNodeValue();
		}
		
		if (node.getAttributes().getNamedItem("subnameValue") != null)
		{
			subnameValue = node.getAttributes().getNamedItem("subnameValue").getNodeValue();
		}
	}
}
