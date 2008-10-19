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
package plugin.lsttokens.deity;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Deity;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with WORSHIPPERS Token
 */
public class WorshippersToken extends AbstractToken implements
		CDOMPrimaryToken<Deity>
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	@Override
	public String getTokenName()
	{
		return "WORSHIPPERS";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(deity, StringKey.WORSHIPPERS, value);
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		String worshippers = context.getObjectContext().getString(deity,
				StringKey.WORSHIPPERS);
		if (worshippers == null)
		{
			return null;
		}
		return new String[] { worshippers };
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}
