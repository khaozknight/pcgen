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
package plugin.lsttokens.pcclass;

import org.junit.Test;

import pcgen.cdom.enumeration.MapKey;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.AttackType;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class AttackCycleTokenTest extends AbstractTokenTestCase<PCClass>
{

	static AttackcycleToken token = new AttackcycleToken();
	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<PCClass>(
			PCClass.class);

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public CDOMLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoCycle() throws PersistenceLayerException
	{
		assertFalse(parse("BAB"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyCycle() throws PersistenceLayerException
	{
		assertFalse(parse("BAB|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyType() throws PersistenceLayerException
	{
		assertFalse(parse("|4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOpenStart() throws PersistenceLayerException
	{
		assertFalse(parse("|BAB|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOpenEnd() throws PersistenceLayerException
	{
		assertFalse(parse("BAB|4|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNaN() throws PersistenceLayerException
	{
		assertFalse(parse("BAB|x"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegative() throws PersistenceLayerException
	{
		assertFalse(parse("BAB|-2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZero() throws PersistenceLayerException
	{
		assertFalse(parse("BAB|0"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipeTypeOne()
			throws PersistenceLayerException
	{
		assertFalse(parse("BAB||5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipeTypeTwo()
			throws PersistenceLayerException
	{
		assertFalse(parse("BAB|5||UAB|5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipeTypeThree()
			throws PersistenceLayerException
	{
		assertFalse(parse("BAB|5|UAB||4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputGAB() throws PersistenceLayerException
	{
		assertFalse(parse("GAB|5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputGABSecond() throws PersistenceLayerException
	{
		assertFalse(parse("BAB|4|GAB|5"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinBab() throws PersistenceLayerException
	{
		runRoundRobin("BAB|3");
	}

	@Test
	public void testRoundRobinRab() throws PersistenceLayerException
	{
		runRoundRobin("RAB|4");
	}

	@Test
	public void testRoundRobinUab() throws PersistenceLayerException
	{
		runRoundRobin("UAB|5");
	}

	@Test
	public void testRoundRobinMixed() throws PersistenceLayerException
	{
		runRoundRobin("BAB|3|RAB|4|UAB|5");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "BAB|4";
	}

	@Override
	protected String getLegalValue()
	{
		return "BAB|2";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.addToMapFor(MapKey.ATTACK_CYCLE, AttackType.MELEE, null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		primaryProf.addToMapFor(MapKey.ATTACK_CYCLE, AttackType.MELEE, 1);
		expectSingle(getToken().unparse(primaryContext, primaryProf), "BAB|1");
	}

	@Test
	public void testUnparseNegative() throws PersistenceLayerException
	{
		primaryProf.addToMapFor(MapKey.ATTACK_CYCLE, AttackType.MELEE, -2);
		assertBadUnparse();
	}

	@Test
	public void testUnparseZero() throws PersistenceLayerException
	{
		primaryProf.addToMapFor(MapKey.ATTACK_CYCLE, AttackType.MELEE, 0);
		assertBadUnparse();
	}

	@Test
	public void testUnparseMultiple() throws PersistenceLayerException
	{
		primaryProf.addToMapFor(MapKey.ATTACK_CYCLE, AttackType.MELEE, 1);
		primaryProf.addToMapFor(MapKey.ATTACK_CYCLE, AttackType.RANGED, 2);
		expectSingle(getToken().unparse(primaryContext, primaryProf), "BAB|1|RAB|2");
	}

	@Test
	public void testUnparseGrappleNoMelee() throws PersistenceLayerException
	{
		primaryProf.addToMapFor(MapKey.ATTACK_CYCLE, AttackType.GRAPPLE, 1);
		assertBadUnparse();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFailValue() throws PersistenceLayerException
	{
		MapKey mapKey = MapKey.ATTACK_CYCLE;
		primaryProf.addToMapFor(mapKey, AttackType.MELEE, "STR");
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (ClassCastException e)
		{
			//Yep!
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFailKey() throws PersistenceLayerException
	{
		MapKey mapKey = MapKey.ATTACK_CYCLE;
		primaryProf.addToMapFor(mapKey, "STR", 1);
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (ClassCastException e)
		{
			//Yep!
		}
	}
}
