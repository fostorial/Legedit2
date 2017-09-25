package legedit2.exporters;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import legedit2.card.Card;
import legedit2.helpers.ProjectHelper;

public class LegeditExporter {
	
	private int currentValue;
	private int maxValue;
	
	private LegeditExportDialog dialog;
	
	private boolean jpegMode = false;

	public int getExportCount()
	{
		return 100;
	}
	
	public void export(File exportDirectory)
	{
		return;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

	public LegeditExportDialog getDialog() {
		return dialog;
	}

	public void setDialog(LegeditExportDialog dialog) {
		this.dialog = dialog;
	}

	public boolean isJpegMode() {
		return jpegMode;
	}

	public void setJpegMode(boolean jpegMode) {
		this.jpegMode = jpegMode;
	}
	
	public void exportImage(BufferedImage image, Card card, File exportFolder)
	{
		try
		{
			//ImageIO.write(image, "jpg", newFile);
			if (!jpegMode)
			{
				File newFile = new File(exportFolder.getAbsolutePath() + File.separator + getExportName(card, exportFolder) + ".png");
				exportToPNG(image, newFile);
			}
			else
			{
				File newFile = new File(exportFolder.getAbsolutePath() + File.separator + getExportName(card, exportFolder) + ".jpg");
				exportToJPEG(image, newFile);
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void exportSingleImage(BufferedImage image, Card card, File exportFolder)
	{
		try
		{
			//ImageIO.write(image, "jpg", newFile);
			if (!jpegMode)
			{
				File newFile = new File(exportFolder.getAbsolutePath());
				exportToPNG(image, newFile);
			}
			else
			{
				File newFile = new File(exportFolder.getAbsolutePath());
				exportToJPEG(image, newFile);
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	private String getExportName(Card card, File exportFolder)
	{
		if (card != null)
		{
			return card.getCardName(exportFolder.getAbsolutePath());
		}
		else if (ProjectHelper.getCurrentFile() != null)
		{
			int i = 1;
			String filename = ProjectHelper.getCurrentFile().getName().replace(" ", "") + "_" + i;
			
			do
			{
				filename = ProjectHelper.getCurrentFile().getName().replace(" ", "") + "_" + i;
				i++;
			}
			while (new File(exportFolder.getAbsolutePath() + File.separator + filename + ".jpg").exists() 
					|| new File(exportFolder.getAbsolutePath() + File.separator + filename + ".png").exists());
			
			return filename;
		}
		else
		{
			int i = 1;
			String filename = "Untitled_" + i;
			
			do
			{
				filename = "Untitled_" + i;
				i++;
			}
			while (new File(exportFolder.getAbsolutePath() + File.separator + filename + ".jpg").exists() 
					|| new File(exportFolder.getAbsolutePath() + File.separator + filename + ".png").exists());
			
			return filename;
		}
		
	}
	
	public void exportToJPEG(BufferedImage image, File newFile) throws Exception
	{
		System.out.println("Exporting: " + newFile.getName());
		
		BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		g.dispose();
		
		newFile.mkdirs();
		
		ImageIO.write(bi, "jpeg", newFile);
	}
	
	public void exportToPNG(BufferedImage image, File newFile) throws Exception
	{
		System.out.println("Exporting: " + newFile.getName());
		ImageIO.write(image, "png", newFile);
	}

}
