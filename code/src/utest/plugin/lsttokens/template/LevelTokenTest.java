/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.template;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.DrLst;
import plugin.lsttokens.SaLst;
import plugin.lsttokens.SrLst;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreLevelWriter;

public class LevelTokenTest extends AbstractTokenTestCase<PCTemplate>
{

	static LevelToken token = new LevelToken();
	static PCTemplateLoader loader = new PCTemplateLoader();

	private static boolean classSetUpFired = false;

	@BeforeClass
	public static final void ltClassSetUp() throws PersistenceLayerException
	{
		TokenRegistration.register(new PreLevelParser());
		TokenRegistration.register(new PreLevelWriter());
		TokenRegistration.register(new CrToken());
		TokenRegistration.register(new DrLst());
		TokenRegistration.register(new SrLst());
		TokenRegistration.register(new SaLst());
		classSetUpFired = true;
	}

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		if (!classSetUpFired)
		{
			ltClassSetUp();
		}
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCTemplate> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputHDonly() throws PersistenceLayerException
	{
		assertFalse(parse("3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputPipe() throws PersistenceLayerException
	{
		assertFalse(parse("3|SR|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOneColon() throws PersistenceLayerException
	{
		assertFalse(parse("3:SR|2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyHD() throws PersistenceLayerException
	{
		assertFalse(parse(":DR:3/+1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptySubtype() throws PersistenceLayerException
	{
		assertFalse(parse("4::3/+1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyDR() throws PersistenceLayerException
	{
		assertFalse(parse("3:DR:"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyDRNoColon()
		throws PersistenceLayerException
	{
		assertFalse(parse("3:DR"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoSlashDR() throws PersistenceLayerException
	{
		assertFalse(parse("3:DR:1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoSlashDR() throws PersistenceLayerException
	{
		assertFalse(parse("3+:DR:1/3/+4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptySR() throws PersistenceLayerException
	{
		assertFalse(parse("3:SR:"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptySA() throws PersistenceLayerException
	{
		assertFalse(parse("3:SA:"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyCR() throws PersistenceLayerException
	{
		assertFalse(parse("3:CR:"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptySRNoColon()
		throws PersistenceLayerException
	{
		assertFalse(parse("3:SR"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptySANoColon()
		throws PersistenceLayerException
	{
		assertFalse(parse("3:SA"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyCRNoColon()
		throws PersistenceLayerException
	{
		assertFalse(parse("3:CR"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoAbbrs() throws PersistenceLayerException
	{
		assertFalse(parse("3:C:3"));
		assertFalse(parse("3:D:1/+2"));
		assertFalse(parse("3:CRA:3"));
		assertFalse(parse("3:DRA:1/+2"));
		assertFalse(parse("3:SAA:Special"));
		assertFalse(parse("3:SRA:1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadClear() throws PersistenceLayerException
	{
		assertFalse(parse(".CLEARSTUFF"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoSpecificClear()
		throws PersistenceLayerException
	{
		assertFalse(parse(".CLEAR.3:CR:3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadHDRangePlus()
		throws PersistenceLayerException
	{
		assertFalse(parse("+3:SA:Special Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadHDRangeMult()
		throws PersistenceLayerException
	{
		assertFalse(parse("*3:SA:Special Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadHDRangeTwoDash()
		throws PersistenceLayerException
	{
		assertFalse(parse("1-3:SA:Special Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadHDRangeEndDash()
		throws PersistenceLayerException
	{
		assertFalse(parse("4-:SA:Special Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadHDRangeEndPlus()
		throws PersistenceLayerException
	{
		assertFalse(parse("4+:SA:Special Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadHDRangeUpTo()
		throws PersistenceLayerException
	{
		assertFalse(parse("-4:SA:Special Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadTemplateToken() throws PersistenceLayerException
	{
		assertFalse(parse("5:CR:-3"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinDR() throws PersistenceLayerException
	{
		runRoundRobin("2:DR:1/+3");
	}

	@Test
	public void testRoundRobinSRNumber() throws PersistenceLayerException
	{
		runRoundRobin("3:SR:25");
	}

	@Test
	public void testRoundRobinSRFormula() throws PersistenceLayerException
	{
		runRoundRobin("3:SR:Formula");
	}

	@Test
	public void testRoundRobinSA() throws PersistenceLayerException
	{
		runRoundRobin("5:SA:Special Ability, Man!");
	}

	@Test
	public void testRoundRobinCRNumber() throws PersistenceLayerException
	{
		runRoundRobin("4:CR:3");
	}

	@Test
	public void testRoundRobinCRFormula() throws PersistenceLayerException
	{
		runRoundRobin("4:CR:Formula");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		runRoundRobin("3:SR:Formula", "4:CR:Formula");
	}

	@Test
	public void testRoundRobinMultipleSame() throws PersistenceLayerException
	{
		runRoundRobin("4:CR:Formula", "4:SA:Special Ability, Man!");
	}

}
