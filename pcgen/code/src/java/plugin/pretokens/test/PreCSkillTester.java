/*
 * PreCSkill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2005 (C) Thomas Clegg <TN_Clegg@lycos.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 */package plugin.pretokens.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author arknight
 *
 */
public class PreCSkillTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{
	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final int reqnumber = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;
		HashMap<Skill,HashSet<Skill>> serveAsSkills = new HashMap<Skill, HashSet<Skill>>();
		Set<Skill> imitators = new HashSet<Skill>();
		this.getImitators(serveAsSkills, imitators, character);

		// Compute the skill name from the Prerequisite
		String requiredSkillKey = prereq.getKey().toUpperCase();

		if (prereq.getSubKey() != null)
		{
			requiredSkillKey += " (" + prereq.getSubKey().toUpperCase() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		final boolean isType =
				(requiredSkillKey.startsWith("TYPE.") || requiredSkillKey.startsWith("TYPE=")); //$NON-NLS-1$ //$NON-NLS-2$
		if (isType)
		{
			requiredSkillKey = requiredSkillKey.substring(5);
		}
		final String skillKey = requiredSkillKey;
		Set<Skill> skillMatches = new HashSet<Skill>();

		if (isType)
		{
			//Skill name is actually type to compare for

			//loop through skill list checking for type and class skill
			for (Skill skill : Globals.getSkillList())
			{
				if (skill.isType(skillKey) && skill.isClassSkill(character))
				{
					skillMatches.add(skill);
					runningTotal++;
				}
				
			}
			if (runningTotal < reqnumber ) 
			{
BREAKOUT:		for(Skill fake: serveAsSkills.keySet())
				{
					if (fake.isClassSkill(character))
					{
						for(Skill mock: serveAsSkills.get(fake))
						{
							if (skillMatches.contains(mock))
							{
								// We already counted this skill in the above 
								// calculation.  We DONT want to match it 
								// a second time
								break BREAKOUT; 
							}
							if (mock.isType(skillKey))
							{
								runningTotal++;
								break BREAKOUT;
							}
						}
					}
				}
			}
		}
		else
		{
			Skill skill = Globals.getSkillKeyed(skillKey);
			if (skill != null && skill.isClassSkill(character))
			{
				runningTotal++;
			}
			else 
			{
				for(Skill mock: imitators)
				{
					if (mock.isClassSkill(character) && serveAsSkills.get(mock).contains(skill))
					{
						runningTotal++;
						break;
					}
				}
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, reqnumber);
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * @param serveAsSkills
	 * @param imitators
	 * @param character
	 */
	private void getImitators(
		HashMap<Skill, HashSet<Skill>> serveAsSkills, Set<Skill> imitators,
		PlayerCharacter character)
	{
		List<Skill> allSkills = Globals.getSkillList();		
		for(Skill aSkill: allSkills)
		{
			Skill finalSkill = null ;
			Set<Skill> servesAs = new HashSet<Skill>();
			for(String fakeSkill: aSkill.getServesAs(""))
			{
				finalSkill = Globals.getSkillKeyed(fakeSkill);
				servesAs.add(finalSkill);
			}
			
			if(servesAs.size() > 0)
			{
				imitators.add(aSkill);
				serveAsSkills.put(aSkill, (HashSet<Skill>) servesAs);
			}
		}		
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "CSKILL"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String skillName = prereq.getKey();
		if (prereq.getSubKey() != null && !prereq.getSubKey().equals("")) //$NON-NLS-1$
		{
			skillName += " (" + prereq.getSubKey() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

		}

		String foo = "";
		if (prereq.getOperand().equals("1") && prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
		{
			foo = PropertyFactory.getFormattedString("PreCSkill.single.toHtml", //$NON-NLS-1$
					new Object[]{skillName});
		}
		else
		{
			foo = PropertyFactory.getFormattedString("PreCSkill.toHtml", //$NON-NLS-1$
					new Object[]{prereq.getOperator().toDisplayString(),
						prereq.getOperand(), skillName});
		}
		return foo;
	}
}
