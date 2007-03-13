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
package plugin.lsttokens.template;

import java.util.Set;

import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with CR Token
 */
public class CrToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "CR";
	}

	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			template.setCR(Integer.parseInt(value));
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			ChallengeRating cr = new ChallengeRating(value);
			context.graph.linkObjectIntoGraph(getTokenName(), template, cr);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> links =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					ChallengeRating.class);
		if (links == null || links.isEmpty())
		{
			return null;
		}
		if (links.size() > 1)
		{
			context
				.addWriteMessage("Only 1 ChallengeRating is allowed per Template");
			return null;
		}
		ChallengeRating cr =
				(ChallengeRating) links.iterator().next().getSinkNodes().get(0);
		return new String[]{cr.toLSTform()};
	}
}
