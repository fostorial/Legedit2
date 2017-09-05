package legedit2.cardtype;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.w3c.dom.Node;

import legedit2.card.Card;
import legedit2.definitions.Icon;
import legedit2.definitions.Icon.ICON_TYPE;

public class ElementIcon extends CustomElement {
	
	public Icon defaultValue = Icon.valueOf("NONE");
	public boolean allowChange = false;
	public boolean optional = false;
	public int x;
	public int y;
	public int maxWidth = Integer.MAX_VALUE;
	public int maxHeight = Integer.MAX_VALUE;
	public boolean drawUnderlay;
	public int blurRadius;
	public boolean blurDouble;
	public int blurExpand;
	public Color blurColour;
	public ICON_TYPE iconType;
	public String valueFrom;
	
	//User values
	public Icon value;
	
	private JComboBox<Icon> iconCombobox;
	
	public void drawElement(Graphics2D g)
	{
		if (visible && getIconValue().getImagePath() != null)
		{
			BufferedImage bi = getIcon(getIconValue(), getPercentage(maxWidth,getScale()), getPercentage(maxHeight,getScale()));
			int xStart = getPercentage(x,getScale()) - (bi.getWidth() / 2);
	    	int yStart = getPercentage(y,getScale()) - (bi.getHeight() / 2);
	    	
	    	if (drawUnderlay)
	    	{
	    		drawUnderlay(bi, g, BufferedImage.TYPE_INT_ARGB, xStart, yStart, getPercentage(blurRadius,getScale()), blurDouble, getPercentage(blurExpand,getScale()), blurColour);
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
	    	
	    	g.drawImage(bi, xStart, yStart, null);
		}
	}
	
	public Icon getIconValue()
	{
		if (valueFrom != null)
		{
			for (CustomElement el : template.elements)
			{
				if (el.name.equalsIgnoreCase(valueFrom))
				{
					if (el instanceof ElementIcon)
					{
						return ((ElementIcon)el).getIconValue();
					}
					if (el instanceof ElementIconImage)
					{
						return ((ElementIconImage)el).getIconValue();
					}
				}
			}
		}
		if (value != null)
		{
			return value;
		}
		if (defaultValue != null)
		{
			return defaultValue;
		}
		return Icon.valueOf("NONE");
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

	public JComboBox<Icon> getIconCombobox() {
		return iconCombobox;
	}

	public void setIconCombobox(JComboBox<Icon> iconCombobox) {
		this.iconCombobox = iconCombobox;
	}
	
	@Override
	public void updateCardValues()
	{
		if (iconCombobox != null)
		{
			value = (Icon)iconCombobox.getSelectedItem();
		}
	}
	
	public void loadValues(Node node, Card card)
	{
		if (!node.getNodeName().equals("icon"))
		{
			return;
		}
		
		if (node.getAttributes().getNamedItem("value") != null)
		{
			value = Icon.valueOf(node.getAttributes().getNamedItem("value").getNodeValue());
		}
	}
	
	public String getDifferenceXML()
	{
		String str = "";
		
		str += "<icon name=\"" + name + "\" value=\""+getIconValue().getEnumName()+"\" />\n";
		
		return str;
	}
}
