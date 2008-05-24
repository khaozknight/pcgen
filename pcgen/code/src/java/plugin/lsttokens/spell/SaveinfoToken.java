package plugin.lsttokens.spell;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SAVEINFO Token
 */
public class SaveinfoToken extends AbstractToken implements
		CDOMPrimaryToken<Spell>
{

	@Override
	public String getTokenName()
	{
		return "SAVEINFO";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;
		while (aTok.hasMoreTokens())
		{
			String tok = aTok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tok))
			{
				if (!first)
				{
					Logging.errorPrint("Non-sensical use of .CLEAR in "
							+ getTokenName() + ": " + value);
					return false;
				}
				context.getObjectContext().removeList(spell, ListKey.SAVE_INFO);
			}
			else
			{
				context.getObjectContext().addToList(spell, ListKey.SAVE_INFO,
						tok);
				Globals.addSpellSaveInfoSet(tok);
			}
			first = false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		Changes<String> changes = context.getObjectContext().getListChanges(
				spell, ListKey.SAVE_INFO);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Collection<?> added = changes.getAdded();
		boolean globalClear = changes.includesGlobalClear();
		if (globalClear)
		{
			sb.append(Constants.LST_DOT_CLEAR);
		}
		if (added != null && !added.isEmpty())
		{
			if (globalClear)
			{
				sb.append(Constants.PIPE);
			}
			sb.append(StringUtil.join(added, Constants.PIPE));
		}
		if (sb.length() == 0)
		{
			context.addWriteMessage(getTokenName()
					+ " was expecting non-empty changes to include "
					+ "added items or global clear");
			return null;
		}
		return new String[] { sb.toString() };
	}

	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
