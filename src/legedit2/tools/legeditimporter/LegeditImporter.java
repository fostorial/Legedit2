package legedit2.tools.legeditimporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import legedit2.card.Card;
import legedit2.cardtype.CardType;
import legedit2.cardtype.CustomElement;
import legedit2.cardtype.ElementCardName;
import legedit2.cardtype.Style;
import legedit2.deck.Deck;
import legedit2.decktype.DeckType;
import legedit2.gui.LegeditFrame;
import legedit2.helpers.LegeditHelper;
import legedit2.helpers.ProjectHelper;

public class LegeditImporter {

	public LegeditImporter(File file)
	{
		ProjectHelper.newProject();
		
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
			String line;
			Deck deck = new Deck();
			Card card = new Card();
			while ((line = br.readLine()) != null) {
			   if (line != null && !line.startsWith("#") && !line.isEmpty())
			   {
				   
				   if (line.startsWith("VILLAIN;")
						   && (!line.replace("VILLAIN;", "").equals("system_wound_villain")
								   && !line.replace("VILLAIN;", "").equals("system_wound_villain")
								   && !line.replace("VILLAIN;", "").equals("system_bindings_villain")
								   && !line.replace("VILLAIN;", "").equals("system_bystander_villain")))
				   {   
					  for (DeckType dt : DeckType.getDeckTypes())
					  {
						  if (dt.getName().equalsIgnoreCase("villain"))
						  {
							  deck = new Deck();
							  deck.setName(line.replace("VILLAIN;", ""));
							  deck.setTemplate(dt.getCopy());
							  deck.setTemplateName(dt.getName());
							  deck.getCards().clear();
							  ProjectHelper.addLegeditItem(deck);
						  }
					  }
				   }
				   
				   if (line.startsWith("VILLAINCARD;"))
				   {
					   
					   if (deck != null && deck.getTemplate() != null)
					   {
						   for (CardType ct : deck.getTemplate().getCardTypes()) {
							   if (ct.getName().equalsIgnoreCase("villain"))
							   {
								   card = new Card();
								   card.setTemplate(ct.getCopy());
								   card.setTemplateName(ct.getName());
								   deck.getCards().add(card);
							   }
						   }
					   }
				   }
				   
				   if (line.startsWith("VCNAME;"))
				   {
					   setValue(card, ElementCardName.class, "Card Name", line.replace("VCNAME;", ""));
				   }
				   
				   if (line.startsWith("VCGENERATE;"))
				   {
					   
				   }
			   }
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(LegeditFrame.legedit, "No icon selected", e.getMessage() != null ? e.getMessage() : LegeditHelper.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
		}
		finally {
			if (br != null)
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		LegeditFrame.refreshGUI();
	}
	
	private void setValue(Card card, Class clazz, String name, Object value)
	{
		if (card != null && card.getTemplate() != null)
		{
			for (CustomElement e : card.getTemplate().elements)
			{
				if (e.getClass().getSimpleName().equalsIgnoreCase(clazz.getSimpleName())
						&& e.name.equalsIgnoreCase(name))
				{
					if (e instanceof ElementCardName)
					{
						((ElementCardName)e).value = (String)value;
					}
				}
			}
			
			for (Style style : card.getTemplate().getStyles()) {
				for (CustomElement e : style.getElements())
				{
					if (e.getClass().getSimpleName().equalsIgnoreCase(clazz.getSimpleName())
							&& e.name.equalsIgnoreCase(name))
					{
						if (e instanceof ElementCardName)
						{
							((ElementCardName)e).value = (String)value;
						}
					}
				}
			}
		}
	}
}
