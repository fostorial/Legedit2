package legedit2.cardtype;

import java.util.ArrayList;
import java.util.List;

public class WordDefinition {

	public String word;
	public boolean space = true;
	
	public WordDefinition(String word, boolean space) {
		super();
		this.word = word;
		this.space = space;
	}
	
	public static List<WordDefinition> getWordDefinitionList(String text)
	{
		List<WordDefinition> retVal = new ArrayList<WordDefinition>();
		
		String[] words = text.split(" ");
		
		for (String s : words)
		{
			int breakCount = 0;
			for (char c : s.toCharArray())
			{
				if (c == '<')
				{
					breakCount++;
				}
			}
			
			if (breakCount >= 1)
			{
				String[] split = s.split("<");
				if (split[0] != null && !split[0].isEmpty())
				{
					if (split[0].contains("<") )
					{
						
						if (split[0].contains(">") && split[0].split(">").length > 1 && split[0].split(">")[1].length() > 0)
						{
							String[] split2 = split[0].split(">");
							//System.out.println(split2[0] + ":" + split2[1]);
							retVal.add(new WordDefinition("<" + split2[0] + ">", false));
							retVal.add(new WordDefinition(split2[1], false));
						}
						else
						{
							retVal.add(new WordDefinition("<" + split[0], false));
						}
						
					}
					else
					{
						retVal.add(new WordDefinition(split[0], false));
					}
				}
				for (int i = 1; i < split.length - 1; i++)
				{
					if (split[i].contains(">") && split[i].split(">").length > 1 && split[i].split(">")[1].length() > 0)
					{
						String[] split2 = split[i].split(">");
						//System.out.println(split2[0] + ":" + split2[1]);
						retVal.add(new WordDefinition("<" + split2[0] + ">", false));
						retVal.add(new WordDefinition(split2[1], false));
					}
					else
					{
						retVal.add(new WordDefinition("<" + split[i], false));
					}
				}
				if (split[split.length - 1].contains(">") && split[split.length - 1].split(">").length > 1 && split[split.length - 1].split(">")[1].length() > 0)
				{
					String[] split2 = split[split.length - 1].split(">");
					retVal.add(new WordDefinition("<" + split2[0] + ">", false));
					retVal.add(new WordDefinition(split2[1], true));
				}
				else
				{
					retVal.add(new WordDefinition("<" + split[split.length - 1], true));
				}
			}
			else
			{
				retVal.add(new WordDefinition(s, true));
			}
		}
		
		return retVal;
	}
}
