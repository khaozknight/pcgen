package plugin.lsttokens.add;

import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphAllowsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.Aggregator;
import pcgen.core.PObject;
import pcgen.core.SpellList;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.util.Logging;

public class SpellLevelToken implements AddLstToken
{

	private static final Class<Spell> SPELL_CLASS = Spell.class;

	public boolean parse(PObject target, String value, int level)
	{
		target.addAddList(level, getTokenName() + ":" + value);
		return true;
	}

	public String getTokenName()
	{
		return "SPELLLEVEL";
	}

	public boolean parse(LoadContext context, PObject obj, String value)
		throws PersistenceLayerException
	{
		if (!value.startsWith("CLASS|SPELLCASTER."))
		{
			Logging.errorPrint(getTokenName()
				+ " syntax starts with CLASS|SPELLCASTER.");
			return false;
		}
		String classSpell = value.substring(18);
		int pipeLoc = classSpell.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " has too few | present");
			return false;
		}
		if (pipeLoc != classSpell.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint(getTokenName() + " has too many | present");
			return false;
		}
		String classLevel = classSpell.substring(0, pipeLoc);
		int equalsLoc = classLevel.indexOf(Constants.EQUALS);
		if (equalsLoc == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " does not follow SpellList=Level syntax");
			return false;
		}
		if (equalsLoc != classLevel.lastIndexOf(Constants.EQUALS))
		{
			Logging.errorPrint(getTokenName() + " has too many = present");
			return false;
		}

		CDOMReference<SpellList> spelllist =
				context.ref.getCDOMReference(SpellList.class, classLevel
					.substring(0, equalsLoc));
		Integer level;
		try
		{
			level = Integer.valueOf(classLevel.substring(equalsLoc + 1));
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " encountered a level which is not an integer");
			return false;
		}
		if (level.intValue() < 0)
		{
			Logging.errorPrint(getTokenName()
				+ " encountered a level which is negative");
			return false;
		}

		/*
		 * BUG TODO I suspect this is actually broken, as ADD produces a SLOT,
		 * and this is not currently doing that.
		 */
		String spell = classSpell.substring(pipeLoc + 1);
		if (spell.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " has no spell present");
			return false;
		}
		CDOMReference<Spell> sp =
				context.ref.getCDOMReference(SPELL_CLASS, spell);

		Aggregator agg = new Aggregator(obj, spelllist, getTokenName());
		/*
		 * This is intentionally Holds, as the context for traversal must only
		 * be the ref (linked by the Activation Edge). So we need an edge to the
		 * Activator to get it copied into the PC, but since this is a 3rd party
		 * Token, the granting object should never grant anything hung off the
		 * aggregator.
		 */
		context.graph.linkHoldsIntoGraph(getTokenName(), obj, agg);
		context.graph.linkActivationIntoGraph(getTokenName(), spelllist, agg);

		PCGraphAllowsEdge edge =
				context.graph.linkAllowIntoGraph(getTokenName(), agg, sp);
		edge.setAssociation(AssociationKey.SPELL_LEVEL, level);
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					Aggregator.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (PCGraphEdge edge : edgeList)
		{
			if (!processEdge(set, context, edge))
			{
				return null;
			}
		}
		return set.toArray(new String[set.size()]);
	}

	private boolean processEdge(Set<String> set, LoadContext context,
		PCGraphEdge edge)
	{
		Aggregator a = (Aggregator) edge.getNodeAt(1);
		Set<PCGraphEdge> edgeFromSourceList =
				context.graph.getParentLinksFromToken(getTokenName(), a,
					SpellList.class);
		if (edgeFromSourceList.size() != 1)
		{
			context
				.addWriteMessage("Spell must have one source SpellList for Token "
					+ getTokenName());
			return false;
		}
		CDOMReference<SpellList> sourceList =
				(CDOMReference<SpellList>) edgeFromSourceList.iterator().next()
					.getNodeAt(0);

		Set<PCGraphEdge> edgeToSpellList =
				context.graph.getChildLinksFromToken(getTokenName(), a,
					SPELL_CLASS);
		for (PCGraphEdge se : edgeToSpellList)
		{
			Integer level = se.getAssociation(AssociationKey.SPELL_LEVEL);
			if (level == null)
			{
				context.addWriteMessage("Spell must have level for Token "
					+ getTokenName());
				return false;
			}
			if (level.intValue() < 0)
			{
				context
					.addWriteMessage("Spell must have positive integer level for Token "
						+ getTokenName());
				return false;
			}
			CDOMReference<Spell> spell = (CDOMReference<Spell>) se.getNodeAt(1);
			String string =
					new StringBuilder().append("CLASS|SPELLCASTER.").append(
						sourceList.getLSTformat()).append('=').append(level)
						.append('|').append(spell.getLSTformat()).toString();
			set.add(string);
		}
		return true;
	}
}
