package legedit2.cardtype;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.w3c.dom.Node;

import legedit2.card.Card;
import legedit2.definitions.Icon;
import legedit2.definitions.Icon.ICON_TYPE;
import legedit2.imaging.CustomCardMaker;

public class ElementIconImage extends CustomElement {
	
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
	
	public int imageX;
	public int imageY;
	public int imageMaxWidth;
	public int imageMaxHeight;
	public String imagePrefix;
	public String imageExtension;
	public String imageFilter;
	
	//User values
	public Icon value;
	
	private JComboBox<Icon> iconCombobox;
	
	public void drawElement(Graphics2D g)
	{
		// Draw BG
		String file = CustomCardMaker.templateFolder + File.separator 
				+ template.getTemplateName()
				+ File.separator + (imagePrefix != null ? imagePrefix : "") 
				+ getIconValue().getEnumName() 
				+ (imageExtension != null ? imageExtension : "");
		
		
		if (file != null && new File(file).exists())
		{
			int cardWidth = template.getCardWidth();
			int cardHeight = template.getCardHeight();
			
			BufferedImage bi = null;
			if (imageFilter == null)
			{
				bi = resizeImage(new ImageIcon(file), getPercentage(cardWidth, getScale()), getPercentage(cardHeight, getScale()));
			}
			if (imageFilter != null && imageFilter.equalsIgnoreCase("dualclass") && !getIconValue().getEnumName().equals("NONE"))
			{
				bi = resizeImage(new ImageIcon(getFadedBackground(new ImageIcon(file))), getPercentage(cardWidth, getScale()), getPercentage(cardHeight, getScale()));
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
			
			g.drawImage(bi, getPercentage(imageX,getScale()), getPercentage(imageY,getScale()), null);
		}
		
		// Draw Icon
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
		if (!node.getNodeName().equals("iconbg"))
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
		
		str += "<iconbg name=\"" + replaceNonXMLCharacters(name) + "\" value=\""+getIconValue().getEnumName()+"\" />\n";
		
		return str;
	}
	
	private BufferedImage getFadedBackground(ImageIcon ii)
	{
		int cardWidth = template.getCardWidth();
		int cardHeight = template.getCardHeight();

		BufferedImage bi = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		g.drawImage(ii.getImage(), 0, 0, cardWidth, cardHeight, null);
		
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		int fadeHeight = getPercentage(cardHeight, 0.14d);
		double increment = 255d / (double)fadeHeight;
		
//		int alpha = 0;
//		for (int xx = 0; xx < width; xx++) {
//            for (int yy = 0; yy < height; yy++) {
//                Color originalColor = new Color(bi.getRGB(xx, yy), true);
//                if (originalColor.getAlpha() > 0) {
//                	
//                	if (yy <= ((cardHeight / 2) + (fadeHeight / 2)))
//                    {
//                		
//                    	alpha = (int)((((cardHeight / 2) + (fadeHeight / 2)) - yy) * increment);
//                    	if (alpha > 255)
//                    	{
//                    		alpha = 255;
//                    	}
//              
//                    }
//                	
//                    else
//                    {
//                    	alpha = 0;
//                    }
//                	
//                    int col = (alpha << 24) | (originalColor.getRed() << 16) | (originalColor.getGreen() << 8) | originalColor.getBlue();
//                    bi.setRGB(xx, yy, col);
//                }
//            }
//        }
		
		int alpha = 0;
		for (int xx = 0; xx < width; xx++) {
			int yyn = 0;
            for (int yy = height-1; yy >= 0; yy--) {
                Color originalColor = new Color(bi.getRGB(xx, yy), true);
                if (originalColor.getAlpha() > 0) {
                	
                	if (yy >= ((cardHeight / 2) - (fadeHeight / 2)))
                    {
                		
                    	alpha = (int)((((cardHeight / 2) + (fadeHeight / 2)) - yyn) * increment);
                    	if (alpha > 255)
                    	{
                    		alpha = 255;
                    	}
              
                    }
                	
                    else
                    {
                    	alpha = 0;
                    }
                	
                    int col = (alpha << 24) | (originalColor.getRed() << 16) | (originalColor.getGreen() << 8) | originalColor.getBlue();
                    bi.setRGB(xx, yy, col);
                    yyn++;
                }
            }
        }
		
		g.dispose();
		
		return bi;
	}
}
