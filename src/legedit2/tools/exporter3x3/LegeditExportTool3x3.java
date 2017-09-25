package legedit2.tools.exporter3x3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.w3c.dom.Element;

public class LegeditExportTool3x3 extends JFrame 
{
		// 2.5 by 3.5 inches - Poker Size
		static int cardWidth = 750;
		static int cardHeight = 1050;
		
		static double scale = 2.0;
		static double xPadding = 0.02;
		static double yPadding = 0.02;
		
		static int dpi = 300;
		
		static List<File> files = new ArrayList<File>();
		static List<File> cards = new ArrayList<File>();

		static String expansionDirectory = "";
			
		static String exportFolder = "export";
		//static String exportFileName = "expansion_";
		//static String exportFileName = "replacement_";
		static String exportFileName = "export_";
		
		static String[] includedFolders = new String[] {"Heroes", "Villains", "Masterminds", "Bystanders", "Other", "Wounds", "Schemes"};
		
		public LegeditExportTool3x3() {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			setTitle("Export Tool 3x3");
			
			add(new JLabel("Working..."));
			setVisible(true);
			
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int outcome = chooser.showOpenDialog(this);
			if (outcome == JFileChooser.APPROVE_OPTION)
			{
				expansionDirectory = chooser.getSelectedFile().getAbsolutePath();
			}
			else
			{
				JOptionPane.showMessageDialog(this, "No File Selected");
				this.dispose();
				return;
			}
			
			doExport();
			
			JOptionPane.showMessageDialog(this, "Done!");
			
			this.dispose();
			
			return;
		}
		
