/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.choice;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SpellSchool;
import pcgen.core.PlayerCharacter;

public class SpellSchoolChooser extends AbstractChooser<SpellSchool>
{

	public SpellSchoolChooser()
	{
		super();
	}

	public Set<SpellSchool> getSet(PlayerCharacter pc)
	{
		return new HashSet<SpellSchool>(SpellSchool.getAllConstants());
	}

	@Override
	public String toString()
	{
		return getCount().toString() + '<' + getMaxSelections().toString()
			+ Constants.PIPE + "SpellSchools";
	}

	@Override
	public int hashCode()
	{
		return chooserHashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof SpellSchoolChooser))
		{
			return false;
		}
		return o == this || equalsAbstractChooser((SpellSchoolChooser) o);
	}
}
