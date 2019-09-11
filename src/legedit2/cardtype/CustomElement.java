package legedit2.cardtype;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import org.w3c.dom.Node;

import legedit2.card.Card;
import legedit2.deck.Deck;
import legedit2.definitions.Icon;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;
import legedit2.imaging.CustomCardMaker;
import legedit2.imaging.GaussianFilter;

public class CustomElement implements Cloneable {

	public String name;
	public CardType template;
	public List<CustomElement> childElements = new ArrayList<CustomElement>();
	public boolean visible = true;
	public int rotate = 0;
	private double scale = 1.0d;
	
	public static String[] elementTypes = new String[]{"cardname", "text", "textarea", "property", "icon", "iconbg", "image", "bgimage", "imagebg", "elementgroup", "scrollingtextarea"};
	
	private JCheckBox visibleCheckbox;
	
	public void drawElement(Graphics2D g)
	{
		
	}
	
	public Font createFont(String fontName, String fallbackFontName, int fontStyle, int textSize)
	{
		if (fallbackFontName == null)
		{
			fallbackFontName = "Percolator.otf";
		}
		
		if (fontName != null)
		{
			try
			{
				return new Font(fontName, fontStyle, getPercentage(textSize, getScale()));
			}
			catch (Exception e)
			{
	    		e.printStackTrace();
			}
		}

		try
		{
			Font newFont = Font.createFont(Font.TRUETYPE_FONT, new File(LegeditHelper.getFontPath(fallbackFontName)));
    		newFont = newFont.deriveFont((float)getPercentage(textSize, getScale()));
    		return newFont;
		}
		catch (Exception e2)
		{
    		e2.printStackTrace();
    		
    		return new Font("Percolator", Font.PLAIN, getPercentage(textSize, getScale()));
		}		
    }	
	
	public BufferedImage getIcon(Icon icon, int maxWidth, int maxHeight)
	{
		ImageIcon ii = new ImageIcon(icon.getImagePath());
		double r = 1d;
		double rX = (double)((double)maxWidth / (double)ii.getIconWidth());
		double rY = (double)((double)maxHeight / (double)ii.getIconHeight());
		if (rY < rX)
		{
			r = rY;
		}
		else
		{
			r = rX;
		}
		
		return resizeImage(ii, r);
	}
	
	public BufferedImage getIconMaxHeight(Icon icon, int maxHeight)
	{
		ImageIcon ii = new ImageIcon(icon.getImagePath());
		double r = (double)((double)maxHeight / (double)ii.getIconHeight());
		
		return resizeImage(ii, r);
	}
	
	public int getPercentageValue(int value, int max)
	{
		return (int)Math.round((double)(((double)value / (double)max) * 100d));
	}
	
	public int getPercentage(int size, double scale)
	{
		return (int)(((double)size * (double)scale));
	}
	
	public BufferedImage resizeImage(ImageIcon imageIcon, double scale)
	{
			int w = (int)(imageIcon.getIconWidth() * scale);
	        int h = (int)(imageIcon.getIconHeight() * scale);
	        int type = BufferedImage.TYPE_INT_ARGB;
	        
	        if (w <= 0 || h <= 0)
	        {
	        	return null;
	        }
	        
	        BufferedImage image = new BufferedImage(w, h, type);
	        Graphics2D g = getGraphics(image);
	        
	        g.drawImage(imageIcon.getImage(), 0, 0, w, h, 
	        		0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
	        
	        g.dispose();
	        
	        return image;
	}
	
	public BufferedImage resizeImage(ImageIcon imageIcon, int width, int height)
	{
	        int type = BufferedImage.TYPE_INT_ARGB;
	        
	        BufferedImage image = new BufferedImage(width, height, type);
	        Graphics2D g = getGraphics(image);
	        
	        g.drawImage(imageIcon.getImage(), 0, 0, width, height, 
	        		0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
	        
	        g.dispose();
	        
	        return image;
	}
	
	public static ConvolveOp getGaussianBlurFilter(int radius,
            boolean horizontal) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }
        
        int size = radius * 2 + 1;
        float[] data = new float[size];
        
        float sigma = radius / 3.0f;
        float twoSigmaSquare = 2.0f * sigma * sigma;
        float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
        float total = 0.0f;
        
        for (int i = -radius; i <= radius; i++) {
            float distance = i * i;
            int index = i + radius;
            data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
            total += data[index];
        }
        
        for (int i = 0; i < data.length; i++) {
            data[i] /= total;
        }        
        
        Kernel kernel = null;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }
	
