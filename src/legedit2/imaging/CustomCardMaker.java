package legedit2.imaging;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedWriter;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import legedit2.card.Card;
import legedit2.cardtype.CardType;
import legedit2.cardtype.CustomElement;
import legedit2.definitions.Icon;

public class CustomCardMaker {
	
	public String exportFolder = "cardCreator";
	public static String templateFolder = "legedit" + File.separator + "cardtypes";
	
	private boolean debug = false;
	
	// 2.5 by 3.5 inches - Poker Size
	public static int cardWidth = 750;
	public static int cardHeight = 1050;
	public static int dpi = 300;
	
	boolean exportImage = false;
	public boolean exportToPNG = true;
	
	public Card card;
	
	public BufferedWriter bwErr = null;
	
	public CardType template = null;
	
	private double scale = 1.0d;

	public CustomCardMaker()
	{

	}
	
	public void setCard(Card c)
	{
		card = c;
		template = c.getTemplate();
	}
	
	public void populateCard()
	{
		card = new Card();
	}
	
	public void populateBlankCard()
	{
		card = new Card();
	}
	
	public static Card getBlankCard()
	{
		Card card = new Card();
		return card;
	}
	
	public BufferedImage generateCard()
	{
		int type = BufferedImage.TYPE_INT_ARGB;
		if (exportToPNG)
		{
			type = BufferedImage.TYPE_INT_ARGB;	
		}
	    BufferedImage image = new BufferedImage(getPercentage(cardWidth, getScale()), getPercentage(cardHeight, getScale()), type);
	    Graphics2D g = (Graphics2D)image.getGraphics();
	    
	    if (debug)
	    {
	    	g.setColor(Color.black);
	    	g.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
	    }
	    
	    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    
	    g = setGraphicsHints(g);

	    
	    if (template != null)
	    {
	    	for (CustomElement element : template.elements)
	    	{
	    		element.setScale(scale);
	    		element.drawElement(g);
	    	}
	    	
//	    	if (template.getStyles().size() > 0 && template.getStyle() == null)
//	    	{
//	    		template.setStyle(template.getStyles().get(0));
//	    	}
	    	
	    	if (template.getStyle() != null)
	    	{
	    		for (CustomElement element : template.getStyle().getElements())
		    	{
		    		element.setScale(scale);
		    		element.drawElement(g);
		    	}
	    	}
	    }
	    
	    if (exportImage)
	    {
	    	exportImage(image);
	    }
	    
		g.dispose();
		
		return image;
	}
	
	
	
