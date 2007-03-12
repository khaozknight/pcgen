package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractStringTokenTestCase;

public class RateOfFireTokenTest extends AbstractStringTokenTestCase<Equipment>
{

	static RateoffireToken token = new RateoffireToken();
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
	public StringKey getStringKey()
	{
		return StringKey.RATE_OF_FIRE;
	}
}
