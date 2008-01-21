/*
 * CompanionModLoader.java
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @Created on July 10th, 2002, 3:55 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.text.ParseException;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Loads the level based Mount and Familiar benefits
 * 
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 */
public class CompanionModLoader extends LstLineFileLoader {

	private Map<String, String> sourceMap = null;

	@Override
	public void parseLine(String lstLine, URI sourceURI)
			throws PersistenceLayerException {
		
		if (lstLine.startsWith("SOURCE")) //$NON-NLS-1$
		{
			sourceMap = SourceLoader.parseLine(lstLine, sourceURI);
			return;
		}
		
		CompanionMod cmpMod = new CompanionMod();

		final StringTokenizer colToken = new StringTokenizer(lstLine,
				SystemLoader.TAB_DELIM);

		String name = null;
		cmpMod.setSourceCampaign(getActiveSource().getCampaign());
		cmpMod.setSourceURI(sourceURI);
		
		// Make sure the source info was set
		if (sourceMap != null)
		{
			try
			{
				cmpMod.setSourceMap(sourceMap);
			}
			catch (ParseException e)
			{
				throw new PersistenceLayerException(e.toString());
			}
		}
		
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				CompanionModLstToken.class);
		while (colToken.hasMoreTokens()) {
			String colString = colToken.nextToken().trim();

			// Companion mods don't have a name, but instead start straight into the first token
			if (name == null)
			{
				name = colString;
				cmpMod.setName(name);
			}
			final int idxColon = colString.indexOf(':');
			String key = "";
			try {
				key = colString.substring(0, idxColon);
			} catch (Exception e) {
				throw new PersistenceLayerException();
			}
			CompanionModLstToken token = (CompanionModLstToken) tokenMap
					.get(key);
			if (token != null) {
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, cmpMod, value);
				if (!token.parse(cmpMod, value)) {
					Logging.errorPrint("Error parsing CompanionMod "
							+ cmpMod.getDisplayName() + ':' + sourceURI
							+ ':' + colString + "\"");
				}
			} else if (PObjectLoader.parseTag(cmpMod, colString)) {
				continue;
			} else {
				Logging.addParseMessage(Logging.LST_ERROR,
						"Unrecognized Token in CompanionMod: " + sourceURI
								+ ":" + " \"" + colString + "\"");
			}
		}

		Globals.addCompanionMod(cmpMod);
	}
}