	public void exportImage(BufferedImage image)
	{
		try
		{
			//ImageIO.write(image, "jpg", newFile);
			if (exportToPNG)
			{
				File newFile = new File(exportFolder + File.separator + card.getCardName(exportFolder) + ".png");
				exportToPNG(image, newFile);
			}
			else
			{
				File newFile = new File(exportFolder + File.separator + card.getCardName(exportFolder) + ".jpg");
				exportToJPEG(image, newFile);
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void exportToJPEG(BufferedImage image, File newFile) throws Exception
	{
		//System.out.println("Exporting: " + newFile.getName());
		
		BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		g.dispose();
		
		File dir = new File(exportFolder);
		dir.mkdirs();
		
		ImageIO.write(bi, "jpeg", newFile);
	}
	
	public void exportToPNG(BufferedImage image, File newFile) throws Exception
	{
		//System.out.println("Exporting: " + newFile.getName());
		ImageIO.write(image, "png", newFile);
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
	        Graphics g = image.getGraphics();
	        
	        g.drawImage(imageIcon.getImage(), 0, 0, w, h, 
	        		0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
	        
	        g.dispose();
	        
	        return image;
	}
	
	public BufferedImage resizeImage(ImageIcon imageIcon, int width, int height)
	{
	        int type = BufferedImage.TYPE_INT_ARGB;
	        
	        BufferedImage image = new BufferedImage(width, height, type);
	        Graphics g = image.getGraphics();
	        
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
	
	private BufferedImage blackoutImage(BufferedImage image, Color blackoutColor) {
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
	
	private void drawUnderlay(BufferedImage bi, Graphics g, int type, int x, int y, int blurRadius, boolean doubleBlur, int expandBlackout, Color underlayColour)
	{
		BufferedImage blackout = new BufferedImage(cardWidth, cardHeight, type);
    	blackout.getGraphics().drawImage(bi, x, y, null);
    	
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
	
	private BufferedImage blurImage(BufferedImage bi, Graphics g, int blurRadius)
	{
		if (blurRadius > 0)
    	{
    		BufferedImageOp op = new GaussianFilter( blurRadius );
        	BufferedImage bi2 = op.filter(bi, null);
        	return bi2;
    	}
		return bi;
	}
	
	private BufferedImage expandBlackout(BufferedImage image, int expandBlackout, Color blackoutColor)
	{
		BufferedImage expand = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_ARGB);
		
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
	
	private Icon isIcon(String str)
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
	
	private BufferedImage createRareBacking(int x, int y, int x2, int y2)
	{
		BufferedImage bi = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g2 = bi.getGraphics();
        
        //System.out.println(x +":"+y+":"+x2+":"+y2+":"+(x2-x)+":"+(y2-y));
        
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, x2 - x, y2 - y);
        
		return bi;
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
	
	public BufferedImage resizeImagePS(BufferedImage bi)
	{
		//Resize for printer studio
		double scale = 2.0;
		double xPadding = 0.043;
		double yPadding = 0.08;
		String exportType = "jpg"; //png or jpg
		
		ImageIcon imageIcon = new ImageIcon(bi);
		
		int w = (int)(imageIcon.getIconWidth() * scale);
		int xPad = (int)((imageIcon.getIconWidth() * scale) * xPadding);
		int fullW = w + xPad + xPad;
        int h = (int)(imageIcon.getIconHeight() * scale);
        int yPad = (int)((imageIcon.getIconHeight() * scale) * yPadding);
        int fullH = h + yPad + yPad;
        int type = BufferedImage.TYPE_INT_ARGB;
        if (exportType.equals("jpg"))
        {
        	type = BufferedImage.TYPE_INT_RGB;
        }
        BufferedImage image = new BufferedImage(fullW, fullH, type);
        Graphics g = image.getGraphics();
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, fullW, fullH);
        
        g.drawImage(imageIcon.getImage(), xPad, yPad, w + xPad, h + yPad, 
        		0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
        
        g.dispose();
        
        return image;
	}
	
	private BufferedImage getFadedBackground(ImageIcon ii)
	{
		BufferedImage bi = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		g.drawImage(ii.getImage(), 0, 0, cardWidth, cardHeight, null);
		
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		int fadeHeight = getPercentage(cardHeight, 0.14d);
		double increment = 255d / (double)fadeHeight;
		
		int alpha = 0;
		for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(bi.getRGB(xx, yy), true);
                if (originalColor.getAlpha() > 0) {
                	
                	if (yy <= ((cardHeight / 2) + (fadeHeight / 2)))
                    {
                		
                    	alpha = (int)((((cardHeight / 2) + (fadeHeight / 2)) - yy) * increment);
                    	if (alpha > 255)
                    	{
                    		alpha = 255;
                    	}
                    	
                    	if (yy <= ((cardHeight / 2) + (fadeHeight / 2)) && xx == 0)
                		{
                			//System.out.println(yy + ":" + alpha);
                		}
                    }
                	
                    else
                    {
                    	alpha = 0;
                    }
                	
                    int col = (alpha << 24) | (originalColor.getRed() << 16) | (originalColor.getGreen() << 8) | originalColor.getBlue();
                    bi.setRGB(xx, yy, col);
                }
            }
        }
		
		g.dispose();
		
		return bi;
	}
	
	private BufferedImage clearHalfImage(BufferedImage bi, boolean leftHalf)
	{
		Graphics2D g2D = (Graphics2D)bi.getGraphics();
		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		Rectangle2D.Double rect = null;
		if (!leftHalf)
		{
			rect = new Rectangle2D.Double(0,0,bi.getWidth() / 2, bi.getHeight());
		}
		else
		{
			rect = new Rectangle2D.Double(bi.getWidth() / 2,0,bi.getWidth(), bi.getHeight());
		}
		g2D.fill(rect);
		g2D.dispose();
		return bi;
	}
	
	private Graphics2D getGraphics(BufferedImage bi)
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
	
	private Graphics2D setGraphicsHints(Graphics2D g2)
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

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}