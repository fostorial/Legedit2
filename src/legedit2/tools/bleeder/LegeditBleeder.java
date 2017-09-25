package legedit2.tools.bleeder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class LegeditBleeder extends JFrame 
{
		static String filePath = "";
		
		//Resize for printer studio
		static double scale = 2.0;
		static double xPadding = 0.043;
		static double yPadding = 0.08;
		static String exportType = "jpg"; //png or jpg
		
		//static String filePath = null;
		
		static Color backColor = Color.BLACK;
		
		static boolean fancyBleed = false;
		
		/*
		//Convert to JPG
		static double scale = 0.6;
		static double xPadding = 0;
		static double yPadding = 0;
		static String exportType = "jpg"; //png or jpg
		*/
		
		static float compressionQuality = 0.0f;

		public LegeditBleeder() {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			setTitle("Bleeder");
			
			add(new JLabel("Working..."));
			setVisible(true);
			
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int outcome = chooser.showOpenDialog(this);
			if (outcome == JFileChooser.APPROVE_OPTION)
			{
				filePath = chooser.getSelectedFile().getAbsolutePath();
			}
			else
			{
				JOptionPane.showMessageDialog(this, "No File Selected");
				this.dispose();
				return;
			}
			
			try
			{
				String str = JOptionPane.showInputDialog("Enter the scale (original size X this value)", "" + scale);
				scale = Double.parseDouble(str);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage());
				this.dispose();
				return;
			}
			
			JColorChooser color = new JColorChooser(backColor);
			Color c = color.showDialog(this, "Select a backing color", backColor);
			if (c != null)
			{
				backColor = c;
			}
			else
			{
				backColor = Color.GRAY;
			}
			
			try
			{
				String str = JOptionPane.showInputDialog("Enter the horizontal padding (% to expand horizontal bleed)", "" + xPadding);
				xPadding = Double.parseDouble(str);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage());
				this.dispose();
				return;
			}
			
			try
			{
				String str = JOptionPane.showInputDialog("Enter the vertical padding (% to expand vertical bleed)", "" + yPadding);
				yPadding = Double.parseDouble(str);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage());
				this.dispose();
				return;
			}
			
			outcome = JOptionPane.showOptionDialog(this, "Do Fancy Bleed? This will take longer.", "Fancy Bleed?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (outcome == JOptionPane.YES_OPTION)
			{
				fancyBleed = true;
			}
			else
			{
				fancyBleed = false;
			}
			
			doResize();
			
			JOptionPane.showMessageDialog(this, "Done!");
			
			this.dispose();
			
			return;
		}
		
		public void doResize()
		{

			File folder = new File(filePath);
			if (folder.exists() && folder.isDirectory())
			{
				File[] files = folder.listFiles();
				for (File f : files)
				{
					System.out.println(f.getAbsolutePath());
					
					ImageIcon imageIcon = null;
					String imageType = null;
					
					if (f.getAbsolutePath().toLowerCase().endsWith("jpg") 
							|| f.getAbsolutePath().toLowerCase().endsWith("jpeg"))
					{
						try
						{
							imageIcon = new ImageIcon(f.getAbsolutePath());
							imageType = "jpg";
						}
						catch (Exception e) { e.printStackTrace(); }
					}
					
					if (f.getAbsolutePath().toLowerCase().endsWith("png"))
					{
						try
						{
							imageIcon = new ImageIcon(f.getAbsolutePath());
							imageType = "png";
						}
						catch (Exception e) { e.printStackTrace(); }
					}
					
					if (imageIcon != null && imageType != null)
					{
						try
						{
							File dir = new File(folder.getAbsoluteFile() + File.separator + "resized" + File.separator);
							dir.mkdirs();
							File newFile = new File(folder.getAbsoluteFile() + File.separator + "resized" + File.separator + f.getName().replace(".png", "").replace(".jpg", "").replace(".jpeg", "") + "." + exportType);
							
							//ImageIO.write(resizeImage(imageIcon, scale), exportType, newFile);
							
							BufferedImage bi = resizeImage(imageIcon, scale);
							
							if (exportType.equals("jpg"))
							{
								try {
							        // Retrieve jpg image to be compressed
							        BufferedImage rendImage = bi;

							        // Find a jpeg writer
							        ImageWriter writer = null;
							        @SuppressWarnings("rawtypes")
									Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
							        if (iter.hasNext()) {
							            writer = (ImageWriter)iter.next();
							        }

							        // Prepare output file
							        newFile.createNewFile();
							        ImageOutputStream ios = ImageIO.createImageOutputStream(newFile);
							        writer.setOutput(ios);

							        // Set the compression quality
							        ImageWriteParam iwparam = new MyImageWriteParam();
							        iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT) ;
							        iwparam.setCompressionQuality(compressionQuality);

							        // Write the image
							        writer.write(null, new IIOImage(rendImage, null, null), iwparam);

							        // Cleanup
							        ios.flush();
							        writer.dispose();
							        ios.close();
							    } catch (IOException e) {
							    }
							}
							else
							{
								ImageIO.write(bi, exportType, newFile);
							}
							
						}
						catch (Exception e) { e.printStackTrace(); }
					}
				}
			}
		}

		public static BufferedImage resizeImage(ImageIcon imageIcon, double scale)
		{
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
		        
		        g.setColor(backColor);
		        g.fillRect(0, 0, fullW, fullH);
		        
		        if (fancyBleed)
		        {
		        	int padSize = xPad;
		        	if (yPad > xPad)
		        	{
		        		padSize = yPad;
		        	}
		        	
		        	int increment = padSize;
		        	for (int i = padSize; i > 0; i--)
		        	{
		        		g.drawImage(imageIcon.getImage(), xPad - increment, yPad - increment, w + xPad + increment, h + yPad + increment, 
		    	        		0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
		        		
		        		increment--;
		        	}
		        }
		        
		        g.drawImage(imageIcon.getImage(), xPad, yPad, w + xPad, h + yPad, 
		        		0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
		        
		        g.dispose();
		        
		        return image;
		}
		
		private static class MyImageWriteParam extends JPEGImageWriteParam {
		    public MyImageWriteParam() {
		        super(Locale.getDefault());
		    }

		    // This method accepts quality levels between 0 (lowest) and 1 (highest) and simply converts
		    // it to a range between 0 and 256; this is not a correct conversion algorithm.
		    // However, a proper alternative is a lot more complicated.
		    // This should do until the bug is fixed.
		    public void setCompressionQuality(float quality) {
		        if (quality < 0.0F || quality > 1.0F) {
		            throw new IllegalArgumentException("Quality out-of-bounds!");
		        }
		        this.compressionQuality = 256 - (quality * 256);
		    }
		}
}
