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
package plugin.lsttokens.race;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;
import pcgen.rules.persistence.token.ErrorParsingWrapper;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with REACH Token
 */
public class ReachToken extends ErrorParsingWrapper<Race> implements CDOMPrimaryParserToken<Race>
{

	public String getTokenName()
	{
		return "REACH";
	}

	public ParseResult parseToken(LoadContext context, Race race, String value)
	{
		try
		{
			Integer i = Integer.valueOf(value);
			if (i.intValue() < 0)
			{
				return new ParseResult.Fail(getTokenName() + " must be an integer >= 0");
			}
			context.getObjectContext().put(race, IntegerKey.REACH, i);
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail("Expected an Integer in Tag: " + value);
		}
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Integer reach = context.getObjectContext().getInteger(race,
				IntegerKey.REACH);
		if (reach == null)
		{
			return null;
		}
		if (reach.intValue() < 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { reach.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}
