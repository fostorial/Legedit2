package legedit2.exporters;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import legedit2.card.Card;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;
import legedit2.imaging.CustomCardMaker;

public class LegeditExporterSingleImage extends LegeditExporter {
	
	private File exportFile;
	private Card card;
	
	public LegeditExporterSingleImage(Card card)
	{
		this.card = card;
	}
	
	public int getExportCount()
	{
		return ProjectHelper.getDistinctCardCount();
	}
	
	public void export(File exportDirectory)
	{
		this.exportFile = exportDirectory;
		
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
				getDialog().getProgressBar().setMaximum(1);
				
				CustomCardMaker maker = new CustomCardMaker();
				maker.setScale(1.0d);
				
				maker.setCard(card);
				BufferedImage bi = maker.generateCard();
				exportSingleImage(bi, card, exportFile);
				
				getDialog().getProgressBar().setValue(1);
				
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(getDialog(), ex.getMessage() != null ? ex.getMessage() : LegeditHelper.getErrorMessage(), LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				
			}
			
			getDialog().setVisible(false);
			
			return null;
		}
	}
}