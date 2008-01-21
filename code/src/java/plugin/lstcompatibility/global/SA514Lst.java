package plugin.lstcompatibility.global;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.PCClass;
import pcgen.core.SpecialAbility;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstCompatibilityToken;
import pcgen.util.Logging;

public class SA514Lst extends AbstractToken implements GlobalLstCompatibilityToken
{

	private static final Class<SpecialAbility> SA_CLASS = SpecialAbility.class;

	@Override
	public String getTokenName()
	{
		return "SA";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof PCClass)
		{
			return false;
		}
		return parseSpecialAbility(context, obj, value);
	}

	/**
	 * This method sets the special abilities granted by this [object]. For
	 * efficiency, avoid calling this method except from I/O routines.
	 * 
	 * @param obj
	 *            the PObject that is to receive the new SpecialAbility
	 * @param aString
	 *            String of special abilities delimited by pipes
	 * @param level
	 *            int level at which the ability is gained
	 */
	public boolean parseSpecialAbility(LoadContext context, CDOMObject obj,
		String aString)
	{
		if (isEmpty(aString) || hasIllegalSeparator('|', aString))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(aString, Constants.PIPE);

		String firstToken = tok.nextToken();
		if (firstToken.startsWith("PRE") || firstToken.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
				+ getTokenName());
			return false;
		}

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			context.getGraphContext().removeAll(getTokenName(), obj);
			if (!tok.hasMoreTokens())
			{
				return true;
			}
			firstToken = tok.nextToken();
		}

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			Logging.errorPrint("SA tag confused by redundant '.CLEAR'"
				+ aString);
			return false;
		}

		SpecialAbility sa = new SpecialAbility(firstToken);

		if (!tok.hasMoreTokens())
		{
			context.getGraphContext().grant(getTokenName(), obj, sa);
			return true;
		}

		StringBuilder saName = new StringBuilder();
		saName.append(firstToken);

		String token = tok.nextToken();
		while (true)
		{
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				Logging.errorPrint("SA tag confused by '.CLEAR' as a "
					+ "middle token: " + aString);
				return false;
			}
			else if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
			else
			{
				saName.append(Constants.PIPE).append(token);
				// sa.addVariable(FormulaFactory.getFormulaFor(token));
			}

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				// CONSIDER This is a HACK and not the long term strategy of SA:
				sa.setName(saName.toString());
				context.getGraphContext().grant(getTokenName(), obj, sa);
				return true;
			}
			token = tok.nextToken();
		}
		// CONSIDER This is a HACK and not the long term strategy of SA:
		sa.setName(saName.toString());

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put Abilities after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			/*
			 * The following subkey is required in order to give context to the
			 * variables as they are calculated (make the context the current
			 * class, so that items like Class Level can be correctly
			 * calculated).
			 */
			if (obj instanceof PCClass && "var".equals(prereq.getKind()))
			{
				prereq.setSubKey("CLASS:" + obj.getKeyName());
			}
			sa.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}
		context.getGraphContext().grant(getTokenName(), obj, sa);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<SpecialAbility> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, SA_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		List<String> list = new ArrayList<String>(added.size() + 1);
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (added.isEmpty())
		{
			// Zero indicates no Token (and no global clear, so nothing to do)
			return null;
		}
		for (LSTWriteable lw : added)
		{
			StringBuilder sb = new StringBuilder();
			SpecialAbility ab = (SpecialAbility) lw;
			sb.append(ab.getDisplayName());
			if (ab.hasPrerequisites())
			{
				sb.append(Constants.PIPE);
				sb.append(getPrerequisiteString(context, ab
					.getPrerequisiteList()));
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}
}
