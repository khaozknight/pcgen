/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.util.ArrayList;
import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.DataSetInitializedFacet;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.helper.AvailableSpell;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;

/**
 * The Class <code>MasterSkillFacet</code> caches a copy of all class skill
 * lists. This allows faster checking of whether skills are class skills for a
 * character class. Note this is a "global" facet in that it does not have
 * method that depend on CharID (they are not character specific).
 * 
 * @author Tom Parker <thpr@users.sourceforge.net>
 */
public class MasterAvailableSpellFacet extends
		AbstractListFacet<DataSetID, AvailableSpell> implements
		DataSetInitializedFacet
{
	private DataSetInitializationFacet datasetInitializationFacet;

	/**
	 * Initializes the global lists of ClassSkillLists. This method only needs
	 * to be called once for each set of sources that are loaded.
	 */
	public synchronized void initialize(LoadContext lc)
	{
		DataSetID dsID = lc.getDataSetID();
		MasterListInterface masterLists = Globals.getMasterLists();
		ArrayList<CDOMReference<CDOMList<Spell>>> useLists =
				new ArrayList<CDOMReference<CDOMList<Spell>>>();
		for (CDOMReference ref : masterLists.getActiveLists())
		{
			Collection<CDOMList<Spell>> lists = ref.getContainedObjects();
			for (CDOMList<Spell> list : lists)
			{
				if ((list instanceof ClassSpellList)
					|| (list instanceof DomainSpellList))
				{
					useLists.add(ref);
					break;
				}
			}
		}
		for (CDOMReference<CDOMList<Spell>> ref : useLists)
		{
			for (Spell spell : masterLists.getObjects(ref))
			{
				Collection<AssociatedPrereqObject> assoc =
						masterLists.getAssociations(ref, spell);
				for (AssociatedPrereqObject apo : assoc)
				{
					int lvl = apo.getAssociation(AssociationKey.SPELL_LEVEL);
					for (CDOMList<Spell> list : ref.getContainedObjects())
					{
						AvailableSpell as = new AvailableSpell(list, spell, lvl);
						if (apo.hasPrerequisites())
						{
							as.addAllPrerequisites(apo.getPrerequisiteList());
						}
						add(dsID, as);
					}
				}
			}
		}
	}

	public void setDataSetInitializationFacet(
		DataSetInitializationFacet datasetInitializationFacet)
	{
		this.datasetInitializationFacet = datasetInitializationFacet;
	}

	public void init()
	{
		datasetInitializationFacet.addDataSetInitializedFacet(this);
	}
}
