package legedit2.exporters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import legedit2.card.Card;
import legedit2.deck.Deck;
import legedit2.definitions.LegeditItem;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;
import legedit2.imaging.CustomCardMaker;

public class LegeditExporterSingleImagesBleeder extends LegeditExporter {
	
	private File exportDirectory;
	
	//Resize for printer studio
	static double xPadding = 0.043;
	static double yPadding = 0.08;
	static Color backColor = Color.BLACK;
	static boolean fancyBleed = true;
	static float compressionQuality = 0.0f;
	
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
				for (LegeditItem i : items)
				{
					if (i instanceof Deck)
					{
						for (Card c : ((Deck)i).getCards())
						{
							maker.setCard(c);
							BufferedImage bi = maker.generateCard();
							bi = resizeImage(bi);
							exportImage(bi, c, exportDirectory);
							
							value++;
							getDialog().getProgressBar().setValue(value);
						}
					}
					
					if (i instanceof Card)
					{
						maker.setCard((Card)i);
						BufferedImage bi = maker.generateCard();
						bi = resizeImage(bi);
						exportImage(bi, (Card)i, exportDirectory);
						
						value++;
						getDialog().getProgressBar().setValue(value);
					}
				}
				
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(getDialog(), ex.getMessage() != null ? ex.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				
			}
			
			getDialog().setVisible(false);
			
			return null;
		}
		
		public BufferedImage resizeImage(BufferedImage imageIcon)
		{
				int w = (int)(imageIcon.getWidth());
				int xPad = (int)((imageIcon.getWidth()) * xPadding);
				int fullW = w + xPad + xPad;
		        int h = (int)(imageIcon.getHeight());
		        int yPad = (int)((imageIcon.getHeight()) * yPadding);
		        int fullH = h + yPad + yPad;
		        int type = BufferedImage.TYPE_INT_ARGB;
		        if (isJpegMode())
		        {
		        	type = BufferedImage.TYPE_INT_RGB;
		        }
		        BufferedImage image = new BufferedImage(fullW, fullH, type);
		        Graphics g = image.getGraphics();
		        
		        //g.setColor(backColor);
		        //g.fillRect(0, 0, fullW, fullH);
		        
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
		        		g.drawImage(imageIcon, xPad - increment, yPad - increment, w + xPad + increment, h + yPad + increment, 
		    	        		0, 0, imageIcon.getWidth(), imageIcon.getHeight(), null);
		        		
		        		increment--;
		        	}
		        }
		        
		        g.drawImage(imageIcon, xPad, yPad, w + xPad, h + yPad, 
		        		0, 0, imageIcon.getWidth(), imageIcon.getHeight(), null);
		        
		        g.dispose();
		        
		        return image;
		}
	}
}
