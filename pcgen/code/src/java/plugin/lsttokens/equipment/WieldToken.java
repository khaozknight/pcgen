/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.character.WieldCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with WIELD token
 */
public class WieldToken implements CDOMPrimaryToken<Equipment>
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "WIELD";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			WieldCategory wc = WieldCategory.findByName(value);
			if (wc.equals(WieldCategory.DEFAULT_UNUSABLE))
			{
				Logging.log(Logging.LST_ERROR, "In " + getTokenName()
						+ " unable to find WieldCategory for " + value);
				return false;
			}
			context.getObjectContext().put(eq, ObjectKey.WIELD, wc);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.log(Logging.LST_ERROR, "Invalid Wield provided in " + getTokenName()
					+ ": " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		WieldCategory w = context.getObjectContext().getObject(eq,
				ObjectKey.WIELD);
		if (w == null)
		{
			return null;
		}
		return new String[] { w.getName() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
