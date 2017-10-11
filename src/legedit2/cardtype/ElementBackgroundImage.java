package legedit2.cardtype;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.w3c.dom.Node;

import legedit2.card.Card;
import legedit2.imaging.CustomCardMaker;

public class ElementBackgroundImage extends CustomElement {
	
	public String path;
	public boolean allowChange = false;
	public int x;
	public int y;
	public int maxWidth = Integer.MAX_VALUE;
	public int maxHeight = Integer.MAX_VALUE;
	public boolean zoomable;
	public boolean fullSize;
	public boolean templateFile;
	
	public double zoom = 1.0d;
	public int imageOffsetX = 0;
	public int imageOffsetY = 0;
	
	private JLabel fileNameLabel;
	private JButton browseButton;
	private JFileChooser chooser;
	private JTextField zoomField;
	private JTextField offsetXField;
	private JTextField offsetYField;
	
	public void drawElement(Graphics2D g)
	{
		String file = path;
		if (templateFile)
		{
			file = CustomCardMaker.templateFolder + File.separator 
					+ template.getTemplateName() 
					+ File.separator + path; 
		}
		else
		{
			file = path;
		}
		
		
		if (file != null)
		{
			File fileValue = new File(file);
			if (fileValue.exists())
			{
				BufferedImage bi = null;
				if (fullSize)
				{
					bi = resizeImage(new ImageIcon(file), getPercentage(CustomCardMaker.cardWidth,getScale()), getPercentage(CustomCardMaker.cardHeight,getScale()));				
				}
				else
				{
					ImageIcon ii = new ImageIcon(file);
					bi = resizeImage(new ImageIcon(file), (int)(getPercentage(ii.getIconWidth(),getScale()) * zoom), (int)(getPercentage(ii.getIconHeight(),getScale()) * zoom));
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
				
				g.drawImage(bi, getPercentage(x + imageOffsetX,getScale()), getPercentage(y + imageOffsetY,getScale()), null);
			}
		}
	}
	
	public String generateOutputString()
	{
		return generateOutputString(false);
	}
	
	public String generateOutputString(boolean fullExport)
	{
		String str = "";
		if (allowChange)
		{
			str += "CUSTOMVALUE;" + name + ";path;" + path + "\n";
		}
		
		str += "CUSTOMVALUE;" + name + ";visible;" + visible + "\n";
		
		str += "CUSTOMVALUE;" + name + ";zoom;" + zoom + "\n";
		str += "CUSTOMVALUE;" + name + ";imageOffsetX;" + imageOffsetX + "\n";
		str += "CUSTOMVALUE;" + name + ";imageOffsetY;" + imageOffsetY + "\n";
		
		return str;
	}
	
	public void loadValues(Node node, Card card)
	{
		if (!node.getNodeName().equals("bgimage"))
		{
			return;
		}
		
		if (node.getAttributes().getNamedItem("path") != null)
		{
			path = node.getAttributes().getNamedItem("path").getNodeValue();
		}
		
		if (node.getAttributes().getNamedItem("zoom") != null)
		{
			zoom = Double.parseDouble(node.getAttributes().getNamedItem("zoom").getNodeValue());
		}
		
		if (node.getAttributes().getNamedItem("imageOffsetX") != null)
		{
			imageOffsetX = Integer.parseInt(node.getAttributes().getNamedItem("imageOffsetX").getNodeValue());
		}
		
		if (node.getAttributes().getNamedItem("imageOffsetY") != null)
		{
			imageOffsetY = Integer.parseInt(node.getAttributes().getNamedItem("imageOffsetY").getNodeValue());
		}
		
		if (offsetXField != null)
		{
			offsetXField.setText("" + imageOffsetX);
		}
		
		if (offsetYField != null)
		{
			offsetYField.setText("" + imageOffsetY);
		}
	}
	
	public String getDifferenceXML()
	{
		String str = "";
		
		str += "<bgimage name=\"" + name + "\" path=\""+path+"\" zoom=\""+zoom+"\" imageOffsetX=\""+imageOffsetX+"\" imageOffsetY=\""+imageOffsetY+"\" />\n";
		
		return str;
	}

	public JLabel getFileNameLabel() {
		return fileNameLabel;
	}

	public void setFileNameLabel(JLabel fileNameLabel) {
		this.fileNameLabel = fileNameLabel;
	}

	public JButton getBrowseButton() {
		return browseButton;
	}

	public void setBrowseButton(JButton browseButton) {
		this.browseButton = browseButton;
	}

	public JFileChooser getChooser() {
		return chooser;
	}

	public void setChooser(JFileChooser chooser) {
		this.chooser = chooser;
	}
	
	@Override
	public void updateCardValues()
	{
		if (chooser != null && chooser.getSelectedFile() != null)
		{
			path = chooser.getSelectedFile().getAbsolutePath();
		}
		
		if (zoomField != null && zoomable)
		{
			try
			{
				zoom = Double.parseDouble(zoomField.getText());
			}
			catch (Exception e)
			{
				/* DO NOTHING */
			}
		}
		
		if (offsetXField != null && !fullSize)
		{
			try
			{
				imageOffsetX = Integer.parseInt(offsetXField.getText());
			}
			catch (Exception e)
			{
				/* DO NOTHING */
			}
		}
		
		if (offsetYField != null && !fullSize)
		{
			try
			{
				imageOffsetY = Integer.parseInt(offsetYField.getText());
			}
			catch (Exception e)
			{
				/* DO NOTHING */
			}
		}
	}

	public JTextField getZoomField() {
		return zoomField;
	}

	public void setZoomField(JTextField zoomField) {
		this.zoomField = zoomField;
	}

	public JTextField getOffsetYField() {
		return offsetYField;
	}

	public void setOffsetYField(JTextField offsetYField) {
		this.offsetYField = offsetYField;
	}

	public JTextField getOffsetXField() {
		return offsetXField;
	}

	public void setOffsetXField(JTextField offsetXField) {
		this.offsetXField = offsetXField;
	}

}