		private void doExport()
		{
			prepareFiles();
			
			File file = new File(expansionDirectory + File.separator + exportFolder + File.separator);
			file.mkdirs();
			
			List<BufferedImage> images = new ArrayList<BufferedImage>();
			System.out.println();
			System.out.println("Preloading Images");
			for (File f : cards)
			{
				BufferedImage image = resizeImage(new ImageIcon(f.getAbsolutePath()), cardWidth, cardHeight);
				images.add(image);
			}
			
			System.out.println();
			System.out.println("Exporting Sheets");
			
			int i = 0;
			int j = 1;
			
			int type = BufferedImage.TYPE_INT_RGB;
	        BufferedImage image = new BufferedImage(images.get(0).getWidth() * 3, images.get(0).getHeight() * 3, type);
	        Graphics g = image.getGraphics();
	        
	        g.setColor(Color.white);
	        g.fillRect(0, 0, image.getWidth(), image.getHeight());
	        
	        int x = 0; int y = 0;
			for (BufferedImage bi : images)
			{
				g.drawImage(bi, x, y, null);
				
				x += bi.getWidth();
				if (x >= bi.getWidth() * 3)
				{
					y += bi.getHeight();
					x = 0;
				}
					
				i++;
				if (i == 9)
				{
					i=0;
					x = 0; y = 0;
					try
					{
						File newFile = new File(file.getAbsoluteFile() + File.separator + exportFileName + j + ".jpg");
						//ImageIO.write(image, "jpg", newFile);
						exportToJPEG(image, newFile);
					}
					catch (Exception e) { e.printStackTrace(); }
					
					g.dispose();
					image = new BufferedImage(images.get(0).getWidth() * 3, images.get(0).getHeight() * 3, type);
			        g = image.getGraphics();
			        g.setColor(Color.white);
			        g.fillRect(0, 0, image.getWidth(), image.getHeight());
			        j++;
				}
			}
			
			if (i < 9)
			{
				i=0;
				try
				{
					File newFile = new File(file.getAbsoluteFile() + File.separator + exportFileName + j + ".jpg");
					//ImageIO.write(image, "jpg", newFile);
					exportToJPEG(image, newFile);
				}
				catch (Exception e) { e.printStackTrace(); }
		        j++;
			}
			
			System.out.println();
			System.out.println("Exported " + j + " Sheets");
			System.out.println();
			System.out.println("Export Complete");
			
			g.dispose();
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
		        BufferedImage image = new BufferedImage(fullW, fullH, type);
		        Graphics g = image.getGraphics();
		        
		        g.drawImage(imageIcon.getImage(), xPad, yPad, w + xPad, h + yPad, 
		        		0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
		        
		        g.dispose();
		        
		        return image;
		}
		
		public static BufferedImage resizeImage(ImageIcon imageIcon, int width, int height)
		{
				int w = width;
				int xPad = (int)((width) * xPadding);
				int fullW = w + xPad + xPad;
		        int h = (int)(height);
		        int yPad = (int)((height) * yPadding);
		        int fullH = h + yPad + yPad;
		        int type = BufferedImage.TYPE_INT_ARGB;
		        BufferedImage image = new BufferedImage(fullW, fullH, type);
		        Graphics g = image.getGraphics();
		        
		        g.drawImage(imageIcon.getImage(), xPad, yPad, w + xPad, h + yPad, 
		        		0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
		        
		        g.dispose();
		        
		        return image;
		}
		
		public static void prepareFiles()
		{
			System.out.println("Preparing Files");
			
			for (String str : includedFolders)
			{
				File file = new File(expansionDirectory + File.separator + str + File.separator);
				System.out.println();
				System.out.println("Preparing: " + file.getName());
				if (file.exists() && file.isDirectory())
				{
					File[] filesArray = file.listFiles();
					for (File f : filesArray)
					{
						if (f.getName().toLowerCase().endsWith(".jpg") 
								|| f.getName().toLowerCase().endsWith(".jpeg") 
								|| f.getName().toLowerCase().endsWith(".png"))
						{
							int dotPos = f.getName().lastIndexOf(".");
							String nameNoExtension = f.getName().substring(0, dotPos);
							System.out.println(nameNoExtension);
							
							files.add(f);
						}
					}
				}
			}
			
			System.out.println();
			System.out.println("Organising Cards");
			cards = new ArrayList<File>();
			
			for (File f : files)
			{
				int dotPos = f.getName().lastIndexOf(".");
				String nameNoExtension = f.getName().substring(0, dotPos);
				
				String[] folderSplit = f.getAbsolutePath().split(File.separator);
				String parentFolder = folderSplit[folderSplit.length - 2];
				
				if (parentFolder.toLowerCase().equals("heroes"))
				{
					if (nameNoExtension.contains("_common"))
					{
						cards.add(f);
						cards.add(f);
						cards.add(f);
						cards.add(f);
						cards.add(f);
						System.out.println(nameNoExtension + " x 5");
					}
					
					if (nameNoExtension.contains("_uncommon"))
					{
						cards.add(f);
						cards.add(f);
						cards.add(f);
						System.out.println(nameNoExtension + " x 3");
					}
					
					if (nameNoExtension.contains("_rare"))
					{
						cards.add(f);
						System.out.println(nameNoExtension + " x 1");
					}
					
					if (nameNoExtension.contains("_x_"))
					{
						int count = 1;
						String[] split = nameNoExtension.split("_x_");
						try
						{
							count = Integer.parseInt(split[split.length -1]);
							for (int i = 0; i < count; i++)
							{
								cards.add(f);
							}
						}
						catch (Exception e)
						{
							cards.add(f);
						}
						System.out.println(nameNoExtension + " x " + count);
					}
				}
				else if (parentFolder.toLowerCase().equals("villains"))
				{
					if (nameNoExtension.contains("_henchmen"))
					{
						cards.add(f);
						cards.add(f);
						cards.add(f);
						cards.add(f);
						cards.add(f);
						cards.add(f);
						cards.add(f);
						cards.add(f);
						cards.add(f);
						cards.add(f);
						System.out.println(nameNoExtension + " x 10");
					}
					else if (nameNoExtension.contains("_x_"))
					{
						int count = 1;
						String[] split = nameNoExtension.split("_x_");
						try
						{
							count = Integer.parseInt(split[split.length -1]);
							for (int i = 0; i < count; i++)
							{
								cards.add(f);
							}
						}
						catch (Exception e)
						{
							cards.add(f);
						}
						System.out.println(nameNoExtension + " x " + count);
					}
					else
					{
						cards.add(f);
						cards.add(f);
						System.out.println(nameNoExtension + " x 2");
					}
				}
				//Anything else
				else
				{
					if (nameNoExtension.contains("_x_"))
					{
						int count = 1;
						String[] split = nameNoExtension.split("_x_");
						try
						{
							count = Integer.parseInt(split[split.length -1]);
							for (int i = 0; i < count; i++)
							{
								cards.add(f);
							}
						}
						catch (Exception e)
						{
							cards.add(f);
						}
						System.out.println(nameNoExtension + " x " + count);
					}
					else
					{
						cards.add(f);
						System.out.println(nameNoExtension + " x 1");
					}
				}
			}
		}
		
		public static void exportToJPEG(BufferedImage image, File newFile) throws Exception
		{
			FileOutputStream fos = new FileOutputStream(newFile);
			ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
			//JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpeg").next();
		    ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
		    imageWriter.setOutput(ios);
		 
		    //and metadata
		    IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), null);
		    
		  //new metadata
	        Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
	        Element jfif = (Element)tree.getElementsByTagName("app0JFIF").item(0);
	        jfif.setAttribute("Xdensity", Integer.toString(dpi));
			jfif.setAttribute("Ydensity", Integer.toString(dpi));
			jfif.setAttribute("resUnits", "1"); // density is dots per inch	
	        imageMetaData.setFromTree("javax_imageio_jpeg_image_1.0", tree);
	        
	        
	        JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
	        jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
	        jpegParams.setCompressionQuality(1f);
	        
	      //new Write and clean up
	        imageWriter.write(null, new IIOImage(image, null, imageMetaData), jpegParams);
	        ios.close();
	        imageWriter.dispose();
		}
}
