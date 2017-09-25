package legedit2.exporters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import legedit2.card.Card;
import legedit2.deck.Deck;
import legedit2.definitions.LegeditItem;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;
import legedit2.imaging.CustomCardMaker;

public class LegeditExporter3by3 extends LegeditExporter {
	
	private File exportDirectory;
	
	static double xPadding = 0.02;
	static double yPadding = 0.02;
	
	// 2.5 by 3.5 inches - Poker Size
		static int cardWidth = 750;
		static int cardHeight = 1050;
	
	static int dpi = 300;
	
	public int getExportCount()
	{
		return ProjectHelper.getDistinctCardCount();
	}
	
	public void export(File exportDirectory)
	{
		this.exportDirectory = exportDirectory;
		
		setMaxValue(getExportCount());
		setCurrentValue(0);
		
		Task task = new Task();
		
		task.execute();
		
		return;
	}
	
	class Task extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			
			try
			{
				getDialog().getProgressBar().setValue(0);
				getDialog().getProgressBar().setMaximum(getMaxValue());
				
				List<LegeditItem> items = ProjectHelper.getLegeditItems();
				
				CustomCardMaker maker = new CustomCardMaker();
				maker.setScale(1.0d);
				
				int value = 0;
				
				List<BufferedImage> images = new ArrayList<BufferedImage>();
				System.out.println();
				System.out.println("Preloading Images");
				for (LegeditItem li : items)
				{
					BufferedImage bi = null;
					if (li instanceof Deck)
					{
						for (Card c : ((Deck)li).getCards())
						{
							maker.setCard(c);
							bi = maker.generateCard();
							value++;
							getDialog().getProgressBar().setValue(value);
							
							BufferedImage image = resizeImage(bi, cardWidth, cardHeight);
							for (int i = 0; i < ((Card)c).getNumberInDeck(); i++)
							{
								images.add(image);
							}
						}
					}
					
					if (li instanceof Card)
					{
						maker.setCard((Card)li);
						bi = maker.generateCard();
						value++;
						getDialog().getProgressBar().setValue(value);
						
						BufferedImage image = resizeImage(bi, cardWidth, cardHeight);
						for (int i = 0; i < ((Card)li).getNumberInDeck(); i++)
						{
							images.add(image);
						}
					}
					
					
				}
				
				int i = 0;
				
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
							exportImage(image, null, exportDirectory);
						}
						catch (Exception e) { e.printStackTrace(); }
						
						g.dispose();
						image = new BufferedImage(images.get(0).getWidth() * 3, images.get(0).getHeight() * 3, type);
				        g = image.getGraphics();
				        g.setColor(Color.white);
				        g.fillRect(0, 0, image.getWidth(), image.getHeight());
					}
				}
				
				if (i < 9)
				{
					i=0;
					try
					{
						exportImage(image, null, exportDirectory);
					}
					catch (Exception e) { e.printStackTrace(); }
				}
				
				g.dispose();
				
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(getDialog(), ex.getMessage() != null ? ex.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
			}
			
			getDialog().setVisible(false);
			
			return null;
		}
		
		public BufferedImage resizeImage(ImageIcon imageIcon, double scale)
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
		
		public BufferedImage resizeImage(BufferedImage imageIcon, int width, int height)
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
		        
		        g.drawImage(imageIcon, xPad, yPad, w + xPad, h + yPad, 
		        		0, 0, imageIcon.getWidth(), imageIcon.getHeight(), null);
		        
		        g.dispose();
		        
		        return image;
		}
	}
}
