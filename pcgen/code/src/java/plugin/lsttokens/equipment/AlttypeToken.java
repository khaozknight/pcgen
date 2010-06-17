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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ALTTYPE token
 */
public class AlttypeToken extends AbstractToken implements
		CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "ALTTYPE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		EquipmentHead head = eq.getEquipmentHead(2);
		if (value.startsWith(".CLEAR"))
		{
			context.getObjectContext().removeList(head, ListKey.TYPE);
			if (value.length() == 6)
			{
				return true;
			}
			else if (value.charAt(6) == '.')
			{
				value = value.substring(7);
				if (isEmpty(value))
				{
					Logging
							.errorPrint(getTokenName()
									+ "started with .CLEAR. but expected to have a Type after .: "
									+ value);
					return false;
				}
			}
			else
			{
				Logging
						.errorPrint(getTokenName()
								+ "started with .CLEAR but expected next character to be .: "
								+ value);
				return false;
			}
		}
		if (hasIllegalSeparator('.', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.DOT);

		boolean bRemove = false;
		boolean bAdd = false;
		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();
			if ("ADD".equals(aType))
			{
				if (bRemove)
				{
					Logging.log(Logging.LST_ERROR,
							"Non-sensical use of .REMOVE.ADD. in "
									+ getTokenName() + ": " + value);
					return false;
				}
				bRemove = false;
				bAdd = true;
			}
			else if ("REMOVE".equals(aType))
			{
				if (bAdd)
				{
					Logging.log(Logging.LST_ERROR,
							"Non-sensical use of .ADD.REMOVE. in "
									+ getTokenName() + ": " + value);
					return false;
				}
				bRemove = true;
			}
			else if ("CLEAR".equals(aType))
			{
				Logging.log(Logging.LST_ERROR, "Non-sensical use of .CLEAR in "
						+ getTokenName() + ": " + value);
				return false;
			}
			else if (bRemove)
			{
				Type type = Type.getConstant(aType);
				context.getObjectContext().removeFromList(head, ListKey.TYPE,
						type);
				bRemove = false;
			}
			else
			{
				Type type = Type.getConstant(aType);
				context.getObjectContext().addToList(head, ListKey.TYPE, type);
				bAdd = false;
			}
		}
		if (bRemove)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ "ended with REMOVE, so didn't have any Type to remove: "
					+ value);
			return false;
		}
		if (bAdd)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ "ended with ADD, so didn't have any Type to add: "
					+ value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = eq.getEquipmentHead(2);
		Changes<Type> changes = context.getObjectContext().getListChanges(head,
				ListKey.TYPE);
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
				sb.append(Constants.DOT);
			}
			sb.append(StringUtil.join(added, Constants.DOT));
		}
		Collection<Type> removed = changes.getRemoved();
		if (removed != null && !removed.isEmpty())
		{
			if (sb.length() > 0)
			{
				sb.append(Constants.DOT);
			}
			sb.append("REMOVE.");
			sb.append(StringUtil.join(removed, Constants.DOT));
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

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

}
