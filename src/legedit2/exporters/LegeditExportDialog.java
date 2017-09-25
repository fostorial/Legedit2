package legedit2.exporters;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import legedit2.card.Card;
import legedit2.gui.LegeditFrame;
import legedit2.helpers.LegeditHelper;

public class LegeditExportDialog extends JDialog implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = -1844700819488507706L;
	
	private LegeditExporter exporter;
	private JProgressBar progressBar;
	
	final static int OK_OPTION = JOptionPane.OK_OPTION;
	final static int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
	
	private int outcome = CANCEL_OPTION;
	
	public LegeditExportDialog(LegeditExporter exporter) {
        super(LegeditFrame.legedit, true);
        
        setTitle("Exporting...");
        setSize(300, 50);
		
        setExporter(exporter);
 
        setProgressBar(new JProgressBar(0, exporter.getExportCount()));
        getProgressBar().setValue(0);
        getProgressBar().setStringPainted(true);
 
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(getProgressBar(), BorderLayout.CENTER);
 
        add(panel, BorderLayout.PAGE_START);
    }
	
	public void export()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showSaveDialog(LegeditFrame.legedit);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			if (!chooser.getSelectedFile().isDirectory())
			{
				JOptionPane.showMessageDialog(LegeditFrame.legedit, "Not a Directory!", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				this.setVisible(false);
				return;
			}
			
			exporter.setDialog(this);
			exporter.export(chooser.getSelectedFile());
			
			this.setVisible(true);
		}
		else
		{
			return;
		}
	}
	
	public void exportSingle()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = chooser.showSaveDialog(LegeditFrame.legedit);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			if (chooser.getSelectedFile().isDirectory())
			{
				JOptionPane.showMessageDialog(LegeditFrame.legedit, "Not a File!", LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
				this.setVisible(false);
				return;
			}
			
			exporter.setDialog(this);
			exporter.export(chooser.getSelectedFile());
			
			this.setVisible(true);
		}
		else
		{
			return;
		}
	}
	
	public static void export(LegeditExporter exporter)
	{
		LegeditExportDialog d = new LegeditExportDialog(exporter);
		d.export();
	}
	
	public static void exportSingleImage(Card card)
	{
		LegeditExportDialog d = new LegeditExportDialog(new LegeditExporterSingleImage(card));
		d.exportSingle();
	}
	
	public static void exportSingleImageWithBleed(Card card)
	{
		LegeditExportDialog d = new LegeditExportDialog(new LegeditExporterSingleImageBleeder(card));
		d.exportSingle();
	}
	
	public static void exportSingleImages()
	{
		LegeditExportDialog d = new LegeditExportDialog(new LegeditExporterSingleImages());
		d.export();
	}
	
	public static void exportSingleImagesWithBleed()
	{
		LegeditExportDialog d = new LegeditExportDialog(new LegeditExporterSingleImagesBleeder());
		d.export();
	}
	
	public static void export3By3Pages()
	{
		LegeditExportDialog d = new LegeditExportDialog(new LegeditExporter3by3());
		d.export();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public LegeditExporter getExporter() {
		return exporter;
	}

	public void setExporter(LegeditExporter exporter) {
		this.exporter = exporter;
	}

	public int getOutcome() {
		return outcome;
	}

	public void setOutcome(int outcome) {
		this.outcome = outcome;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

}
