/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.race;

import java.util.Set;

import pcgen.base.formula.AddingFormula;
import pcgen.base.formula.DividingFormula;
import pcgen.base.formula.MultiplyingFormula;
import pcgen.base.formula.SubtractingFormula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.Aggregator;
import pcgen.cdom.modifier.AbstractHitDieModifier;
import pcgen.cdom.modifier.HitDieFormula;
import pcgen.cdom.modifier.HitDieLock;
import pcgen.cdom.modifier.HitDieStep;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.util.Logging;

/**
 * Class deals with HITDIE Token
 */
public class HitdieToken extends AbstractToken implements RaceLstToken
{

	@Override
	public String getTokenName()
	{
		return "HITDIE";
	}

	public boolean parse(Race race, String value)
	{
		race.setHitDieLock(value);
		return true;
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		try
		{
			String lock = value;
			int pipeLoc = lock.indexOf(Constants.PIPE);
			if (pipeLoc != lock.lastIndexOf(Constants.PIPE))
			{
				Logging.errorPrint(getTokenName() + " has more than one pipe, "
					+ "is not of format: <int>[|<prereq>]");
				return false;
			}
			CDOMReference<PCClass> owner;
			if (pipeLoc != -1)
			{
				// Has a limitation
				String lockPre = lock.substring(pipeLoc + 1);
				if (lockPre.startsWith("CLASS.TYPE="))
				{
					String substring = lock.substring(pipeLoc + 12);
					if (substring.length() == 0)
					{
						Logging
							.errorPrint("Cannot have Empty Type Limitation in "
								+ getTokenName() + ": " + value);
						return false;
					}
					owner =
							context.ref.getCDOMTypeReference(PCClass.class,
								substring.split("\\."));
				}
				else if (lockPre.startsWith("CLASS="))
				{
					String substring = lock.substring(pipeLoc + 7);
					if (substring.length() == 0)
					{
						Logging
							.errorPrint("Cannot have Empty Class Limitation in "
								+ getTokenName() + ": " + value);
						return false;
					}
					owner =
							context.ref.getCDOMReference(PCClass.class,
								substring);
				}
				else
				{
					Logging.errorPrint("Invalid Limitation in HITDIE: "
						+ lockPre);
					return false;
				}
				lock = lock.substring(0, pipeLoc);
			}
			else
			{
				// Unlimited
				owner = context.ref.getCDOMAllReference(PCClass.class);
			}

			AbstractHitDieModifier hdm;
			if (lock.startsWith("%/"))
			{
				// HITDIE:%/num --- divides the classes hit die by num.
				int denom = Integer.parseInt(lock.substring(2));
				if (denom <= 0)
				{
					Logging.errorPrint(getTokenName()
						+ " was expecting a Positive Integer "
						+ "for dividing Lock, was : " + lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new DividingFormula(denom));
			}
			else if (lock.startsWith("%*"))
			{
				// HITDIE:%*num --- multiplies the classes hit die by num.
				int mult = Integer.parseInt(lock.substring(2));
				if (mult <= 0)
				{
					Logging.errorPrint(getTokenName()
						+ " was expecting a Positive "
						+ "Integer for multiplying Lock, was : "
						+ lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new MultiplyingFormula(mult));
			}
			else if (lock.startsWith("%+"))
			{
				// possibly redundant with BONUS:HD MAX|num
				// HITDIE:%+num --- adds num to the classes hit die.
				int add = Integer.parseInt(lock.substring(2));
				if (add <= 0)
				{
					Logging.errorPrint(getTokenName()
						+ " was expecting a Positive "
						+ "Integer for multiplying Lock, was : "
						+ lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new AddingFormula(add));
			}
			else if (lock.startsWith("%-"))
			{
				// HITDIE:%-num --- subtracts num from the classes hit die.
				// possibly redundant with BONUS:HD MAX|num if that will
				// take negative numbers.
				int sub = Integer.parseInt(lock.substring(2));
				if (sub <= 0)
				{
					Logging.errorPrint(getTokenName()
						+ " was expecting a Positive "
						+ "Integer for multiplying Lock, was : "
						+ lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new SubtractingFormula(sub));
			}
			else if (lock.startsWith("%up"))
			{
				// HITDIE:%upnum --- moves the hit die num steps up the die size
				// list d4,d6,d8,d10,d12. Stops at d12.

				int steps = Integer.parseInt(lock.substring(3));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName());
					return false;
				}
				// TODO Check steps??

				// FIXME This is passing null, but really needs to be limited!!!
				hdm = new HitDieStep(steps, null);
			}
			else if (lock.startsWith("%Hup"))
			{
				// HITDIE:%upnum --- moves the hit die num steps up the die size
				// list d4,d6,d8,d10,d12. Stops at d12.

				int steps = Integer.parseInt(lock.substring(4));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName());
					return false;
				}
				hdm = new HitDieStep(steps, null);
			}
			else if (lock.startsWith("%down"))
			{
				// HITDIE:%downnum --- moves the hit die num steps down the die
				// size
				// list d4,d6,d8,d10,d12. Stops at d4.

				int steps = Integer.parseInt(lock.substring(5));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName());
					return false;
				}
				// TODO Check steps??

				// FIXME This is passing null, but really needs to be limited!!!
				hdm = new HitDieStep(-steps, null);
			}
			else if (lock.startsWith("%Hdown"))
			{
				// HITDIE:%downnum --- moves the hit die num steps down the die
				// size
				// list. No limit.
				int steps = Integer.parseInt(lock.substring(6));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
						+ getTokenName());
					return false;
				}
				hdm = new HitDieStep(-steps, null);
			}
			else
			{
				int i = Integer.parseInt(lock);
				if (i <= 0)
				{
					Logging.errorPrint("Invalid HitDie: " + i + " in "
						+ getTokenName());
					return false;
				}
				// HITDIE:num --- sets the hit die to num regardless of class.
				hdm = new HitDieLock(new HitDie(i));
			}

			Aggregator ag = new Aggregator(race, owner, getTokenName());
			context.graph.linkAllowIntoGraph(getTokenName(), race, ag);
			context.graph.linkActivationIntoGraph(getTokenName(), owner, ag);
			context.graph.linkObjectIntoGraph(getTokenName(), ag, hdm);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
				+ nfe.getLocalizedMessage());
			Logging.errorPrint("  Must be an Integer");
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), race,
					Aggregator.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		if (edges.size() != 1)
		{
			context
				.addWriteMessage("Only 1 Hit Die Lock is allowed per Template");
			return null;
		}
		// CONSIDER Verify this is an allow edge?
		Aggregator ag =
				(Aggregator) edges.iterator().next().getSinkNodes().get(0);
		Set<PCGraphEdge> aggEdges =
				context.graph.getChildLinksFromToken(getTokenName(), ag,
					AbstractHitDieModifier.class);
		if (aggEdges == null || aggEdges.isEmpty())
		{
			context
				.addWriteMessage("Found Aggregator, but no children for token "
					+ getTokenName() + ".  Error in Graph Structure.");
			return null;
		}
		if (aggEdges.size() != 1)
		{
			context
				.addWriteMessage("Only 1 Hit Die Lock is allowed per Aggregator");
			return null;
		}
		// CONSIDER Verify this is a grant edge?
		AbstractHitDieModifier hdm =
				(AbstractHitDieModifier) aggEdges.iterator().next()
					.getSinkNodes().get(0);
		StringBuilder sb = new StringBuilder();
		sb.append(hdm.getLSTform());

		Set<PCGraphEdge> aggParents =
				context.graph.getParentLinksFromToken(getTokenName(), ag,
					PCClass.class);
		if (aggParents == null || aggParents.isEmpty())
		{
			context
				.addWriteMessage("Found Aggregator, but no activating parent for token "
					+ getTokenName() + ".  Error in Graph Structure.");
			return null;
		}
		if (aggParents.size() != 1)
		{
			context
				.addWriteMessage("Only 1 Activation source is allowed per Aggregator");
			return null;
		}
		// CONSIDER Verify this is an activation edge?
		CDOMReference<PCClass> ref =
				(CDOMReference<PCClass>) aggParents.iterator().next()
					.getNodeAt(0);
		if (!ref.equals(context.ref.getCDOMAllReference(PCClass.class)))
		{
			String lstFormat = ref.getLSTformat();
			sb.append("|CLASS");
			sb.append(lstFormat.indexOf('=') == -1 ? '=' : '.');
			sb.append(lstFormat);
		}

		return new String[]{sb.toString()};
	}
}