	public BufferedImage blackoutImage(BufferedImage image, Color blackoutColor) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(image.getRGB(xx, yy), true);
                //System.out.println(xx + "|" + yy + " color: " + originalColor.toString() + "alpha: " + originalColor.getAlpha());
                if (originalColor.getAlpha() > 0) {
                    image.setRGB(xx, yy, blackoutColor.getRGB());
                }
            }
        }
        return image;
    }
	
	public void drawUnderlay(BufferedImage bi, Graphics g, int type, int x, int y, int blurRadius, boolean doubleBlur, int expandBlackout, Color underlayColour)
	{
		BufferedImage blackout = new BufferedImage(template.getCardWidth(), template.getCardHeight(), type);
    	getGraphics(blackout).drawImage(bi, x, y, null);
    	
    	blackout = blackoutImage(blackout, underlayColour);
    	
    	if (expandBlackout > 0)
    	{
    		blackout = expandBlackout(blackout, expandBlackout, underlayColour);
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
	
	public BufferedImage blurImage(BufferedImage bi, Graphics g, int blurRadius)
	{
		if (blurRadius > 0)
    	{
    		BufferedImageOp op = new GaussianFilter( blurRadius );
        	BufferedImage bi2 = op.filter(bi, null);
        	return bi2;
    	}
		return bi;
	}
	
	public BufferedImage expandBlackout(BufferedImage image, int expandBlackout, Color blackoutColor)
	{
		BufferedImage expand = new BufferedImage(template.getCardWidth(), template.getCardHeight(), BufferedImage.TYPE_INT_ARGB);
		
		int width = image.getWidth();
        int height = image.getHeight();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(image.getRGB(xx, yy), true);
                
                if (originalColor.getAlpha() > 0) {
                	//Quick and Dirty - Just ignore out of bounds
                	for (int i = expandBlackout; i > 0; i--)
                	{
                		try { expand.setRGB(xx, yy - i, blackoutColor.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx, yy + i, blackoutColor.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx - i, yy, blackoutColor.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx + i, yy, blackoutColor.getRGB()); } catch (Exception e) {}
                    	
                    	if (i == 1)
                    	{
                    	try { expand.setRGB(xx - i, yy - i, blackoutColor.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx - i, yy + i, blackoutColor.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx + i, yy - i, blackoutColor.getRGB()); } catch (Exception e) {}
                    	try { expand.setRGB(xx + i, yy + i, blackoutColor.getRGB()); } catch (Exception e) {}
                    	}
                	}
                }
            }
        }
        return expand;
	}
	
	public Icon isIcon(String str)
	{
		try
		{
			if (str != null && !str.startsWith("<") && !str.endsWith(">"))
			{
				return null;
			}
			
			Icon i = Icon.valueOf(str.replace("<", "").replace(">", ""));
			return i;
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
	}
	
	public CustomElement getCopy(CardType template)
	{
		try {
			CustomElement e = (CustomElement) clone();
			e.template = template;
			return e;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	public String generateOutputString()
	{
		return generateOutputString(false);
	}
	
	public String generateOutputString(boolean fullExport)
	{
		return "";
	}
	
	public Graphics2D getGraphics(BufferedImage bi)
	{
		Graphics2D g2 = (Graphics2D)bi.getGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				
		g2.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		return g2;
	}
	
	public Graphics2D setGraphicsHints(Graphics2D g2)
	{
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				
		g2.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		return g2;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
	
	public void updateCardValues()
	{
		
	}
	
	public String populateVariables(String s, CustomElement ce)
	{
		if (s == null)
		{
			return s;
		}
		
//		if (Deck.getStaticDeck() != null && s != null)
//		{
//			s = s.replace("%DECKNAME%", Deck.getStaticDeck().getDeckName());
//		}
		
		for (Deck d : ProjectHelper.getDecks())
		{
			for (Card c : d.getCards())
			{
				if (c.getTemplate() != null)
				{
					for (CustomElement e : c.getTemplate().elements)
					{
						if (e.equals(ce))
						{
							s = s.replace("%DECKNAME%", d.getDeckName());
						}
					}
					
					for (Style st : c.getTemplate().getStyles())
					{
						for (CustomElement e : st.getElements())
						{
							if (e.equals(ce))
							{
								s = s.replace("%DECKNAME%", d.getDeckName());
							}
						}
					}
				}
			}
		}
		
		return s;
	}
	
	public String getDifferenceXML()
	{
		return "\n";
	}
	
	public void loadValues(Node node, Card card)
	{
		
	}
	
	public String replaceNonXMLCharacters(String str)
	{
	    if (str == null)
        {
            return str;
        }

		return str.replace("<", "&lt;")
				.replace(">","&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;")
				.replace("&", "&amp;");
	}
	
	public String restoreNonXMLCharacters(String str)
	{
		return str.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&quot;","\"")
				.replace("&apos;", "'")
				.replace("&amp;", "&");
	}

	public JCheckBox getVisibleCheckbox() {
		return visibleCheckbox;
	}

	public void setVisibleCheckbox(JCheckBox visibleCheckbox) {
		this.visibleCheckbox = visibleCheckbox;
	}
}
