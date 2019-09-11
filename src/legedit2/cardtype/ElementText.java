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

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.w3c.dom.Node;

import legedit2.card.Card;
import legedit2.definitions.Icon;
import legedit2.helpers.LegeditHelper;
import legedit2.imaging.CustomCardMaker;

public class ElementText extends CustomElement {

	public String defaultValue;
	public CustomElement linkedElement;
	public ALIGNMENT alignment = ALIGNMENT.CENTER;
	public boolean allowChange;
	public int x;
	public int y;
	public Color colour;
	public boolean drawUnderlay;
	public int blurRadius;
	public boolean blurDouble;
	public int blurExpand;
	public Color blurColour;
	public int textSize;
	public String fontName;
	public int fontStyle;
	public boolean uppercase;
	
	public String value;
	
	private JTextField textField;
	private JButton fontButton;
	
	public void drawElement(Graphics2D g)
	{
		if (getValue() != null && visible == true)
		{
			BufferedImage bi = new BufferedImage(getPercentage(template.getCardWidth(), getScale()), getPercentage(template.getCardHeight(), getScale()), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = getGraphics(bi);
			g2 = setGraphicsHints(g2);
			
			if (colour != null)
			{
				g2.setColor(colour);
			}
			
			Font font = createFont(fontName, "Sylfaen.ttf", fontStyle, textSize);

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
	        int yModified = getPercentage(y,getScale()) + (int)((lm.getAscent() - lm.getDescent()) / 2);
	        
	        g2.drawString(getValueForDraw(), newx, yModified);
	    	if (drawUnderlay)
	    	{
	    		drawUnderlay(bi, g2, BufferedImage.TYPE_INT_ARGB, 0, 0, getPercentage(blurRadius,getScale()), blurDouble, getPercentage(blurExpand,getScale()), blurColour);
	    	}
	    	
	    	g2.drawString(getValueForDraw(), newx, yModified);

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
			return value;
		}
		return defaultValue;
	}
	
	private String getValueForDraw()
	{
		if (uppercase && getValue() != null)
		{
			return getValue().toUpperCase();
		}
		return getValue();
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
		
		str += "CUSTOMVALUE;" + name + ";visible;" + visible + "\n";
		
		return str;
	}

	public JTextField getTextField() {
		return textField;
	}

	public void setTextField(JTextField textField) {
		this.textField = textField;
	}
	
	@Override
	public void updateCardValues()
	{
		if (textField != null)
		{
			value = textField.getText();
		}
	}
	
	public void loadValues(Node node, Card card)
	{
		if (!node.getNodeName().equals("text"))
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
		
		if (node.getAttributes().getNamedItem("fontstyle") != null)
		{
			fontStyle = Integer.parseInt(node.getAttributes().getNamedItem("fontstyle").getNodeValue());
		}

		if (node.getAttributes().getNamedItem("textsize") != null)
		{
			textSize = Integer.parseInt(node.getAttributes().getNamedItem("textsize").getNodeValue());
		}
	}
	
	public String getDifferenceXML()
	{
		String str = "";
		
		str += "<text name=\"" + replaceNonXMLCharacters(name) + "\" value=\""+replaceNonXMLCharacters(getValue())+ "\" "
				+ (fontName == null ? "" : "fontname=\""+replaceNonXMLCharacters(fontName) + "\" ")
				+ (fontName == null ? "" : "fontstyle=\""+fontStyle+"\" ")
				+ "textsize=\""+textSize+"\" />\n";
		

		return str;
	}

	public void setFontButton(JButton fontButton) {
		this.fontButton = fontButton;
	}	
}
