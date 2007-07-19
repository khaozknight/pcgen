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
package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.Logging;

/**
 * Deals with ALTCRITRANGE token
 */
public class AltcritrangeToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "ALTCRITRANGE";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setAltCritRange(value);
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer cr = Integer.valueOf(value);
			if (cr.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " cannot be < 0");
				return false;
			}
			context.obj.put(context.graph.getEquipmentHead(eq, 2),
				IntegerKey.CRIT_RANGE, cr);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName() + " expected an integer.  "
				+ "Tag must be of the form: " + getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = context.graph.getEquipmentHeadReference(eq, 2);
		if (head == null)
		{
			return null;
		}
		Integer mult = context.obj.getInteger(head, IntegerKey.CRIT_RANGE);
		if (mult == null)
		{
			return null;
		}
		return new String[]{mult.toString()};
	}
}
