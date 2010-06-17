/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class TypeLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "TYPE";
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		if (value.startsWith(".CLEAR"))
		{
			context.getObjectContext().removeList(cdo, ListKey.TYPE);
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
						"Non-sensical use of .REMOVE.ADD. in " + getTokenName()
							+ ": " + value);
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
						"Non-sensical use of .ADD.REMOVE. in " + getTokenName()
							+ ": " + value);
					return false;
				}
				bRemove = true;
			}
			else if ("CLEAR".equals(aType))
			{
				Logging.errorPrint("Non-sensical use of .CLEAR in "
					+ getTokenName() + ": " + value);
				return false;
			}
			else if (bRemove)
			{
				Type type = Type.getConstant(aType);
				context.getObjectContext().removeFromList(cdo, ListKey.TYPE,
					type);
				bRemove = false;
			}
			else
			{
				Type type = Type.getConstant(aType);
				context.getObjectContext().addToList(cdo, ListKey.TYPE, type);
				bAdd = false;
			}
		}
		if (bRemove)
		{
			Logging.errorPrint(getTokenName()
				+ "ended with REMOVE, so didn't have any Type to remove: "
				+ value);
			return false;
		}
		if (bAdd)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
				+ "ended with ADD, so didn't have any Type to add: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		Changes<Type> changes =
				context.getObjectContext().getListChanges(cdo, ListKey.TYPE);
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
		return new String[]{sb.toString()};
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
