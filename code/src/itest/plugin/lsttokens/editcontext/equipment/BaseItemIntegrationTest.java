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
package plugin.lsttokens.editcontext.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.editcontext.testsupport.AbstractItemIntegrationTestCase;
import plugin.lsttokens.equipment.BaseitemToken;

public class BaseItemIntegrationTest extends
		AbstractItemIntegrationTestCase<Equipment, Equipment>
{

	static BaseitemToken token = new BaseitemToken();
	static EquipmentLoader loader = new EquipmentLoader();

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public LstObjectFileLoader<Equipment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Equipment> getToken()
	{
		return token;
	}

	@Override
	public Class<Equipment> getTargetClass()
	{
		return Equipment.class;
	}

}
